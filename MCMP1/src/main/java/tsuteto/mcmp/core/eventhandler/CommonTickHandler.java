package tsuteto.mcmp.core.eventhandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;

@SideOnly(Side.CLIENT)
public class CommonTickHandler
{

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = FMLClientHandler.instance().getClient();

            if (mc.thePlayer != null)
            {
                for (ItemMcmpPlayer mcmpPlayer : McmpPlayerManager.getPlayerList())
                {
                    if (!mcmpPlayer.inInventory && mcmpPlayer.isPlaying
                            && !mc.thePlayer.inventory.hasItem(mcmpPlayer))
                    {
                        mcmpPlayer.stop(null, mc.thePlayer);
                    } else
                    {
                        mcmpPlayer.inInventory = false;
                    }
                }
            }
            McmpSoundManager.getInstance().updateVolume();
        }
    }
}
