package tsuteto.mcmp.core.audio.extension;

import com.sun.media.sound.WaveFileReader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.util.McmpLog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@SideOnly(Side.CLIENT)
public class WavPlayer extends AudioPlayerBase
{
    private final int EXTERNAL_BUFFER_SIZE = 8 * 1024;

    public WavPlayer(File in) throws Exception
    {
        this.init(new WaveFileReader().getAudioInputStream(in));
    }

    public WavPlayer(InputStream in) throws Exception
    {
        this.init(new WaveFileReader().getAudioInputStream(in));
    }

    public void play() throws Exception
    {
        try
        {
            line.start();

            int bytesRead = 0;
            byte[] buf = new byte[EXTERNAL_BUFFER_SIZE];
            try
            {
                while (bytesRead != -1)
                {
                    bytesRead = stream.read(buf, 0, buf.length);
                    if (bytesRead >= 0)
                    {
                        line.write(buf, 0, bytesRead);
                    }
                    handleLocking();
                }
            }
            catch (IOException e)
            {
                McmpLog.warn(e, "Failed playing WAV");
            }
        }
        finally
        {
            this.stop();
        }
    }
}