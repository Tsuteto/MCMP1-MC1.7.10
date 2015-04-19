package tsuteto.mcmp.core.mcmpplayer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlayerControllerBase;
import tsuteto.mcmp.core.network.AbstractPacket;
import tsuteto.mcmp.core.network.MessageToClient;
import tsuteto.mcmp.core.network.MessageToServer;
import tsuteto.mcmp.core.util.McmpLog;
import tsuteto.mcmp.deck.TileEntityDeck;

/**
 * A packet handler for the channel MCMP player control
 *
 * @author Tsuteto
 *
 */
public class PacketMcmpPlayerBlockCtl extends AbstractPacket implements MessageToServer, MessageToClient
{
    private int type;
    private int slotPlaying;
    private int playingInStack;

    private int x;
    private int y;
    private int z;

    public PacketMcmpPlayerBlockCtl() {}

    public PacketMcmpPlayerBlockCtl(int type, int x, int y, int z)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PacketMcmpPlayerBlockCtl(int type, int slotPlaying, int playingInStack, int x, int y, int z)
    {
        this(type, x, y, z);
        this.slotPlaying = slotPlaying;
        this.playingInStack = playingInStack;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        buffer.writeByte(type);
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeInt(slotPlaying);
        buffer.writeInt(playingInStack);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        type = buffer.readByte();
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        slotPlaying = buffer.readInt();
        playingInStack = buffer.readInt();
    }

    @Override
    public IMessage handleServerSide(EntityPlayer player)
    {
        return this.handleCommon(player);
    }

    @Override
    public IMessage handleClientSide(EntityPlayer player)
    {
        return this.handleCommon(player);
    }


    public IMessage handleCommon(EntityPlayer player)
    {
        TileEntity te = player.worldObj.getTileEntity(x, y, z);

        if (te != null && te instanceof TileEntityDeck)
        {
            TileEntityDeck deck = (TileEntityDeck) te;

            try
            {
                switch (this.type)
                {
                    case McmpPlayerControllerBase.PKT_TYPE_STATE:
                        deck.controller.playPos.slotPlaying = this.slotPlaying;
                        deck.controller.playPos.playingInStack = this.playingInStack;
                        McmpLog.debug(deck.controller.playPos);
                        break;
                    case McmpPlayerControllerBase.PKT_TYPE_PLAY:
                        deck.play();
                        break;
                    case McmpPlayerControllerBase.PKT_TYPE_STOP:
                        deck.stop();
                        break;
                    case McmpPlayerControllerBase.PKT_TYPE_PAUSE:
                        deck.pause();
                        break;
                    case McmpPlayerControllerBase.PKT_TYPE_RESUME:
                        deck.resume();
                        break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }


}
