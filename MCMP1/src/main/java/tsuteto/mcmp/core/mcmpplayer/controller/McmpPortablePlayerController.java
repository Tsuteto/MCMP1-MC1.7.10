package tsuteto.mcmp.core.mcmpplayer.controller;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tsuteto.mcmp.core.audio.param.McmpSound;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;
import tsuteto.mcmp.core.mcmpplayer.PacketMcmpPlayerCtl;
import tsuteto.mcmp.core.network.PacketDispatcher;

/**
 * MCMP Player implementation
 */
public class McmpPortablePlayerController extends McmpPlayerControllerBase
{
    public void play(World world, ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
        this.play(world, player, song, McmpSound.audioBgm());
        if (this.isPlayerPlaying)
        {
            McmpPlayerManager.setPlayingPlayer(mcmp);
        }
    }

    @Override
    public void stop(World world)
    {
        super.stop(world);
        if (world.isRemote)
        {
            McmpPlayerManager.setPlayingPlayer(null);
        }
    }

    public PacketDispatcher createPlayerCtlPacket(final int type)
    {
        if (type == McmpPlayerControllerBase.PKT_TYPE_STATE)
        {
            return PacketDispatcher.packet(new PacketMcmpPlayerCtl(type, playPos.slotPlaying, playPos.playingInStack));
        }
        else
        {
            return PacketDispatcher.packet(new PacketMcmpPlayerCtl(type));
        }
    }

    public void loadPlayerData(ItemStack mcmp)
    {
        this.readFromNBT(mcmp.getTagCompound());
    }

    public void savePlayerData(ItemStack mcmp)
    {
        if (mcmp.getTagCompound() == null)
        {
            mcmp.setTagCompound(new NBTTagCompound());
        }
        this.writeToNBT(mcmp.getTagCompound());
    }

    public void updatePlayerState(ItemStack mcmp)
    {
        if (playPos.changed)
        {
            this.updatePlayerState();
            this.savePlayerData(mcmp);
        }
    }
}
