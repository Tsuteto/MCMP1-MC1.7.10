package tsuteto.mcmp.core.audio.extension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import javazoom.jl.decoder.*;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.JavaSoundAudioDevice;

import java.io.InputStream;

/**
 * Almost copied from javazoom.jl.player.advanced.AdvancedPlayer
 */
@SideOnly(Side.CLIENT)
public class Mp3Player extends AudioPlayerBase
{
    /** The MPEG audio bitstream. */
    private Bitstream bitstream;
    /** The MPEG audio decoder. */
    private Decoder decoder;
    /** The AudioDevice the audio samples are written to. */
    private AudioDevice audio;
    /** Has the player been closed? */
    private boolean closed = false;
    /** Has the player played back all frames from the stream? */
    private boolean complete = false;
    private int lastPosition = 0;

    /**
     * Creates a new <code>Player</code> instance.
     */
    public Mp3Player(InputStream stream) throws Exception
    {
        bitstream = new Bitstream(stream);

        audio = FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder());
        if (audio instanceof JavaSoundAudioDevice)
        {
            ((JavaSoundAudioDevice)audio).setPlayer(this);
        }
    }

    public void play() throws Exception
    {
        play(Integer.MAX_VALUE);
    }

    /**
     * Plays a number of MPEG audio frames.
     *
     * @param frames
     *            The number of frames to play.
     * @return true if the last frame was played, or false if there are more
     *         frames.
     */
    public boolean play(int frames) throws JavaLayerException
    {
        boolean ret = true;

        while (frames-- > 0 && ret)
        {
            ret = decodeFrame();
            this.handleLocking();
        }

        {
            // last frame, ensure all data flushed to the audio device.
            AudioDevice out = audio;
            if (out != null)
            {
                // System.out.println(audio.getPosition());
                out.flush();
                // System.out.println(audio.getPosition());
                synchronized (this)
                {
                    complete = (!closed);
                    close();
                }
            }
        }

        running = false;

        return ret;
    }

    /**
     * Cloases this player. Any audio currently playing is stopped immediately.
     */
    public synchronized void close()
    {
        AudioDevice out = audio;
        if (out != null)
        {
            closed = true;
            audio = null;
            // this may fail, so ensure object state is set up before calling this method.
            out.close();
            lastPosition = out.getPosition();
            try
            {
                bitstream.close();
            }
            catch (BitstreamException ex)
            {}
        }
    }

    /**
     * Decodes a single frame.
     *
     * @return true if there are no more frames to decode, false otherwise.
     */
    protected boolean decodeFrame() throws JavaLayerException
    {
        try
        {
            AudioDevice out = audio;
            if (out == null)
                return false;

            Header h = bitstream.readFrame();
            if (h == null)
                return false;

            // sample buffer set when decoder constructed
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            synchronized (this)
            {
                out = audio;
                if (out != null)
                {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
        }
        catch (RuntimeException ex)
        {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    /**
     * skips over a single frame
     *
     * @return false if there are no more frames to decode, true otherwise.
     */
    protected boolean skipFrame() throws JavaLayerException
    {
        Header h = bitstream.readFrame();
        if (h == null)
            return false;
        bitstream.closeFrame();
        return true;
    }

    /**
     * Plays a range of MPEG audio frames
     *
     * @param start
     *            The first frame to play
     * @param end
     *            The last frame to play
     * @return true if the last frame was played, or false if there are more
     *         frames.
     */
    public boolean play(final int start, final int end) throws JavaLayerException
    {
        boolean ret = true;
        int offset = start;
        while (offset-- > 0 && ret)
            ret = skipFrame();
        return play(end - start);
    }

    // /**
    // * Constructs a <code>PlaybackEvent</code>
    // */
    // private PlaybackEvent createEvent(int id)
    // {
    // return createEvent(audio, id);
    // }

    // /**
    // * Constructs a <code>PlaybackEvent</code>
    // */
    // private PlaybackEvent createEvent(AudioDevice dev, int id)
    // {
    // return new PlaybackEvent(this, id, dev.getPosition());
    // }


}