package tsuteto.mcmp.core.mcmpplayer;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class McmpPlayerManager
{
    private static List<ItemMcmpPlayer> mcmpPlayerList = new ArrayList<ItemMcmpPlayer>();
    private static ItemStack playingPlayer = null;

    public static void registerMcmpPlayer(ItemMcmpPlayer mcmpPlayer)
    {
        mcmpPlayerList.add(mcmpPlayer);
    }

    public static List<ItemMcmpPlayer> getPlayerList()
    {
        return mcmpPlayerList;
    }

    public static ItemStack getActivePlayer()
    {
        return playingPlayer;
    }

    public static void setPlayingPlayer(ItemStack playingPlayer)
    {
        McmpPlayerManager.playingPlayer = playingPlayer;
    }
}
