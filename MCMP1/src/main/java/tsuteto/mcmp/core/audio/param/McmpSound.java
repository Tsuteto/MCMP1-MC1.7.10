package tsuteto.mcmp.core.audio.param;

public class McmpSound extends McmpSoundBase
{
    public static McmpSound audioBgm()
    {
        return new McmpSound(1.0F, 1.0F, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
    }

    public static McmpSound audioPositioned(int x, int y, int z)
    {
        return new McmpSound(4.0F, 1.0F, false, 0, AttenuationType.LINEAR, x + 0.5F, y + 0.5F, z + 0.5F);
    }

    public McmpSound(float volume, float pitch, float x, float y, float z)
    {
        this(volume, pitch, false, 0, AttenuationType.LINEAR, x, y, z);
    }

    private McmpSound(float p_i45108_2_, float p_i45108_3_, boolean p_i45108_4_, int p_i45108_5_, AttenuationType p_i45108_6_, float p_i45108_7_, float p_i45108_8_, float p_i45108_9_)
    {
        this.volume = p_i45108_2_;
        this.pitch = p_i45108_3_;
        this.xPos = p_i45108_7_;
        this.yPos = p_i45108_8_;
        this.zPos = p_i45108_9_;
        this.repeat = p_i45108_4_;
        this.repeatDelay = p_i45108_5_;
        this.attenuationType = p_i45108_6_;
    }
}
