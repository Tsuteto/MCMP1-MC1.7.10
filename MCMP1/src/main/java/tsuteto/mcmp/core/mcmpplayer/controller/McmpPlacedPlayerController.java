package tsuteto.mcmp.core.mcmpplayer.controller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tsuteto.mcmp.core.audio.param.McmpSound;
import tsuteto.mcmp.core.mcmpplayer.PacketMcmpPlayerBlockCtl;
import tsuteto.mcmp.core.network.PacketDispatcher;

public class McmpPlacedPlayerController extends McmpPlayerControllerBase
{
    public int xCoord;
    public int yCoord;
    public int zCoord;

    public McmpPlacedPlayerController(int xCoord, int yCoord, int zCoord)
    {
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
    }

    public void playAtPosition(World world, EntityPlayer player, ItemStack song)
    {
        this.play(world, player, song, McmpSound.audioPositioned(xCoord, yCoord, zCoord));
    }

    public PacketDispatcher createPlayerCtlPacket(final int type)
    {
        if (type == McmpPlayerControllerBase.PKT_TYPE_STATE)
        {
            return PacketDispatcher.packet(new PacketMcmpPlayerBlockCtl(
                    type, xCoord, yCoord, zCoord, playPos.slotPlaying, playPos.playingInStack));
        }
        else
        {
            return PacketDispatcher.packet(new PacketMcmpPlayerBlockCtl(type, xCoord, yCoord, zCoord));
        }
    }
}
