package tsuteto.mcmp.mcmps01.midi;

import tsuteto.mcmp.core.audio.McmpPlayerFactory;
import tsuteto.mcmp.core.audio.param.IMcmpSound;
import tsuteto.mcmp.mcmps01.device.McmpSoundDevice;
import tsuteto.mcmp.mcmps01.device.McmpSoundDevicePlaced;
import tsuteto.mcmp.mcmps01.device.McmpSoundDevicePortable;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MidiPlayerFactory implements McmpPlayerFactory
{
    public MidiPlayer play(File mp3, IMcmpSound soundParams) throws Exception
    {
        return play(new BufferedInputStream(new FileInputStream(mp3)), soundParams);
    }

    public MidiPlayer play(final InputStream stream, IMcmpSound soundParams) throws Exception
    {
        McmpSoundDevice device;
        if (soundParams.getAttenuationType() == IMcmpSound.AttenuationType.LINEAR)
        {
            device = new McmpSoundDevicePlaced((int)soundParams.posX(), (int)soundParams.posY(), (int)soundParams.posZ());
        }
        else
        {
            device = new McmpSoundDevicePortable();
        }
        MidiPlayer player = new MidiPlayer(stream, device);
        // Run an internal thread instead
        player.play();
        return player;
    }

}
