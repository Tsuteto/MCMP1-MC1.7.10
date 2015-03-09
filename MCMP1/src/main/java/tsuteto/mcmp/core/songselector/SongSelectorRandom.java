package tsuteto.mcmp.core.songselector;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;

public class SongSelectorRandom extends SongSelector
{

    public SongSelectorRandom(ItemMcmpPlayer player)
    {
        super(player);
    }

    @Override
    public ItemStack selectSong(InventoryPlayer playerInv)
    {
        ItemStack[] inventory = playerInv.mainInventory;
        int slotPlaying = 0;
        int playingInStack = 1;

        int numSongs = 0;
        for (ItemStack itemstack : inventory)
        {
            numSongs += countSongsInItemStack(itemstack);
        }

        if (numSongs == 0)
            return null;

        int songNo = player.playerRand.nextInt(numSongs) + 1;

        try
        {
            for (int i = 0; i < inventory.length; i++)
            {
                ItemStack itemstack = inventory[i];
                if (itemstack != null)
                {
                    songNo -= countSongsInItemStack(itemstack);
                    if (songNo <= 0)
                    {
                        Item item = itemstack.getItem();
                        if (item instanceof ItemChanger)
                        {
                            playingInStack = 1;
                        }
                        else
                        {
                            playingInStack = itemstack.stackSize + songNo;
                        }
                        slotPlaying = i;
                        return pickSongFromItemStack(itemstack);
                    }
                }
            }
            System.out.println("[MCMP-1] Failed to pick a song randomly");
            return null;

        }
        finally
        {
            player.playPos.slotPlaying = slotPlaying;
            player.playPos.playingInStack = playingInStack;
        }
    }

    @Override
    protected ItemStack findInChanger(InventoryChanger inventory)
    {
        int slotPlaying = 0;
        int playingInStack = 1;

        int invSize = inventory.getSizeInventory();
        int songNum = inventory.getNumSongsInside();
        if (songNum == 0)
            return null;

        int songNo = player.playerRand.nextInt(inventory.getNumSongsInside()) + 1;

        try
        {
            for (int i = 0; i < invSize; i++)
            {
                ItemStack itemstack = inventory.getStackInSlot(i);
                if (itemstack != null)
                {
                    songNo -= countSongsInItemStack(itemstack);
                    if (songNo <= 0)
                    {
                        slotPlaying = i;
                        playingInStack = itemstack.stackSize + songNo;
                        Item item = itemstack.getItem();
                        if (item instanceof ItemCassetteTape && ItemCassetteTape.getSong(itemstack) != null)
                        {
                            return itemstack;
                        }
                    }
                }
            }
            System.out.println("[MCMP-1] Failed to pick a song randomly");
            return null;

        }
        finally
        {
            inventory.slotPlaying = slotPlaying;
            inventory.playingInStack = playingInStack;
            inventory.updateState();
        }
    }

}
