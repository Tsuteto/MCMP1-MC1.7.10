package tsuteto.mcmp.core.eventhandler;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import tsuteto.mcmp.core.audio.McmpAudioPlayer;
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
                    if (!mcmpPlayer.inInventory && mcmpPlayer.isPlayerPlaying()
                            && !mc.thePlayer.inventory.hasItem(mcmpPlayer))
                    {
                        mcmpPlayer.stop(null, mc.thePlayer);
                    } else
                    {
                        mcmpPlayer.inInventory = false;
                    }
                }
            }

            for (McmpAudioPlayer player : McmpSoundManager.INSTANCE.getAllPlayers())
            {
                player.onTick();
            }
        }
    }

    @SubscribeEvent
    public void renderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            for (McmpAudioPlayer player : McmpSoundManager.INSTANCE.getAllPlayers())
            {
                player.updateSound(mc.thePlayer, event.renderTickTime);
            }
            McmpSoundManager.INSTANCE.removeInactivePlayers();
        }
    }
}
