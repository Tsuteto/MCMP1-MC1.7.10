package tsuteto.mcmp.core.audio;

import java.io.File;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.audio.extension.ExternalAudioPlayer;
import tsuteto.mcmp.core.audio.extension.Mp3PlayerFactory;
import tsuteto.mcmp.core.audio.extension.AacPlayerFactory;
import tsuteto.mcmp.core.audio.extension.WavPlayerFactory;
import tsuteto.mcmp.core.audio.internal.InternalAudioPlayer;
import tsuteto.mcmp.core.song.SongInfo;
import tsuteto.mcmp.core.song.SongManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.util.McmpLog;

@SideOnly(Side.CLIENT)
public class McmpSoundManager
{
    private static McmpSoundManager instance;

    private SongManager songManager;
    private InternalAudioPlayer internalAudio;
    private ExternalAudioPlayer externalAudio;
    /** For cooling down internal sound system */
    private int ticksCoolDown = 0;

    public static McmpSoundManager getInstance()
    {
        if (instance == null)
        {
            instance = new McmpSoundManager();
        }
        return instance;
    }

    private McmpSoundManager()
    {
        songManager = new SongManager(getSongDir());
        internalAudio = new InternalAudioPlayer();
    }

    public static File getSongDir()
    {
        return new File(FMLClientHandler.instance().getClient().mcDataDir, Mcmp1Core.songDir);
    }

    public boolean playInternal(ISound sound, GameSettings options)
    {
        if (playing()) stop();
        boolean hasPlayed = this.internalAudio.play(sound);
        if (hasPlayed) ticksCoolDown = 20;
        return hasPlayed;
    }

//    public boolean playInternal(SongInfo songinfo, GameSettings options)
//    {
//        if (playing()) stop();
//        boolean hasPlayed = this.internalAudio.play(songinfo.file, options);
//        if (hasPlayed) ticksCoolDown = 20;
//        return hasPlayed;
//    }

    public boolean playMp3(SongInfo songinfo, GameSettings options)
    {
        if (!songinfo.file.exists())
            return false;

        if (playing())
            stop();

        try
        {
            externalAudio = Mp3PlayerFactory.playMp3(songinfo.file);
            externalAudio.setVolume(options.getSoundLevel(SoundCategory.MASTER) * options.getSoundLevel(SoundCategory.MUSIC));
            return true;
        }
        catch (Exception e)
        {
            McmpLog.warn(e, "Failed to play MP3 file");
            return false;
        }
    }

    public boolean playAac(SongInfo songinfo, GameSettings options)
    {
        if (!songinfo.file.exists())
            return false;

        if (playing())
            stop();

        try
        {
            externalAudio = AacPlayerFactory.playAac(songinfo.file);
            externalAudio.setVolume(options.getSoundLevel(SoundCategory.MASTER) * options.getSoundLevel(SoundCategory.MUSIC));
            return true;
        }
        catch (Exception e)
        {
            McmpLog.warn(e, "Failed to play MP4 file");
            return false;
        }
    }

    public boolean playWav(SongInfo songinfo, GameSettings options)
    {
        if (!songinfo.file.exists())
            return false;

        if (playing())
            stop();

        try
        {
            externalAudio = WavPlayerFactory.playWav(songinfo.file);
            externalAudio.setVolume(options.getSoundLevel(SoundCategory.MASTER) * options.getSoundLevel(SoundCategory.MUSIC));
            return true;
        }
        catch (Exception e)
        {
            McmpLog.warn(e, "Failed to play WAV file");
            return false;
        }
    }

    public boolean playRecord(String recordName, GameSettings options)
    {
        ISound sound = PositionedSoundRecord.func_147673_a(new ResourceLocation("records." + recordName));
        return playInternal(sound, options);
    }

    public boolean playHddSong(SongInfo info, GameSettings options)
    {
//        if (info.playerType == EnumSoundSystemType.INTERNAL)
//        {
//            return playInternal(info, options);
//        }
        if (info.playerType == EnumSoundSystemType.MP3)
        {
            return playMp3(info, options);
        }
        else if (info.playerType == EnumSoundSystemType.AAC)
        {
            return playAac(info, options);
        }
        else if (info.playerType == EnumSoundSystemType.WAV)
        {
            return playWav(info, options);
        }
        return false;
    }

    public void stop()
    {
        internalAudio.stop();
        stopExtAudio();
    }

    public void stopExtAudio()
    {
        if (externalAudio != null)
        {
            externalAudio.stop();
            externalAudio = null;
        }
    }

    public void updateVolume()
    {
        if (externalAudio != null && externalAudio.playing())
        {
            Minecraft mc = FMLClientHandler.instance().getClient();
            externalAudio.setVolume(mc.gameSettings.getSoundLevel(SoundCategory.MASTER) * mc.gameSettings.getSoundLevel(SoundCategory.MUSIC));
        }
    }

    public boolean playing()
    {
        if (isReady())
        {
            boolean isInternalPlaying = this.internalAudio.playing();
            if (ticksCoolDown > 0 || isInternalPlaying)
            {
                if (ticksCoolDown > 0)
                    ticksCoolDown--;
                if (ticksCoolDown > 0 && isInternalPlaying)
                {
                    ticksCoolDown = 0;
                }
                return true;
            }
        }
        return externalAudio != null && externalAudio.playing();
    }

    public boolean isReady()
    {
        return internalAudio.isReady();
    }

    public boolean isSoundSystemValid()
    {
        return internalAudio.valid();
    }

    public SongManager getSongManager()
    {
        return songManager;
    }
}
