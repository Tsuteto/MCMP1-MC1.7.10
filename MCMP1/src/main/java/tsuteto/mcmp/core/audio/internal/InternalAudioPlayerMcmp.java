package tsuteto.mcmp.core.audio.internal;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.settings.GameSettings;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class InternalAudioPlayerMcmp extends InternalAudioPlayerBase
{
    private static final String bgmIdentifiedName = "Mcmp1Music";

    private ISound playingRecord = null;
    private boolean paused;

    public boolean play(ISound sound)
    {
        if (super.play(sound))
        {
            this.playingRecord = sound;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean play(File soundFile, GameSettings options)
    {
        if (this.soundSystem() == null)
            return false;

        boolean loaded = managerAccessor().loaded();
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

        this.soundSystem().backgroundMusic(bgmIdentifiedName, url, soundFile.getName(), false);
        this.soundSystem().setVolume(bgmIdentifiedName, musicVolume);
        this.paused = false;
        return true;
    }

    public void pause()
    {
        if (isReady())
        {
            super.pause(playingRecord);
            this.paused = true;
        }
    }

    public void resume()
    {
        if (isReady())
        {
            super.resume(playingRecord);
            this.paused = false;
        }
    }

    public void stop()
    {
        if (isReady())
        {
            super.stop(playingRecord);
            this.soundSystem().stop(bgmIdentifiedName);
            this.paused = false;
        }
    }

    public boolean playing()
    {
        return super.playing(playingRecord);
    }

    public boolean paused()
    {
        return this.paused;
    }
}
