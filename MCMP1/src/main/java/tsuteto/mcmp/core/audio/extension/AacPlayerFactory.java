package tsuteto.mcmp.core.audio.extension;

import tsuteto.mcmp.core.audio.McmpPlayerFactory;
import tsuteto.mcmp.core.audio.param.IMcmpSound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AacPlayerFactory implements McmpPlayerFactory
{
    public AacPlayer play(File mp4, IMcmpSound soundParams) throws Exception
    {
        return play(new BufferedInputStream(new FileInputStream(mp4)), soundParams);
    }

    public AacPlayer play(final InputStream stream, IMcmpSound soundParams) throws Exception
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
