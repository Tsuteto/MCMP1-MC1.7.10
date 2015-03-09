package tsuteto.mcmp.core.mcmpplayer;

import cpw.mods.fml.client.FMLClientHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.network.PacketDispatcher;
import tsuteto.mcmp.core.song.MediaSongEntry;
import tsuteto.mcmp.core.song.SongInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ItemMcmpPlayer extends Item
{
    public static final int PKT_TYPE_STATE = 0;
    public static final int PKT_TYPE_PLAY = 1;
    public static final int PKT_TYPE_STOP = 2;

    protected McmpSoundManager sndMgr;

    public boolean isPlaying;
    public ItemStack itemPlaying;
    public PlayPosition playPos = new PlayPosition();

    public boolean inInventory;
    public int timeInterval = 0;
    public Random playerRand = new Random();
    private boolean isControlLocked = false;

    private String soundPlay = null;
    private String soundStop = null;

    public ItemMcmpPlayer()
    {
        super();
        sndMgr = McmpSoundManager.getInstance();
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
            onPlayerUpdate(itemstack, world, entity, i, flag);
        }
    }

    public void onPlayerUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag)
    {
    }

    public void play(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
        if (player.worldObj.isRemote && !isControlLocked)
        {
            isControlLocked = true;
            if (playSong(song, player))
            {
                isPlaying = true;
                itemPlaying = song;
                McmpPlayerManager.setPlayingPlayer(mcmp);
                this.dispatchPlayerCtlPacket(PKT_TYPE_PLAY);
            }
            isControlLocked = false;
         }
        onPlay(mcmp, player, song);
    }

    /**
     * Plays a specified song.
     */
    protected boolean playSong(ItemStack itemstack, EntityPlayer player)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        Item item = itemstack.getItem();
        String song = null;
        String songName = null;
        boolean isSucceeded = false;

        if (item instanceof ItemRecord)
        {
            ItemRecord itemrecord = ItemRecord.getRecord("records." + ((ItemRecord)item).recordName);
            song = itemrecord.recordName;
            songName = itemrecord.getRecordNameLocal();
            isSucceeded = sndMgr.playRecord(song, mc.gameSettings);
        }
        else if (item instanceof ItemCassetteTape)
        {
            MediaSongEntry songEntry = ((ItemCassetteTape) item).getSong(itemstack);
            if (songEntry != null)
            {
                if (songEntry.source == ItemCassetteTape.Source.HDD)
                {
                    SongInfo info = sndMgr.getSongManager().getSongInfo(songEntry);
                    if (info != null)
                    {
                        songName = info.songName;
                        isSucceeded = sndMgr.playHddSong(info, mc.gameSettings);
                    }
                    else
                    {
                        player.addChatMessage(new ChatComponentTranslation("mcmp1.fileNotFound", songEntry.id));
                    }

                }
                else if (songEntry.source == ItemCassetteTape.Source.RECORDS)
                {
                    song = songEntry.id;
                    ItemRecord itemrecord = ItemRecord.getRecord("records." + song);
                    if (itemrecord != null)
                    {
                        songName = itemrecord.getRecordNameLocal();
                        isSucceeded = sndMgr.playRecord(song, mc.gameSettings);
                    }
                    else
                    {
                        player.addChatMessage(new ChatComponentTranslation("mcmp1.recordNotFound", songEntry.id));
                    }
                }
            }
        }

        if (songName != null)
        {
            mc.ingameGUI.setRecordPlayingMessage(songName);
        }
        return isSucceeded;
    }

    public void stop(ItemStack mcmp, EntityPlayer player)
    {
        if (player.worldObj.isRemote && !isControlLocked)
        {
            isControlLocked = true;
            isPlaying = false;
            itemPlaying = null;
            McmpPlayerManager.setPlayingPlayer(null);
            this.dispatchPlayerCtlPacket(PKT_TYPE_STOP);
            sndMgr.stop();
            isControlLocked = false;
        }
        onStop(mcmp, player);
    }

    public void setNoInterval()
    {
        timeInterval = 0;
    }

    protected void onPlay(ItemStack mcmp, EntityPlayer player, ItemStack song)
    {
    }

    protected void onStop(ItemStack mcmp, EntityPlayer player)
    {
    }

    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer player, List par2List, boolean flag)
    {
        if (isPlaying)
        {
            par2List.add("Playing: " + getPlayingSongName(player));
        }
    }

    public String getPlayingSongName(EntityPlayer player)
    {
        if (itemPlaying != null)
        {
            return getSongName(itemPlaying, player);
        }
        else
        {
            return null;
        }
    }

    public static String getSongName(ItemStack itemstack, EntityPlayer player)
    {
        List songInfo = new ArrayList();
        itemstack.getItem().addInformation(itemstack, player, songInfo, false);
        return songInfo.get(0).toString();
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
		playPos.slotPlaying = nbttagcompound.getShort("pslt");
		playPos.playingInStack = nbttagcompound.getShort("pstk");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setShort("pslt", (short) playPos.slotPlaying);
        nbttagcompound.setShort("pstk", (short) playPos.playingInStack);
    }

    public void loadPlayerData(ItemStack mcmp)
    {
        NBTTagCompound stackTagCompound = mcmp.getTagCompound();

        if (stackTagCompound != null)
        {
            readFromNBT(stackTagCompound);
        }
    }

    public void savePlayerData(ItemStack mcmp)
    {
        if (mcmp.stackTagCompound == null)
        {
            mcmp.setTagCompound(new NBTTagCompound());
        }

        writeToNBT(mcmp.stackTagCompound);
    }

    public void updatePlayerState(ItemStack mcmp)
    {
        this.dispatchPlayerCtlPacket(PKT_TYPE_STATE);
        this.savePlayerData(mcmp);
    }

    protected void dispatchPlayerCtlPacket(final int type)
    {
        if (type == ItemMcmpPlayer.PKT_TYPE_STATE)
        {
            PacketDispatcher.packet(new PacketMcmpPlayerCtl(type, playPos.slotPlaying, playPos.playingInStack)).sendToServer();
        }
        else
        {
            PacketDispatcher.packet(new PacketMcmpPlayerCtl(type)).sendToServer();
        }
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
}
