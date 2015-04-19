package tsuteto.mcmp.core.songselector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPortablePlayerController;
import tsuteto.mcmp.core.media.IMcmpMedia;

public class SongSelectorRandom extends SongSelector
{

    public SongSelectorRandom(McmpPortablePlayerController controller)
    {
        super(controller);
    }

    @Override
    public ItemStack selectSong(ItemStack[] inventory)
    {
        int slotPlaying = 0;
        int playingInStack = 1;

        int numSongs = 0;
        for (ItemStack itemstack : inventory)
        {
            numSongs += countSongsInItemStack(itemstack);
        }

        if (numSongs == 0)
            return null;

        int songNo = controller.rand.nextInt(numSongs) + 1;

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
            controller.playPos.slotPlaying = slotPlaying;
            controller.playPos.playingInStack = playingInStack;
            controller.playPos.changed = true;
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

        int songNo = controller.rand.nextInt(inventory.getNumSongsInside()) + 1;

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
                        if (item instanceof IMcmpMedia && ((IMcmpMedia)item).getSong(itemstack) != null)
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
