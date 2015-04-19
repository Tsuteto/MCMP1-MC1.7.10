package tsuteto.mcmp.core.audio.param;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IMcmpSound
{
    boolean canRepeat();
    int getRepeatDelay();
    float getVolume();
    float getPitch();
    float posX();
    float posY();
    float posZ();
    AttenuationType getAttenuationType();

    @SideOnly(Side.CLIENT)
    enum AttenuationType
    {
        NONE,
        LINEAR
    }
}