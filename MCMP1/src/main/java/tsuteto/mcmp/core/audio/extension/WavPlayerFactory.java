package tsuteto.mcmp.core.audio.extension;

import tsuteto.mcmp.core.audio.McmpPlayerFactory;
import tsuteto.mcmp.core.audio.param.IMcmpSound;

import java.io.File;
import java.io.InputStream;

public class WavPlayerFactory implements McmpPlayerFactory
{
    @Override
    public ExternalAudioPlayer play(InputStream stream, IMcmpSound soundParams) throws Exception
    {
        WavPlayer player = new WavPlayer(stream);
        this.doPlay(player);
        return player;
    }

    public WavPlayer play(final File file, IMcmpSound soundParams) throws Exception
    {
        WavPlayer player = new WavPlayer(file);
        this.doPlay(player);
        return player;
    }

    private void doPlay(final WavPlayer player)
    {
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
    }
}
