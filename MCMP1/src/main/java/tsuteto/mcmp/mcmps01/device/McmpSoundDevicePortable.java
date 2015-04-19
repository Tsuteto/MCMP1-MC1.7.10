package tsuteto.mcmp.mcmps01.device;

import net.minecraft.util.ResourceLocation;
import tsuteto.mcmp.core.audio.internal.InternalSound;
import tsuteto.mcmp.mcmps01.core.InternalAudioPlayer;

public class McmpSoundDevicePortable implements McmpSoundDevice
{
    private float volume = 1.0F;

    @Override
    public void playSound(String inst, float pitch, float velocity)
    {
        InternalAudioPlayer.soundQueue.offer(InternalSound.bgm(new ResourceLocation(inst), velocity * volume, pitch));
    }

    @Override
    public void setVolume(float volume)
    {
        this.volume = volume;
    }
}
