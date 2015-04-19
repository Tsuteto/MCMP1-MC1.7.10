package tsuteto.mcmp.core.mcmpplayer.controller;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.core.audio.McmpAudioPlayer;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.audio.param.IMcmpSound;
import tsuteto.mcmp.core.audio.param.McmpSound;
import tsuteto.mcmp.core.mcmpplayer.data.PlayPosition;
import tsuteto.mcmp.core.media.IMcmpMedia;
import tsuteto.mcmp.core.network.PacketDispatcher;
import tsuteto.mcmp.core.song.MediaSongEntry;
import tsuteto.mcmp.core.song.SongInfo;

import java.util.Random;

public abstract class McmpPlayerControllerBase
{
    public static final int PKT_TYPE_STATE = 0;
    public static final int PKT_TYPE_PLAY = 1;
    public static final int PKT_TYPE_STOP = 2;
    public static final int PKT_TYPE_PAUSE = 3;
    public static final int PKT_TYPE_RESUME = 4;

    protected McmpAudioPlayer audioPlayer = new McmpAudioPlayer();
    public boolean isPlayerPlaying = false;
    public boolean isPlayerPaused = false;
    public ItemStack itemPlaying = null;
    public PlayPosition playPos = new PlayPosition();
    public Random rand = new Random();
    private boolean isControlLocked = false;

    protected void play(World world, EntityPlayer player, ItemStack media, McmpSound soundParams)
    {
        if (!isControlLocked)
        {
            isControlLocked = true;
            if (world.isRemote)
            {
                if (playSong(media, player, soundParams))
                {
                    isPlayerPlaying = true;
                    itemPlaying = media;
                    this.dispatchPlayerCtlPacketToSv(PKT_TYPE_PLAY);
                }
            }
            else
            {
                isPlayerPlaying = true;
                itemPlaying = media;
            }
            isControlLocked = false;
        }
    }

    /**
     * Plays a specified song.
     */
    protected boolean playSong(ItemStack mediaItem, EntityPlayer player, McmpSound soundParams)
    {
        Minecraft mc = FMLClientHandler.instance().getClient();
        Item item = mediaItem.getItem();
        String songName = null;
        boolean isSucceeded = false;

        // Play music of media
        if (item instanceof ItemRecord)
        {
            ItemRecord itemrecord = ItemRecord.getRecord("records." + ((ItemRecord)item).recordName);
            String songId = itemrecord.recordName;
            songName = itemrecord.getRecordNameLocal();
            isSucceeded = audioPlayer.playRecord(songId, soundParams);
        }
        else if (item instanceof IMcmpMedia)
        {
            MediaSongEntry songEntry = ((IMcmpMedia)item).getSong(mediaItem);
            if (songEntry != null)
            {
                if (songEntry.source == ItemCassetteTape.Source.HDD)
                {
                    SongInfo info = McmpSoundManager.INSTANCE.getSongManager().getSongInfo(songEntry);
                    if (info != null)
                    {
                        songName = info.songName;
                        isSucceeded = audioPlayer.playHddSong(info, soundParams);
                    }
                    else if (player != null)
                    {
                        player.addChatMessage(new ChatComponentTranslation("mcmp1.fileNotFound", songEntry.id));
                    }

                }
                else if (songEntry.source == ItemCassetteTape.Source.RECORDS)
                {
                    String songId = songEntry.id;
                    ItemRecord itemrecord = ItemRecord.getRecord("records." + songId);
                    if (itemrecord != null)
                    {
                        songName = itemrecord.getRecordNameLocal();
                        isSucceeded = audioPlayer.playRecord(songId, soundParams);
                    }
                    else if (player != null)
                    {
                        player.addChatMessage(new ChatComponentTranslation("mcmp1.recordNotFound", songEntry.id));
                    }
                }
            }
        }

        if (songName != null)
        {
            boolean shouldDisplaySongName;
            if (soundParams.getAttenuationType() == IMcmpSound.AttenuationType.LINEAR)
            {
                double dist = mc.thePlayer.getDistance(soundParams.posX(), soundParams.posY(), soundParams.posZ());
                shouldDisplaySongName = dist < 4.0D;
            }
            else
            {
                shouldDisplaySongName = true;
            }

            if (shouldDisplaySongName)
            {
                mc.ingameGUI.setRecordPlayingMessage(songName);
            }
        }
        return isSucceeded;
    }

    public void pause(World world)
    {
        if (!isControlLocked && isPlayerPlaying)
        {
            isControlLocked = true;
            isPlayerPaused = true;
            if (world.isRemote)
            {
                audioPlayer.pause();
                this.dispatchPlayerCtlPacketToSv(PKT_TYPE_PAUSE);
            }
            isControlLocked = false;
        }
    }

    public void resume(World world)
    {
        if (!isControlLocked && isPlayerPlaying)
        {
            isControlLocked = true;
            isPlayerPaused = false;
            if (world.isRemote)
            {
                audioPlayer.resume();
                this.dispatchPlayerCtlPacketToSv(PKT_TYPE_RESUME);
            }
            isControlLocked = false;
        }
    }

    public void stop(World world)
    {
        if (!isControlLocked && isPlayerPlaying)
        {
            isControlLocked = true;
            isPlayerPlaying = false;
            isPlayerPaused = false;
            itemPlaying = null;
            if (world.isRemote)
            {
                audioPlayer.stop();
                this.dispatchPlayerCtlPacketToSv(PKT_TYPE_STOP);
            }
            isControlLocked = false;
        }
    }

    public String getPlayingSongName()
    {
        if (itemPlaying != null)
        {
            return getSongName(itemPlaying);
        }
        else
        {
            return null;
        }
    }

    public static String getSongName(ItemStack itemstack)
    {
        Item item = itemstack.getItem();
        if (item instanceof IMcmpMedia)
        {
            return ((IMcmpMedia)item).getSongName(itemstack);
        }
        else if (item instanceof ItemRecord)
        {
            ItemRecord itemrecord = ItemRecord.getRecord("records." + ((ItemRecord)item).recordName);
            return itemrecord.getRecordNameLocal();
        }
        else
        {
            return "";
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound)
    {
        if (nbttagcompound != null)
        {
            playPos.slotPlaying = nbttagcompound.getShort("pslt");
            playPos.playingInStack = nbttagcompound.getShort("pstk");
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound)
    {
        nbttagcompound.setShort("pslt", (short) playPos.slotPlaying);
        nbttagcompound.setShort("pstk", (short) playPos.playingInStack);
    }

    public void updatePlayerState()
    {
        this.dispatchPlayerCtlPacketToSv(PKT_TYPE_STATE);
        this.playPos.changed = false;
    }

    public abstract PacketDispatcher createPlayerCtlPacket(int type);

    public void dispatchPlayerCtlPacketToSv(final int type)
    {
        this.createPlayerCtlPacket(type).sendToServer();
    }

    public McmpAudioPlayer getAudioPlayer()
    {
        return this.audioPlayer;
    }
}
