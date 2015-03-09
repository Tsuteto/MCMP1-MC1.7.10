package tsuteto.mcmp.recorder;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tsuteto.mcmp.cassettetape.ItemCassetteTape.Source;
import tsuteto.mcmp.core.network.AbstractPacket;
import tsuteto.mcmp.core.network.MessageToServer;

public class PacketRecorderCtl extends AbstractPacket implements MessageToServer
{
    private int x, y, z;
    private byte inputSourceId;
    private int selectedRow;

    public PacketRecorderCtl() {}

    public PacketRecorderCtl(int x, int y, int z, Source inputSource, int selectedRow)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.inputSourceId = (byte)inputSource.ordinal();
        this.selectedRow = selectedRow;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        buffer.writeInt(x);
        buffer.writeInt(y);
        buffer.writeInt(z);
        buffer.writeByte(inputSourceId);
        buffer.writeInt(selectedRow);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        x = buffer.readInt();
        y = buffer.readInt();
        z = buffer.readInt();
        inputSourceId = buffer.readByte();
        selectedRow = buffer.readInt();
    }

    @Override
    public IMessage handleServerSide(EntityPlayer player)
    {
        World world = player.worldObj;
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity instanceof TileEntityRecorder)
        {
            TileEntityRecorder tileEntityRecorder = (TileEntityRecorder)tileEntity;
            tileEntityRecorder.setInputSource(Source.values()[inputSourceId]);
            tileEntityRecorder.setSonglistRowSelected(selectedRow);
        }
        return null;
    }
}
