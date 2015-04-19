package tsuteto.mcmp.core.audio.extension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.audio.McmpPlayerFactory;
import tsuteto.mcmp.core.audio.param.IMcmpSound;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@SideOnly(Side.CLIENT)
public class Mp3PlayerFactory implements McmpPlayerFactory
{
    public Mp3Player play(File mp3, IMcmpSound soundParams) throws Exception
    {
        return play(new BufferedInputStream(new FileInputStream(mp3)), soundParams);
    }

    public Mp3Player play(final InputStream stream, IMcmpSound soundParams) throws Exception
    {
        final Mp3Player player = new Mp3Player(stream);

        Thread playingThread = new Thread() {
            public void run()
            {
                try
                {
                    player.play();
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e.getMessage());
                }
            }
        };
        playingThread.setName("MCMP-1 MP3 Player");
        playingThread.start();
        return player;
    }

}
