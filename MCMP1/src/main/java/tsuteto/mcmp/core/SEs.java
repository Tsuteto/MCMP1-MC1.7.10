package tsuteto.mcmp.core;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

/**
 * Created by luisan on 14/04/29.
 */
public class SEs
{
    public static ISound click()
    {
        return PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0f);
    }
}
