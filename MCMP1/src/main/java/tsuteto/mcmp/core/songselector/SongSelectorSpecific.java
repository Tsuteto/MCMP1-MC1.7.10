package tsuteto.mcmp.core.songselector;

import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPortablePlayerController;
import tsuteto.mcmp.core.media.IMcmpMedia;

public class SongSelectorSpecific extends SongSelector
{

    public SongSelectorSpecific(McmpPortablePlayerController controller)
    {
        super(controller);
    }

    @Override
    public ItemStack selectSong(ItemStack[] inventory)
    {
        ItemStack itemstack = inventory[MathHelper.clamp_int(controller.playPos.slotPlaying, 0, inventory.length - 1)];
        return pickSongFromItemStack(itemstack);
    }

    @Override
    protected ItemStack findInChanger(InventoryChanger inventory)
    {
        ItemStack itemstack = inventory.getStackInSlot(inventory.slotPlaying);
        if (itemstack != null && itemstack.getItem() instanceof IMcmpMedia
                && ((IMcmpMedia) itemstack.getItem()).getSong(itemstack) != null)
        {
            return itemstack;
        }
        else
        {
            return null;
        }
    }

}
