package tsuteto.mcmp.core.audio.extension;

import tsuteto.mcmp.core.util.McmpLog;

import javax.sound.sampled.*;

abstract public class AudioPlayerBase implements ExternalAudioPlayer
{
    protected AudioInputStream stream;
    protected SourceDataLine line;
    protected boolean running = true;
    protected boolean paused = false;
    protected boolean interrupted = false;
    private final Object lock = new Object();

    protected FloatControl volumeControl;
    protected FloatControl panControl;

    protected void init(AudioInputStream stream) throws Exception
    {
        this.stream = stream;

        AudioFormat format = stream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        Line line = AudioSystem.getLine(info);
        this.initAudioLine(line, format);
    }

    public void initAudioLine(Line line, AudioFormat format) throws LineUnavailableException
    {
        if (line instanceof SourceDataLine)
        {
            this.line = (SourceDataLine) line;
            this.line.open(format);
            this.initAudioController();
        }
        else
        {
            throw new IllegalArgumentException("Failed to play audio. Unsupported Data Line: " + line.toString());
        }
    }

    protected void initAudioController()
    {
        if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
        {
            volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
        }
        else if (line.isControlSupported(FloatControl.Type.VOLUME))
        {
            volumeControl = (FloatControl) line.getControl(FloatControl.Type.VOLUME);
        }
        else
        {
            McmpLog.warn("Unable to control volume");
        }

        if (line.isControlSupported(FloatControl.Type.PAN))
        {
            panControl = (FloatControl) line.getControl(FloatControl.Type.PAN);
        }
        else
        {
            McmpLog.warn("Unable to control pan");
        }
    }

    public void handleLocking()
    {
        synchronized (this.lock)
        {
            this.lock.notifyAll();
            while (this.paused && !this.interrupted)
            {
                try
                {
                    this.lock.wait();
                }
                catch (InterruptedException ignored) {}
            }
        }
    }

    public boolean playing()
    {
        return running;
    }

    public boolean paused()
    {
        return this.paused;
    }

    public synchronized void close()
    {
        synchronized (this.lock)
        {
            this.interrupted = true;
            this.lock.notifyAll();
        }

        if (line != null)
        {
            line.stop();
            line.close();
        }
        this.running = false;
        this.paused = false;
    }

    @Override
    public void pause()
    {
        synchronized (this.lock)
        {
            this.paused = true;
        }
        line.stop();
    }

    @Override
    public void resume()
    {
        line.start();
        synchronized (this.lock)
        {
            this.paused = false;
            this.lock.notifyAll();
        }
    }

    public void stop()
    {
        close();
    }

    /*
     * Sound control
     */

    public void setGain(float volume)
    {
        float volumeLinear = 20 * (float)Math.log(volume);
        if (volumeControl != null)
        {
            if (volumeLinear > volumeControl.getMinimum())
            {
                volumeControl.setValue(volumeLinear);
            }
            else
            {
                volumeControl.setValue(volumeControl.getMinimum());
            }
        }
    }

    // -1.0 (L) to 1.0 (R)
    public void setPan(float ratio)
    {
        if (panControl != null)
        {
            panControl.setValue(ratio);
        }
    }
}
