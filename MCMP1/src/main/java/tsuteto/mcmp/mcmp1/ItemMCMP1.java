package tsuteto.mcmp.mcmp1;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.songselector.SongSelector;
import tsuteto.mcmp.core.songselector.SongSelectorRandom;
import tsuteto.mcmp.core.util.McmpLog;

public class ItemMCMP1 extends ItemMcmpPlayer
{
    private IIcon iconStopped;
    private IIcon iconPlaying;

    private SongSelector playAction;

    public ItemMCMP1()
    {
        super();
        playAction = new SongSelectorRandom(this.controller);
    }

    /**
     * Action when right-clicking
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
    {
        McmpLog.debug("playing: %s on %s", this.isPlayerPlaying(), player.worldObj.isRemote ? "client" : "server");

        if (this.isPlayerPlaying())
        {
            player.worldObj.playSoundAtEntity(player, getSoundOnStop(), 1.0F, 1.0F);
            stop(itemstack, player);
            setNoInterval();
        }
        else
        {
            player.worldObj.playSoundAtEntity(player, getSoundOnPlay(), 1.0F, 1.0F);
            if (player.worldObj.isRemote)
            {
                this.setPlayerPlaying(true);
            }
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

        boolean sndPlaying = controller.getAudioPlayer().playing();
        EntityPlayer player = (EntityPlayer) entity;

        if (this.isPlayerPlaying() && !sndPlaying)
        {
            if (timeInterval > 0)
            {
                timeInterval--;
            }
            else
            {
                ItemStack nextSong = playAction.selectSongToPlay(itemstack, player.inventory.mainInventory);
                if (nextSong != null)
                {
                    play(itemstack, player, nextSong);
                }
                else
                {
                    stop(itemstack, player);
                    controller.playPos.slotPlaying = 0;
                }
                timeInterval = 20;
            }
        }
    }

	@Override
    protected void onPlay(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
        this.itemIcon = this.iconPlaying;
    }

    @Override
    protected void onStop(ItemStack mcmp, EntityPlayer player)
    {
        this.itemIcon = this.iconStopped;
    }

    @Override
    public String getSoundOnPlay()
    {
        return "mcmp1:play";
    }

    @Override
    public String getSoundOnStop()
    {
        return "mcmp1:stop";
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister par1IconRegister)
    {
        this.iconPlaying = par1IconRegister.registerIcon("mcmp1:playerPlaying");
        this.iconStopped = par1IconRegister.registerIcon("mcmp1:playerStopped");
        this.itemIcon = this.iconStopped;
    }
}
