package tsuteto.mcmp.core.audio;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import paulscode.sound.ListenerData;
import paulscode.sound.Vector3D;
import tsuteto.mcmp.core.audio.extension.ExternalAudioPlayer;
import tsuteto.mcmp.core.audio.internal.InternalAudioPlayerMcmp;
import tsuteto.mcmp.core.audio.param.IMcmpSound;
import tsuteto.mcmp.core.audio.param.McmpSound;
import tsuteto.mcmp.core.song.SongInfo;
import tsuteto.mcmp.core.util.McmpLog;

/**
 * Integrated audio player
 */
public class McmpAudioPlayer
{
    private InternalAudioPlayerMcmp internalAudio;
    private ExternalAudioPlayer externalAudio;
    private IMcmpSound soundParams;
    private ListenerData listener = new ListenerData( 0.0f, 0.0f, 0.0f,  // position
                                                      0.0f, 0.0f, -1.0f, // look-at direction
                                                      0.0f, 1.0f, 0.0f,  // up direction
                                                      0.0f );            // angle
    private Vector3D position;
    private float distOrRoll;
    /** For cooling down internal sound system */
    private int ticksCoolDown = 0;

    public McmpAudioPlayer()
    {
        internalAudio = new InternalAudioPlayerMcmp();
    }

    public IMcmpSound getSoundParams()
    {
        return this.soundParams;
    }

    public boolean playInternal(ISound sound)
    {
        if (playing()) stop();
        boolean hasPlayed = this.internalAudio.play(sound);
        if (hasPlayed) ticksCoolDown = 20;
        McmpSoundManager.INSTANCE.addActivePlayer(this);
        return hasPlayed;
    }

//    public boolean playInternal(SongInfo songinfo, GameSettings options)
//    {
//        if (playing()) stop();
//        boolean hasPlayed = this.internalAudio.play(songinfo.file, options);
//        if (hasPlayed) ticksCoolDown = 20;
//        return hasPlayed;
//    }

    public boolean playRecord(String recordName, McmpSound soundParams)
    {
        this.soundParams = soundParams;
        ResourceLocation resource = new ResourceLocation("records." + recordName);
        ISound sound;
        if (this.soundParams.getAttenuationType() == IMcmpSound.AttenuationType.NONE)
        {
            sound = PositionedSoundRecord.func_147673_a(resource);
        }
        else
        {
            sound = PositionedSoundRecord.func_147675_a(resource, soundParams.posX(), soundParams.posY(), soundParams.posZ());
        }
        return playInternal(sound);
    }

    public boolean playHddSong(SongInfo info, McmpSound soundParams)
    {
        if (!info.file.exists())
            return false;

        if (playing())
            stop();

        try
        {
            this.soundParams = soundParams;
            this.position = new Vector3D(soundParams.posX(), soundParams.posY(), soundParams.posZ());
            this.distOrRoll = soundParams.getVolume() > 1.0F ? soundParams.getVolume() * 16.0F : 16.0F;
            this.externalAudio = McmpSoundManager.INSTANCE.getPlayerFactory(info.playerType).play(info.file, soundParams);
            McmpSoundManager.INSTANCE.addActivePlayer(this);
            return true;
        }
        catch (Exception e)
        {
            McmpLog.warn(e, "Failed to play " + info.songName);
            return false;
        }
    }

    public void pause()
    {
        internalAudio.pause();
        if (externalAudio != null)
        {
            externalAudio.pause();
        }
        McmpLog.debug("paused");
    }

    public void resume()
    {
        internalAudio.resume();
        if (externalAudio != null)
        {
            externalAudio.resume();
        }
        McmpLog.debug("resumed");
    }

    public void stop()
    {
        internalAudio.stop();
        if (externalAudio != null)
        {
            externalAudio.stop();
            externalAudio = null;
        }
    }

    public void updateSound(EntityPlayer listener, float p_148615_2_)
    {
        if (externalAudio != null && externalAudio.playing())
        {
            this.setListener(listener, p_148615_2_);
            this.calculateGain();
            this.calculatePan();
        }
    }

    /**
     * Sets the listener of sounds
     */
    public void setListener(EntityPlayer p_148615_1_, float p_148615_2_)
    {
        float pitch = p_148615_1_.prevRotationPitch + (p_148615_1_.rotationPitch - p_148615_1_.prevRotationPitch) * p_148615_2_;
        float yaw = p_148615_1_.prevRotationYaw + (p_148615_1_.rotationYaw - p_148615_1_.prevRotationYaw) * p_148615_2_;
        double posX = p_148615_1_.prevPosX + (p_148615_1_.posX - p_148615_1_.prevPosX) * (double)p_148615_2_;
        double posY = p_148615_1_.prevPosY + (p_148615_1_.posY - p_148615_1_.prevPosY) * (double)p_148615_2_;
        double posZ = p_148615_1_.prevPosZ + (p_148615_1_.posZ - p_148615_1_.prevPosZ) * (double)p_148615_2_;
        float f3 = MathHelper.cos((yaw + 90.0F) * 0.017453292F);
        float f4 = MathHelper.sin((yaw + 90.0F) * 0.017453292F);
        float f5 = MathHelper.cos(-pitch * 0.017453292F);
        float lookY = MathHelper.sin(-pitch * 0.017453292F);
        float f7 = MathHelper.cos((-pitch + 90.0F) * 0.017453292F);
        float upY = MathHelper.sin((-pitch + 90.0F) * 0.017453292F);
        float lookX = f3 * f5;
        float lookZ = f4 * f5;
        float upX = f3 * f7;
        float upZ = f4 * f7;

        this.listener.position.x = (float)posX;
        this.listener.position.y = (float)posY;
        this.listener.position.z = (float)posZ;
        this.listener.lookAt.x = lookX;
        this.listener.lookAt.y = lookY;
        this.listener.lookAt.z = lookZ;
        this.listener.up.x = upX;
        this.listener.up.y = upY;
        this.listener.up.z = upZ;
    }

    public void calculateGain()
    {
        float gain;

        switch (soundParams.getAttenuationType())
        {
            case LINEAR:
                float distX = position.x - listener.position.x;
                float distY = position.y - listener.position.y;
                float distZ = position.z - listener.position.z;
                float distanceFromListener = (float) Math.sqrt(distX * distX + distY * distY + distZ * distZ);

                if (distanceFromListener <= 0)
                {
                    gain = 1.0f;
                }
                else if (distanceFromListener >= distOrRoll)
                {
                    gain = 0.0f;
                }
                else
                {
                    gain = 1.0f - (distanceFromListener / distOrRoll);
                }
                // make sure gain is between 0 and 1:
                if (gain > 1.0f)
                    gain = 1.0f;
                if (gain < 0.0f)
                    gain = 0.0f;
            break;

            default:
                gain = 1.0F;
                break;
        }

        SoundCategory cat = soundParams.getAttenuationType() == IMcmpSound.AttenuationType.NONE ? SoundCategory.MUSIC : SoundCategory.RECORDS;

        Minecraft mc = FMLClientHandler.instance().getClient();
        gain *= mc.gameSettings.getSoundLevel(SoundCategory.MASTER)
                * mc.gameSettings.getSoundLevel(cat)
                * MathHelper.clamp_float(soundParams.getVolume(), 0.0F, 1.0F);

        externalAudio.setGain(gain);
    }

    public void calculatePan()
    {
        float pan;

        switch (soundParams.getAttenuationType())
        {
            case LINEAR:
                Vector3D side = listener.up.cross(listener.lookAt);
                side.normalize();
                float x = position.dot(position.subtract(listener.position), side);
                float z = position.dot(position.subtract(listener.position), listener.lookAt);
                float angle = (float) Math.atan2(x, z);
                pan = (float) -Math.sin(angle) * 0.6f;
                break;

            default:
                pan = 0.0F;
                break;
        }

        externalAudio.setPan(pan);
    }

    public void onTick()
    {
        if (isReady())
        {
            if (ticksCoolDown > 0)
            {
                if (this.internalAudio.playing())
                {
                    ticksCoolDown = 0;
                }
                else
                {
                    ticksCoolDown--;
                }
            }
        }

        if (!this.playing())
        {
            McmpSoundManager.INSTANCE.removeActivePlayer(this);
        }
    }

    public boolean playing()
    {
        if (isReady())
        {
            if (ticksCoolDown > 0 || this.internalAudio.playing())
            {
                return true;
            }
        }
        return externalAudio != null && externalAudio.playing();
    }

    public boolean paused()
    {
        if (isReady() && this.internalAudio.playing())
        {
            return internalAudio.paused();
        }
        return externalAudio != null && externalAudio.paused();
    }

    public boolean isReady()
    {
        return internalAudio.isReady();
    }

    public boolean isSoundSystemValid()
    {
        return internalAudio.valid();
    }
}
