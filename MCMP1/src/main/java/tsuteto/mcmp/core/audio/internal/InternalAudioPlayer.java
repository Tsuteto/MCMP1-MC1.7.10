package tsuteto.mcmp.core.audio.internal;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import paulscode.sound.SoundSystem;
import tsuteto.mcmp.core.util.McmpLog;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class InternalAudioPlayer
{
    private static final String bgmIdentifiedName = "Mcmp1Music";

    private SoundSystem soundSystem;
    private SoundManager soundManager;
    private SoundManagerAccessor managerAccessor = new SoundManagerAccessor();
    private boolean enabled = true;
    private ISound playingRecord = null;

    private Thread initThread;

    public InternalAudioPlayer()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onSoundSetup(SoundLoadEvent event)
    {
        if(this.enabled) {
            //this.objectToAudioSourceMap.clear();
            Thread thread = this.initThread;
            if(thread != null) {
                thread.interrupt();

                try {
                    thread.join();
                } catch (InterruptedException ignored) {}
            }

            McmpLog.info("MCMP-1 audio starting.");
            this.soundManager = this.managerAccessor.getSoundManager();
            this.initThread = new Thread(new Runnable() {
                public void run() {
                    while(true) {
                        try {
                            if(!Thread.currentThread().isInterrupted()) {
                                boolean e = InternalAudioPlayer.this.managerAccessor.loaded();

                                if(!e) {
                                    Thread.sleep(100L);
                                    continue;
                                }

                                soundSystem = InternalAudioPlayer.this.managerAccessor.getSoundSystem();
                                if(soundSystem == null) {
                                    McmpLog.log(Level.WARN, "Failed to start MCMP-1 audio.");
                                    InternalAudioPlayer.this.enabled = false;
                                } else {
                                    McmpLog.info("MCMP-1 audio is ready.");
                                }
                            }
                        } catch (InterruptedException ignored) {}

                        InternalAudioPlayer.this.initThread = null;
                        return;
                    }
                }
            }, "MCMP-1 Audio Initializer");
            this.initThread.setDaemon(true);
            this.initThread.start();
        }
    }

    public boolean play(ISound sound)
    {
        if (this.soundSystem == null)
            return false;

        boolean loaded = managerAccessor.loaded();

        if (!loaded || sound == null)
        {
            return false;
        }

        this.soundManager.playSound(sound);
        this.playingRecord = sound;
        return true;
    }

    public boolean play(File soundFile, GameSettings options)
    {
        if (this.soundSystem == null)
            return false;

        boolean loaded = managerAccessor.loaded();
        float musicVolume = options.getSoundLevel(SoundCategory.MUSIC);

        if (!loaded || musicVolume == 0.0F)
        {
            return false;
        }

        if (soundFile == null)
        {
            return false;
        }

        URL url;
        try
        {
            url = soundFile.toURI().toURL();
        }
        catch (MalformedURLException e)
        {
            return false;
        }

        this.soundSystem.backgroundMusic(bgmIdentifiedName, url, soundFile.getName(), false);
        this.soundSystem.setVolume(bgmIdentifiedName, musicVolume);
        return true;
    }

    public void stop()
    {
        if (isReady())
        {
            if (playingRecord != null)
            {
                this.soundManager.stopSound(playingRecord);
            }
            this.soundSystem.stop(bgmIdentifiedName);
        }
    }

    public boolean playing()
    {
        return this.soundManager.isSoundPlaying(playingRecord) || this.soundSystem.playing(bgmIdentifiedName);
    }

    public boolean isReady()
    {
        return this.soundSystem != null;
    }

    public boolean valid() {
        try {
            return this.soundSystem != null && this.soundManager != null && this.managerAccessor.loaded();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

}
