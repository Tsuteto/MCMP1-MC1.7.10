package tsuteto.mcmp.core.audio.extension;

import net.sourceforge.jaad.aac.AACException;
import net.sourceforge.jaad.aac.Decoder;
import net.sourceforge.jaad.aac.SampleBuffer;
import net.sourceforge.jaad.mp4.MP4Container;
import net.sourceforge.jaad.mp4.api.AudioTrack;
import net.sourceforge.jaad.mp4.api.Frame;
import net.sourceforge.jaad.mp4.api.Movie;
import tsuteto.mcmp.core.util.McmpLog;

import javax.sound.sampled.*;
import java.io.InputStream;
import java.util.List;

public class AacPlayer implements ExternalAudioPlayer
{
    public boolean playing = false;

    private AudioTrack track;
    private SourceDataLine line = null;
    private FloatControl volumeControl;

    public AacPlayer(InputStream in) throws Exception
    {
        MP4Container cont = new MP4Container(in);
        Movie movie = cont.getMovie();
        List tracks = movie.getTracks(AudioTrack.AudioCodec.AAC);
        if (tracks.isEmpty())
        {
            throw new IllegalArgumentException(" does not contain any AAC track");
        }

        this.track = (AudioTrack) tracks.get(0);
        AudioFormat aufmt = new AudioFormat((float) track.getSampleRate(), track.getSampleSize(), track.getChannelCount(), true, true);
        Line line = AudioSystem.getLine(getSourceLineInfo(aufmt));

        if (line instanceof SourceDataLine)
        {
            this.line = (SourceDataLine) line;
            this.line.open(aufmt);

            if (this.line.isControlSupported(FloatControl.Type.MASTER_GAIN))
            {
                volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
            }
            else if (this.line.isControlSupported(FloatControl.Type.VOLUME))
            {
                volumeControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
            }
            else
            {
                McmpLog.warn("AAC Player: Unable to control the volume");
            }
        }
    }

    protected DataLine.Info getSourceLineInfo(AudioFormat aufmt)
    {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, aufmt);
        return info;
    }

    public void play() throws Exception
    {
        try
        {
            playing = true;
            line.start();
            Decoder dec = new Decoder(track.getDecoderSpecificInfo());
            SampleBuffer buf = new SampleBuffer();

            try
            {
                while (line.isOpen() && track.hasMoreFrames())
                {
                    Frame frame = track.readNextFrame();

                    dec.decodeFrame(frame.getData(), buf);

                    byte[] b = buf.getData();
                    line.write(b, 0, b.length);
                }
            }
            catch (AACException var15)
            {
                McmpLog.warn(var15, "AAC decoding error");
            }
        }
        finally
        {
            this.stop();
        }

    }

    public void stop()
    {
        if (line != null)
        {
            line.stop();
            line.close();
        }
        playing = false;
    }

    public boolean playing()
    {
        return playing;
    }

    public void setVolume(float volume)
    {
        float volumeDb = 20 * (float)Math.log(volume);
        if (volumeControl != null)
        {
            if (volumeDb > volumeControl.getMinimum())
            {
                volumeControl.setValue(volumeDb);
            }
            else
            {
                volumeControl.setValue(volumeControl.getMinimum());
            }
        }
    }

}
