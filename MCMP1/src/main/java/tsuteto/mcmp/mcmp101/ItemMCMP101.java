package tsuteto.mcmp.mcmp101;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.songselector.*;

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

        selectorSpecific = new SongSelectorSpecific(this.controller);
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

        boolean sndPlaying = this.controller.getAudioPlayer().playing();
        EntityPlayer player = (EntityPlayer) entity;

        if (this.isPlayerPlaying() && !sndPlaying)
        {
            if (this.timeInterval > 0)
            {
                this.timeInterval--;
            }
            else
            {
                ItemStack nextSong = this.goNext(itemstack, player.inventory.mainInventory);
                if (nextSong != null)
                {
                    play(itemstack, player, nextSong);
                }
                else
                {
                    stop(itemstack, player);
                    this.controller.playPos.slotPlaying = 0;
                    this.controller.playPos.playingInStack = 0;
                }
                this.timeInterval = 20;
            }
        }
    }

    public ItemStack getSelectedSong(ItemStack mcmp, ItemStack[] inventory)
    {
        return selectorSpecific.selectSongToPlay(mcmp, inventory);
    }

    public ItemStack goNext(ItemStack mcmp, ItemStack[] inventory)
    {
        return selectorNext.selectSongToPlay(mcmp, inventory);
    }

    public ItemStack goBack(ItemStack mcmp, ItemStack[] inventory)
    {
        return selectorPrev.selectSongToPlay(mcmp, inventory);
    }

    public void setSongSelector()
    {
        if (isRepeatPlaying)
        {
            selectorNext = selectorPrev = new SongSelectorSpecific(this.controller);
        }
        else if (isRandomPlaying)
        {
            selectorNext = selectorPrev = new SongSelectorRandom(this.controller);
        }
        else
        {
            selectorNext = new SongSelectorNext(this.controller);
            selectorPrev = new SongSelectorPrev(this.controller);
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
