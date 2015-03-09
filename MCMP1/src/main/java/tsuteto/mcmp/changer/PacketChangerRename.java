package tsuteto.mcmp.changer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.core.network.AbstractPacket;
import tsuteto.mcmp.core.network.MessageToServer;

public class PacketChangerRename extends AbstractPacket implements MessageToServer
{
    public byte[] name;

    public PacketChangerRename() {}

    public PacketChangerRename(String name)
    {
        this.name = name.getBytes();
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        buffer.writeInt(name.length);
        buffer.writeBytes(name);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        int len = buffer.readInt();

        name = new byte[len];
        buffer.readBytes(name);

    }

    @Override
    public IMessage handleServerSide(EntityPlayer player)
    {
        String var3 = (new String(name)).trim();

        if (player.getCurrentEquippedItem() != null)
        {
            ItemStack var4 = player.getCurrentEquippedItem();
            InventoryChanger var5 = new InventoryChanger(player, var4, true);
            var5.setChangerName(var3);
            var5.saveInventory();
        }
        return null;
    }
}
