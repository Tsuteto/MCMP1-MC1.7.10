package tsuteto.mcmp.core.mcmpplayer;

import io.netty.buffer.ByteBuf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tsuteto.mcmp.core.audio.McmpAudioPlayer;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPortablePlayerController;

import java.util.List;

public abstract class ItemMcmpPlayer extends Item
{
    protected McmpPortablePlayerController controller = new McmpPortablePlayerController();

    public boolean inInventory;
    public int timeInterval = 0;

    private String soundPlay = null;
    private String soundStop = null;

    public ItemMcmpPlayer()
    {
        super();
        McmpPlayerManager.registerMcmpPlayer(this);
        setCreativeTab(CreativeTabs.tabTools);
    }

    /**
     * Event on item update
     */
    @Override
    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
        inInventory = true;
        if (world.isRemote)
        {
            this.onPlayerUpdate(itemstack, world, entity, i, flag);
        }
    }

    public void onPlayerUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
    }

    public void play(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
        this.controller.play(player.worldObj, mcmp, player, song);
        this.onPlay(mcmp, player, song);
    }

    public void pause(ItemStack mcmp, EntityPlayer player)
    {
        this.controller.pause(player.worldObj);
        this.onPause(mcmp, player);
    }

    public void resume(ItemStack mcmp, EntityPlayer player)
    {
        this.controller.resume(player.worldObj);
        this.onResume(mcmp, player);
    }

    public void stop(ItemStack mcmp, EntityPlayer player)
    {
        this.controller.stop(player.worldObj);
        this.onStop(mcmp, player);
    }

    public void setNoInterval()
    {
        timeInterval = 0;
    }

    protected void onPlay(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
    }

    protected void onPause(ItemStack mcmp, EntityPlayer player)
    {
    }

    protected void onResume(ItemStack mcmp, EntityPlayer player)
    {
    }

    protected void onStop(ItemStack mcmp, EntityPlayer player)
    {
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List par2List, boolean flag)
    {
        if (this.isPlayerPlaying())
        {
            par2List.add("Playing: " + getPlayingSongName());
        }
    }

    public String getPlayingSongName()
    {
        return this.controller.getPlayingSongName();
    }

    public static String getSongName(ItemStack itemstack)
    {
        return McmpPortablePlayerController.getSongName(itemstack);
    }

    public void loadPlayerData(ItemStack mcmp)
    {
        this.controller.loadPlayerData(mcmp);
    }

    public void savePlayerData(ItemStack mcmp)
    {
        this.controller.savePlayerData(mcmp);
    }

    public void updatePlayerState(ItemStack mcmp)
    {
        this.controller.updatePlayerState(mcmp);
    }

    public NBTTagCompound getTagCompound(ItemStack mcmp)
    {
        if (mcmp.getTagCompound() == null)
        {
            mcmp.setTagCompound(new NBTTagCompound());
        }
        return mcmp.getTagCompound();
    }

    protected void sendAdditionalCtlPacketData(int type, ByteBuf dos)
    {
    }

    protected void receiveAdditionalCtlPacketData(int type, ByteBuf buffer)
    {
    }

    public Item setSoundOnPlay(String s)
    {
        soundPlay = s;
        return this;
    }

    public String getSoundOnPlay()
    {
        return soundPlay;
    }

    public Item setSoundOnStop(String s)
    {
        soundStop = s;
        return this;
    }

    public String getSoundOnStop()
    {
        return soundStop;
    }

    public McmpPortablePlayerController getController()
    {
        return this.controller;
    }

    public McmpAudioPlayer getAudioPlayer()
    {
        return this.controller.getAudioPlayer();
    }

    public boolean isPlayerPlaying()
    {
        return this.controller.isPlayerPlaying;
    }

    public void setPlayerPlaying(boolean isPlayerPlaying)
    {
        this.controller.isPlayerPlaying = isPlayerPlaying;
    }
}
