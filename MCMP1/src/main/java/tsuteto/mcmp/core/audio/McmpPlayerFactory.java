package tsuteto.mcmp.core.audio;

import tsuteto.mcmp.core.audio.extension.ExternalAudioPlayer;
import tsuteto.mcmp.core.audio.param.IMcmpSound;

import java.io.File;
import java.io.InputStream;

/**
 * Created by Tsuteto on 15/04/07.
 */
public interface McmpPlayerFactory
{
    ExternalAudioPlayer play(final InputStream stream, IMcmpSound soundParams) throws Exception;
    ExternalAudioPlayer play(final File file, IMcmpSound soundParams) throws Exception;
}
