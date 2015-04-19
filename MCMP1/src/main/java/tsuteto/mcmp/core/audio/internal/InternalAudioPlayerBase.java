package tsuteto.mcmp.core.audio.internal;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;

public class InternalAudioPlayerBase
{
    public boolean play(ISound sound)
    {
        SoundManagerAccessor managerAccessor = this.managerAccessor();
        SoundSystem soundSystem = this.soundSystem();
        SoundManager soundManager = this.soundManager();

        if (soundSystem == null)
            return false;

        boolean loaded = managerAccessor.loaded();

        if (!loaded || sound == null)
        {
            return false;
        }

        soundManager.playSound(sound);
        return true;
    }

    public void pause(ISound sound)
    {
        String sourceName = this.getSourceName(sound);
        if (sourceName != null)
        {
            this.soundSystem().pause(sourceName);
        }
    }

    public void resume(ISound sound)
    {
        String sourceName = this.getSourceName(sound);
        if (sourceName != null)
        {
            this.soundSystem().play(sourceName);
        }
    }

    public void stop(ISound sound)
    {
        if (isReady() && sound != null)
        {
            this.soundManager().stopSound(sound);
        }
    }

    public String getSourceName(ISound sound)
    {
        return (String)managerAccessor().invPlayingSounds().get(sound);
    }

    public boolean playing(ISound sound)
    {
        return this.soundManager().isSoundPlaying(sound);
    }

    public boolean isReady()
    {
        return this.soundSystem() != null;
    }

    public boolean valid() {
        try {
            return this.soundSystem() != null && this.soundManager() != null && managerAccessor().loaded();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }

    protected SoundManagerAccessor managerAccessor()
    {
        return SoundSystemManager.INSTANCE.managerAccessor;
    }

    protected SoundSystem soundSystem()
    {
        return SoundSystemManager.INSTANCE.soundSystem;
    }

    protected SoundManager soundManager()
    {
        return SoundSystemManager.INSTANCE.soundManager;
    }
}
