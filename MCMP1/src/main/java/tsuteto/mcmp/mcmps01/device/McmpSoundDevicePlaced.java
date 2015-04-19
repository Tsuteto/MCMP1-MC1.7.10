package tsuteto.mcmp.mcmps01.device;

import net.minecraft.util.ResourceLocation;
import tsuteto.mcmp.core.audio.internal.InternalSound;
import tsuteto.mcmp.mcmps01.core.InternalAudioPlayer;

public class McmpSoundDevicePlaced implements McmpSoundDevice
{
    private float volume = 1.0F;

    private int xCoord;
    private int yCoord;
    private int zCoord;

    public McmpSoundDevicePlaced(int xCoord, int yCoord, int zCoord)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }

    @Override
    public void playSound(String inst, float pitch, float velocity)
    {
        InternalAudioPlayer.soundQueue.offer(InternalSound.block(new ResourceLocation(inst + "_r"), velocity * volume, pitch, xCoord, yCoord, zCoord));
    }

    @Override
    public void setVolume(float volume)
    {
        this.volume = volume;
    }
}
