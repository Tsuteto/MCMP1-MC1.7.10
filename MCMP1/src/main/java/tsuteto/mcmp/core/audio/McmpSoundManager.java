package tsuteto.mcmp.core.audio;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.audio.extension.AacPlayerFactory;
import tsuteto.mcmp.core.audio.extension.Mp3PlayerFactory;
import tsuteto.mcmp.core.audio.extension.WavPlayerFactory;
import tsuteto.mcmp.core.audio.param.IMcmpSound;
import tsuteto.mcmp.core.song.SongFileLoader;
import tsuteto.mcmp.core.song.SongManager;
import tsuteto.mcmp.core.song.SongPool;
import tsuteto.mcmp.core.util.McmpLog;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Sound Manager for MCMP-1, handling songs and audio player factories
 */
@SideOnly(Side.CLIENT)
public class McmpSoundManager
{
    public static McmpSoundManager INSTANCE = new McmpSoundManager();

    private Map<SoundSystemType, McmpPlayerFactory> playerFactoryRegistry = Maps.newHashMap();
    private List<McmpAudioPlayer> activeBGMs = Lists.newArrayList();
    private List<McmpAudioPlayer> activeBlocks = Lists.newArrayList();
    private List<McmpAudioPlayer> removedPlayers = Lists.newArrayList();

    private SongManager songManager;

    public static McmpSoundManager getInstance()
    {
        return INSTANCE;
    }

    private McmpSoundManager()
    {
        this.songManager = new SongManager();

        this.registerFactory(SoundSystemType.MP3, new Mp3PlayerFactory());
        this.registerFactory(SoundSystemType.AAC, new AacPlayerFactory());
        this.registerFactory(SoundSystemType.WAV, new WavPlayerFactory());
    }

    public void registerFactory(SoundSystemType type, McmpPlayerFactory factory)
    {
        this.playerFactoryRegistry.put(type, factory);
    }

    public void loadSongs()
    {
        File songDir = new File(FMLClientHandler.instance().getClient().mcDataDir, Mcmp1Core.songDir);
        SongPool songPool = SongFileLoader.loadSongs(songDir);
        this.songManager.setSongPool(songPool);
    }

    public SongManager getSongManager()
    {
        return songManager;
    }

    public McmpPlayerFactory getPlayerFactory(SoundSystemType type)
    {
        return playerFactoryRegistry.get(type);
    }

    public List<McmpAudioPlayer> getAllPlayers()
    {
        List<McmpAudioPlayer> list = Lists.newArrayList();
        list.addAll(this.activeBGMs);
        list.addAll(this.activeBlocks);
        return list;
    }

    public List<McmpAudioPlayer> getActiveBlocks()
    {
        return this.activeBlocks;
    }

    public void addActivePlayer(McmpAudioPlayer player)
    {
        if (player.getSoundParams().getAttenuationType() == IMcmpSound.AttenuationType.NONE)
        {
            this.activeBGMs.add(player);
        }
        else
        {
            this.activeBlocks.add(player);
        }
        McmpLog.debug("Added: BGM=%s, Block=%s", activeBGMs.size(), activeBlocks.size());
    }

    public void removeActivePlayer(McmpAudioPlayer player)
    {
        this.removedPlayers.add(player);
    }

    public void removeInactivePlayers()
    {
        if (!this.removedPlayers.isEmpty())
        {
            for (McmpAudioPlayer player : this.removedPlayers)
            {
                this.activeBGMs.remove(player);
                this.activeBlocks.remove(player);
            }
            this.removedPlayers.clear();
            McmpLog.debug("Removed: BGM=%s, Block=%s", activeBGMs.size(), activeBlocks.size());
        }
    }

    public void resumeAllSounds()
    {
        for (McmpAudioPlayer player : this.getAllPlayers())
        {
            player.resume();
        }
    }

    public void pauseAllSounds()
    {
        for (McmpAudioPlayer player : this.getAllPlayers())
        {
            player.pause();
        }

    }

    // Called from coremod (TEntryMusicType)
    public boolean isBgmPlaying()
    {
        return this.activeBGMs.size() > 0;
    }

    public void stopAllSounds()
    {
        for (McmpAudioPlayer player : this.getAllPlayers())
        {
            player.stop();
        }
    }
}
