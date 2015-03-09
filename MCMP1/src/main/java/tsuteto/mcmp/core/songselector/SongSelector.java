package tsuteto.mcmp.core.songselector;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;

public abstract class SongSelector
{
    protected final ItemMcmpPlayer player;

    public SongSelector(ItemMcmpPlayer player)
    {
        this.player = player;
    }

    public ItemStack selectSongToPlay(ItemStack mcmp, InventoryPlayer playerInv)
    {
        player.loadPlayerData(mcmp);
        ItemStack song = selectSong(playerInv);

        player.updatePlayerState(mcmp);
        return song;
    }

    abstract public ItemStack selectSong(InventoryPlayer playerInv);

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
        else if (item instanceof ItemCassetteTape && ItemCassetteTape.getSong(itemstack) != null)
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
        else if (item instanceof ItemCassetteTape && ItemCassetteTape.getSong(itemstack) != null)
        {
            return itemstack.stackSize;
        }
        return 0;
    }
}
