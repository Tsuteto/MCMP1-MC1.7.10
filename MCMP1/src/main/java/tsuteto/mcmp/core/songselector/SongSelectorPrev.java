package tsuteto.mcmp.core.songselector;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.util.McmpLog;

public class SongSelectorPrev extends SongSelector
{
    StringBuffer trace = new StringBuffer();

    public SongSelectorPrev(ItemMcmpPlayer player)
    {
        super(player);
    }

    @Override
    public ItemStack selectSong(InventoryPlayer playerInv)
    {
        this.trace = new StringBuffer();
        ItemStack[] inventory = playerInv.mainInventory;

        int slotPlaying = player.playPos.slotPlaying;
        int playingInStack = player.playPos.playingInStack;

        ItemStack itemstack = inventory[slotPlaying];
        int c = inventory.length + 1;

        if (itemstack != null && !(itemstack.getItem() instanceof ItemChanger))
        {
            playingInStack--;
        }

        try
        {
            while (c-- >= 0)
            {
                trace.append(slotPlaying);
                if (itemstack != null && playingInStack >= 1)
                {
                    ItemStack song = pickSongFromItemStack(itemstack);
                    if (song != null)
                    {
                    	trace.append(",");
                        return song;
                    }
                }
                trace.append(",");
                slotPlaying--;

                if (slotPlaying == -1)
                {
                	trace.append("--");
                    slotPlaying = inventory.length - 1;
                }
                itemstack = inventory[slotPlaying];
                if (itemstack != null)
                {
                    playingInStack = itemstack.stackSize;
                }
            }
            return null;

        }
        finally
        {
            player.playPos.slotPlaying = slotPlaying;
            player.playPos.playingInStack = playingInStack;
            
        	McmpLog.debug(trace.toString());
        }
    }

    @Override
    protected ItemStack findInChanger(InventoryChanger inventory)
    {
        int invSize = inventory.getSizeInventory();
        int slotPlaying = inventory.slotPlaying;
        int playingInStack = inventory.playingInStack;

        try
        {
            if (slotPlaying == 0 && playingInStack < 1)
            {
                slotPlaying = invSize - 1;
                ItemStack itemstack = inventory.getStackInSlot(slotPlaying);
                playingInStack = itemstack != null ? itemstack.stackSize : 1;
            }
            else
            {
                playingInStack--;
            }

            for (int c = Math.min(slotPlaying, invSize - 1); c >= 0; c--)
            {
                ItemStack itemstack = inventory.getStackInSlot(c);
                if (itemstack != null && playingInStack >= 1)
                {
                    Item item = itemstack.getItem();
                    if (item instanceof ItemCassetteTape && ItemCassetteTape.getSong(itemstack) != null)
                    {
                        slotPlaying = c;
                        return itemstack;
                    }
                }
                if (c > 0)
                {
                    itemstack = inventory.getStackInSlot(c - 1);
                    playingInStack = itemstack != null ? itemstack.stackSize : 1;
                }
            }
            slotPlaying = 0;
            playingInStack = 0;
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
