package tsuteto.mcmp.changer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.core.Mcmp1Core;

/**
 * Basically refers to ContainerChest
 */
public class ContainerChanger extends Container
{
    private IInventory inventory;
    private int numRows;

    public ContainerChanger(InventoryPlayer invPlayer, IInventory inventory)
    {
        this.inventory = inventory;
        this.numRows = inventory.getSizeInventory() / 9;

        int var3 = (numRows - 4) * 18;
        int var4;
        int var5;

        // Prepare slots
        for (var4 = 0; var4 < numRows; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new SlotChanger(inventory, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18));
            }
        }

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(invPlayer, var5 + var4 * 9 + 9, 8 + var5 * 18, 120 + var4 * 18 + var3));
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new Slot(invPlayer, var4, 8 + var4 * 18, 178 + var3));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    /**
     * Called to transfer a stack from one inventory to the other
     */
    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var2 = null;
        Slot var3 = (Slot) this.inventorySlots.get(par2);

        if (var3 != null && var3.getHasStack())
        {
            ItemStack var4 = var3.getStack();
            var2 = var4.copy();

            if (par2 < this.numRows * 9)
            {
            	if (var4.getItem() == Mcmp1Core.itemCassetteNormal)
            	{
	                if (!this.mergeItemStack(var4, this.numRows * 9, this.inventorySlots.size(), true))
	                {
	                    return null;
	                }
            	}
            	else
            	{
            		return null;
            	}
            }
            else if (var4.getItem() != Mcmp1Core.itemCassetteNormal || !this.mergeItemStack(var4, 0, this.numRows * 9, false))
            {
        		return null;
            }

            if (var4.stackSize == 0)
            {
                var3.putStack((ItemStack) null);
            }
            else
            {
                var3.onSlotChanged();
            }
        }

        return var2;
    }

}
