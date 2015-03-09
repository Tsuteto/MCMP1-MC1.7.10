package tsuteto.mcmp.core.eventhandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import tsuteto.mcmp.core.Mcmp1Core;

@SideOnly(Side.CLIENT)
public class PlayerTickHandler
{
    private Mcmp1Core mod;

    public PlayerTickHandler(Mcmp1Core mod)
    {
        this.mod = mod;
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event)
    {
        if (event.type == TickEvent.Type.CLIENT && event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            if (mc.currentScreen != null)
            {
                mod.onTickInGUI(mc, mc.currentScreen);
            }
        }
    }

}
