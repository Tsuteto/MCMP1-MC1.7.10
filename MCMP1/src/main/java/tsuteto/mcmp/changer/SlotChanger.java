package tsuteto.mcmp.changer;

import tsuteto.mcmp.core.Mcmp1Core;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotChanger extends Slot {

	public SlotChanger(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}

    @Override
    public boolean isItemValid(ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem() == Mcmp1Core.itemCassetteNormal;
    }
}
