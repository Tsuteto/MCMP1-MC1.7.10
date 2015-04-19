package tsuteto.mcmp.core.songselector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlayerControllerBase;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPortablePlayerController;
import tsuteto.mcmp.core.media.IMcmpMedia;

/**
 * Handles song selection for player
 */
public abstract class SongSelector
{
    protected final McmpPlayerControllerBase controller;

    public SongSelector(McmpPlayerControllerBase controller)
    {
        this.controller = controller;
    }

    public ItemStack selectSongToPlay(ItemStack stack, ItemStack[] inventory)
    {
        // TODO: This is sort of stupid
        if (controller instanceof McmpPortablePlayerController)
        {
            ItemStack song = selectSong(inventory);
            ((McmpPortablePlayerController)controller).updatePlayerState(stack);
            return song;
        }
        return null;
    }

    public ItemStack selectSongToPlay(NBTTagCompound nbt, ItemStack[] inventory)
    {
        ItemStack song = selectSong(inventory);
        controller.writeToNBT(nbt);
        return song;
    }

    abstract public ItemStack selectSong(ItemStack[] inventory);

    abstract protected ItemStack findInChanger(InventoryChanger inventory);

    protected ItemStack pickSongFromItemStack(ItemStack itemstack)
    {
        if (itemstack == null)
            return null;

        Item item = itemstack.getItem();

        if (item instanceof ItemRecord)
        {
            return itemstack;
        }
        else if (item instanceof ItemChanger)
        {
            ItemStack changerSong = findInChanger(new InventoryChanger(itemstack));
            if (changerSong != null)
                return changerSong;
        }
        else if (item instanceof IMcmpMedia && ((IMcmpMedia)item).getSong(itemstack) != null)
        {
            return itemstack;
        }
        return null;
    }

    protected int countSongsInItemStack(ItemStack itemstack)
    {
        if (itemstack == null)
            return 0;

        Item item = itemstack.getItem();

        if (item instanceof ItemRecord)
        {
            return itemstack.stackSize;
        }
        else if (item instanceof ItemChanger)
        {
            return new InventoryChanger(itemstack).getNumSongsInside();
        }
        else if (item instanceof IMcmpMedia && ((IMcmpMedia)item).getSong(itemstack) != null)
        {
            return itemstack.stackSize;
        }
        return 0;
    }
}
