package tsuteto.mcmp.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tsuteto.mcmp.changer.ContainerChanger;
import tsuteto.mcmp.changer.GuiChanger;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.recorder.ContainerRecorder;
import tsuteto.mcmp.recorder.GuiRecorder;
import tsuteto.mcmp.recorder.TileEntityRecorder;
import cpw.mods.fml.common.network.IGuiHandler;

public class McmpGuiHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiId == 0)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityRecorder)
            {
                return new ContainerRecorder(player.inventory, (TileEntityRecorder) tile);
            }
        }
        else if (guiId == 1)
        {
            ItemStack itemstack = player.getCurrentEquippedItem();
            if (itemstack.getItem() == Mcmp1Core.itemChanger)
            {
                return new ContainerChanger(player.inventory, new InventoryChanger(player, itemstack, true));
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
        if (guiId == 0)
        {
            TileEntity tile = world.getTileEntity(x, y, z);
            if (tile instanceof TileEntityRecorder)
            {
                return new GuiRecorder(player.inventory, (TileEntityRecorder) tile);
            }
        }
        else if (guiId == 1)
        {
            ItemStack itemstack = player.getCurrentEquippedItem();
            if (itemstack.getItem() == Mcmp1Core.itemChanger)
            {
                return new GuiChanger(player.inventory, new InventoryChanger(player, itemstack, true));
            }
        }
        return null;
    }
}
