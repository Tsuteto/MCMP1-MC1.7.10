package tsuteto.mcmp.core.eventhandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraftforge.client.event.GuiOpenEvent;
import tsuteto.mcmp.core.audio.McmpSoundManager;

/**
 * Created by Tsuteto on 15/04/16.
 */
public class GuiEvent
{
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.gui == null)
        {
            McmpSoundManager.INSTANCE.resumeAllSounds();
        }
        else if (event.gui.getClass() == GuiIngameMenu.class)
        {
            McmpSoundManager.INSTANCE.pauseAllSounds();
        }
    }
}
