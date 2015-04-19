package tsuteto.mcmp.changer;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.core.network.AbstractPacket;
import tsuteto.mcmp.core.network.MessageToServer;
import tsuteto.mcmp.core.util.McmpLog;

public class PacketChangerState extends AbstractPacket implements MessageToServer
{
    private boolean isPlayerControl;
    private int locChanger;
    private int slotPlaying;
    private int playingInStack;

    public PacketChangerState() {}

    public PacketChangerState(boolean isPlayerControl, int locChanger, int slotPlaying, int playingInStack)
    {
        this.isPlayerControl = isPlayerControl;
        this.locChanger = locChanger;
        this.slotPlaying = slotPlaying;
        this.playingInStack = playingInStack;
    }

    @Override
    public void encodeInto(ByteBuf buffer)
    {
        buffer.writeBoolean(isPlayerControl);
        buffer.writeInt(locChanger);
        buffer.writeInt(slotPlaying);
        buffer.writeInt(playingInStack);
    }

    @Override
    public void decodeInto(ByteBuf buffer)
    {
        isPlayerControl = buffer.readBoolean();
        locChanger = buffer.readInt();
        slotPlaying = buffer.readInt();
        playingInStack = buffer.readInt();
    }

    @Override
    public IMessage handleServerSide(EntityPlayer player)
    {
        ItemStack changer = player.inventory.getStackInSlot(locChanger);
        if (changer != null && changer.getItem() instanceof ItemChanger)
        {
            try
            {
                InventoryChanger inv = new InventoryChanger(player, changer, isPlayerControl);
                inv.slotPlaying = slotPlaying;
                inv.playingInStack = playingInStack;
                inv.saveInventory();
                McmpLog.debug("CHANGER-POS(%d, %d)", slotPlaying, playingInStack);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
