package tsuteto.mcmp.core.songselector;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlayerControllerBase;
import tsuteto.mcmp.core.media.IMcmpMedia;
import tsuteto.mcmp.core.util.McmpLog;

public class SongSelectorNext extends SongSelector
{
    StringBuffer trace = new StringBuffer();

    public SongSelectorNext(McmpPlayerControllerBase controller)
    {
        super(controller);
    }

    /**
     * Finds next song in a player inventory
     *
     * @param inventory
     * @return
     */
    @Override
    public ItemStack selectSong(ItemStack[] inventory)
    {
        this.trace = new StringBuffer();

        int slotPlaying = controller.playPos.slotPlaying;
        int playingInStack = controller.playPos.playingInStack;
        int c = inventory.length + 1;

        ItemStack itemstack = inventory[slotPlaying];
        if (itemstack != null && !(itemstack.getItem() instanceof ItemChanger))
        {
            playingInStack++;
        }

        try
        {
            while (c-- >= 0)
            {
                trace.append(slotPlaying);
                itemstack = inventory[slotPlaying];
                if (itemstack != null && playingInStack <= itemstack.stackSize)
                {
                    ItemStack song = pickSongFromItemStack(itemstack);
                    if (song != null)
                    {
                        trace.append("->found");
                        return song;
                    }
                }
                trace.append(",");
                playingInStack = 1;
                slotPlaying++;

                if (slotPlaying == inventory.length)
                {
                    trace.append("--,");
                    slotPlaying = 0;
                }
            }
            trace.append("->n/a");
            return null;

        }
        finally
        {
            controller.playPos.slotPlaying = slotPlaying;
            controller.playPos.playingInStack = playingInStack;
            controller.playPos.changed = true;
            
        	McmpLog.debug(trace.toString());
        }
    }

    @Override
    protected ItemStack findInChanger(InventoryChanger inventory)
    {
        int slotPlaying = inventory.slotPlaying;
        int playingInStack = inventory.playingInStack;
        int invSize = inventory.getSizeInventory();

        try
        {
            ItemStack currItemstack = inventory.getStackInSlot(slotPlaying);
            int stackSize = currItemstack != null ? currItemstack.stackSize : 0;

            if (slotPlaying >= invSize - 1 && playingInStack > stackSize)
            {
                slotPlaying = 0;
                playingInStack = 1;
            }
            else
            {
                playingInStack++;
            }

            for (int c = slotPlaying; c < invSize; c++)
            {
                ItemStack itemstack = inventory.getStackInSlot(c);
                if (itemstack != null && playingInStack <= itemstack.stackSize)
                {
                    Item item = itemstack.getItem();
                    if (item instanceof IMcmpMedia && ((IMcmpMedia)item).getSong(itemstack) != null)
                    {
                        slotPlaying = c;
                        return itemstack;
                    }
                }
                if (c < invSize - 1)
                {
                    playingInStack = 1;
                }
            }
            slotPlaying = invSize - 1;
            return null;

        }
        finally
        {
            inventory.slotPlaying = slotPlaying;
            inventory.playingInStack = playingInStack;
            inventory.updateState();
            trace.append(String.format("-%d(%d)", slotPlaying, playingInStack));
        }
    }
}
