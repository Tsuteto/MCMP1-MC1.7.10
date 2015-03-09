package tsuteto.mcmp.core.audio.extension;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AacPlayerFactory
{
    public static AacPlayer playAac(File mp4) throws Exception
    {
        return playAac(new BufferedInputStream(new FileInputStream(mp4)));
    }

    public static AacPlayer playAac(final InputStream stream) throws Exception
    {
        final AacPlayer player = new AacPlayer(stream);

        Thread playingThread = new Thread() {
            public void run()
            {
                try
                {
                    player.play();
                }
                catch (Exception e)
                {
                    throw new IllegalStateException(e);
                }
            }
        };
        playingThread.setName("MCMP-1 AAC Player");
        playingThread.start();
        return player;
    }
}
