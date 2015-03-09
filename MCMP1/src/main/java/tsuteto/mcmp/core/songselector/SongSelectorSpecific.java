package tsuteto.mcmp.core.songselector;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;

public class SongSelectorSpecific extends SongSelector
{

    public SongSelectorSpecific(ItemMcmpPlayer player)
    {
        super(player);
    }

    @Override
    public ItemStack selectSong(InventoryPlayer playerInv)
    {
        ItemStack itemstack = playerInv.mainInventory[player.playPos.slotPlaying];
        return pickSongFromItemStack(itemstack);
    }

    @Override
    protected ItemStack findInChanger(InventoryChanger inventory)
    {
        ItemStack itemstack = inventory.getStackInSlot(inventory.slotPlaying);
        if (itemstack != null && itemstack.getItem() instanceof ItemCassetteTape
                && ItemCassetteTape.getSong(itemstack) != null)
        {
            return itemstack;
        }
        else
        {
            return null;
        }
    }

}
