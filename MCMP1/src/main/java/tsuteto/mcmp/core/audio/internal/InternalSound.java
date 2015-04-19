package tsuteto.mcmp.core.audio.internal;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.ResourceLocation;

public class InternalSound extends PositionedSound
{
    public static InternalSound bgm(ResourceLocation p_147674_0_, float volume, float pitch)
    {
        return new InternalSound(p_147674_0_, volume, pitch, false, 0, AttenuationType.NONE, 0.0F, 0.0F, 0.0F);
    }

    public static InternalSound block(ResourceLocation p_147674_0_, float volume, float pitch, int x, int y, int z)
    {
        return new InternalSound(p_147674_0_, volume, pitch, false, 0, AttenuationType.LINEAR, ((float)x) + 0.5F, ((float)y) + 0.5F, ((float)z) + 0.5F);
    }

    private InternalSound(ResourceLocation p_i45108_1_, float p_i45108_2_, float p_i45108_3_, boolean p_i45108_4_, int p_i45108_5_, ISound.AttenuationType p_i45108_6_, float p_i45108_7_, float p_i45108_8_, float p_i45108_9_)
    {
        super(p_i45108_1_);
        this.volume = p_i45108_2_;
        this.field_147663_c = p_i45108_3_;
        this.xPosF = p_i45108_7_;
        this.yPosF = p_i45108_8_;
        this.zPosF = p_i45108_9_;
        this.repeat = p_i45108_4_;
        this.field_147665_h = p_i45108_5_;
        this.field_147666_i = p_i45108_6_;
    }

}
