package tsuteto.mcmp.core.audio.extension;

import java.io.File;

public class WavPlayerFactory
{
//    public static WavPlayer playWav(File wav) throws Exception
//    {
//        return playWav(new BufferedInputStream(new FileInputStream(wav)));
//    }

    public static WavPlayer playWav(final File stream) throws Exception
    {
        final WavPlayer player = new WavPlayer(stream);

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
        playingThread.setName("MCMP-1 WAV Player");
        playingThread.start();
        return player;
    }
}
