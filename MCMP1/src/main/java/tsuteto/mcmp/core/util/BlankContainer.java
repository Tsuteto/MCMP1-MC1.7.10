package tsuteto.mcmp.core.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class BlankContainer extends Container
{
    private IInventory inventory;

    public BlankContainer(InventoryPlayer invPlayer, IInventory inventory)
    {
        this.inventory = inventory;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public void putStackInSlot(int par1, ItemStack par2ItemStack)
    {
    }
}
