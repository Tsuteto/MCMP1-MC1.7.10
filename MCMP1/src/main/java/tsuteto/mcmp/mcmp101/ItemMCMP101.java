package tsuteto.mcmp.mcmp101;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.songselector.SongSelector;
import tsuteto.mcmp.core.songselector.SongSelectorNext;
import tsuteto.mcmp.core.songselector.SongSelectorPrev;
import tsuteto.mcmp.core.songselector.SongSelectorRandom;
import tsuteto.mcmp.core.songselector.SongSelectorSpecific;

public class ItemMCMP101 extends ItemMcmpPlayer
{
    private SongSelector selectorNext;
    private SongSelector selectorPrev;
    private SongSelector selectorSpecific;

    public boolean hasStartedUp = false;
    public boolean isRepeatPlaying = false;
    public boolean isRandomPlaying = false;

    public ItemMCMP101()
    {
        super();

        selectorSpecific = new SongSelectorSpecific(this);
        setSongSelector();
    }

    /**
     * Action when right-clicking
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        if (itemstack != null)
        {
            player.openGui(MCMP101.instance, 0, player.worldObj, 0, 0, 0);
        }
        return itemstack;
    }

    /**
     * Event on item update
     */
    @Override
    public void onPlayerUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
        if (!(entity instanceof EntityPlayer))
        {
            return;
        }

        boolean sndPlaying = sndMgr.playing();
        EntityPlayer player = (EntityPlayer) entity;

        if (isPlaying && !sndPlaying)
        {
            if (timeInterval > 0)
            {
                timeInterval--;
            }
            else
            {
                ItemStack nextSong = this.goNext(itemstack, player.inventory);
                if (nextSong != null)
                {
                    play(itemstack, player, nextSong);
                }
                else
                {
                    stop(itemstack, player);
                    playPos.slotPlaying = 0;
                    playPos.playingInStack = 0;
                }
                timeInterval = 20;
            }
        }
    }

    public ItemStack getSelectedSong(ItemStack mcmp, InventoryPlayer playerInv)
    {
        return selectorSpecific.selectSongToPlay(mcmp, playerInv);
    }

    public ItemStack goNext(ItemStack mcmp, InventoryPlayer playerInv)
    {
        return selectorNext.selectSongToPlay(mcmp, playerInv);
    }

    public ItemStack goBack(ItemStack mcmp, InventoryPlayer playerInv)
    {
        return selectorPrev.selectSongToPlay(mcmp, playerInv);
    }

    public void setSongSelector()
    {
        if (isRepeatPlaying)
        {
            selectorNext = selectorPrev = new SongSelectorSpecific(this);
        }
        else if (isRandomPlaying)
        {
            selectorNext = selectorPrev = new SongSelectorRandom(this);
        }
        else
        {
            selectorNext = new SongSelectorNext(this);
            selectorPrev = new SongSelectorPrev(this);
        }
    }

    @Override
    public String getSoundOnPlay()
    {
        return "MCMP1.play";
    }

    @Override
    public String getSoundOnStop()
    {
        return "MCMP1.stop";
    }
}
