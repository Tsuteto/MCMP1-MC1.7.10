package tsuteto.mcmp.core.mcmpplayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.core.network.AbstractPacket;
import tsuteto.mcmp.core.network.MessageToServer;
import tsuteto.mcmp.core.util.McmpLog;

/**
 * A packet handler for the channel MCMP player control
 *
 * @author Tsuteto
 *
 */
public class PacketMcmpPlayerCtl extends AbstractPacket implements MessageToServer
{
    private int type;
    private int slotPlaying;
    private int playingInStack;
    private ByteBuf byteBuf;

    public PacketMcmpPlayerCtl() {}

    public PacketMcmpPlayerCtl(int type)
    {
        this.type = type;
    }

    public PacketMcmpPlayerCtl(int type, int slotPlaying, int playingInStack)
    {
        this.type = type;
        this.slotPlaying = slotPlaying;
        this.playingInStack = playingInStack;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        buffer.writeByte(type);
        buffer.writeInt(slotPlaying);
        buffer.writeInt(playingInStack);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        type = buffer.readByte();
        slotPlaying = buffer.readInt();
        playingInStack = buffer.readInt();
        this.byteBuf = buffer;
    }

    @Override
    public IMessage handleServerSide(EntityPlayer player)
    {
        ItemStack itemstack = McmpPlayerManager.getActivePlayer();

        if (itemstack != null && itemstack.getItem() instanceof ItemMcmpPlayer)
        {
            ItemMcmpPlayer mcmpPlayer = (ItemMcmpPlayer) itemstack.getItem();

            try
            {
                switch (this.type)
                {
                    case ItemMcmpPlayer.PKT_TYPE_STATE:
                        mcmpPlayer.playPos.slotPlaying = this.slotPlaying;
                        mcmpPlayer.playPos.playingInStack = this.playingInStack;
                        McmpLog.debug(mcmpPlayer.playPos);
                        break;
                    case ItemMcmpPlayer.PKT_TYPE_PLAY:
                        mcmpPlayer.play(itemstack, player, null);
                        break;
                    case ItemMcmpPlayer.PKT_TYPE_STOP:
                        mcmpPlayer.stop(itemstack, player);
                        break;
                }
                mcmpPlayer.receiveAdditionalCtlPacketData(type, byteBuf);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
