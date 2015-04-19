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

public class AacPlayer extends AudioPlayerBase
{
    private AudioTrack track;

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
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, aufmt);
        Line line = AudioSystem.getLine(info);
        this.initAudioLine(line, aufmt);
    }

    public void play() throws Exception
    {
        try
        {
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
                    this.handleLocking();
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

    @Override
    public synchronized void close()
    {
        super.close();
    }
}
