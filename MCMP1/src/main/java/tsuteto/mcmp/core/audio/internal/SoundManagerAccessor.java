package tsuteto.mcmp.core.audio.internal;

import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import paulscode.sound.SoundSystem;
import tsuteto.mcmp.core.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

public class SoundManagerAccessor
{
    private SoundManager soundManager;
    private final Field fldLoaded;
    private final Field fldInvPlayingSounds;

    SoundManagerAccessor()
    {
        fldLoaded = ReflectionHelper.findField(SoundManager.class, "field_148617_f", "loaded");
        fldInvPlayingSounds = ReflectionHelper.findField(SoundManager.class, "field_148630_i", "invPlayingSounds");
    }

    public SoundManager getSoundManager()
    {
        if (this.soundManager == null)
        {
            SoundHandler handler = Minecraft.getMinecraft().getSoundHandler();
            this.soundManager = ReflectionUtil.getValueMatchingType(handler, SoundManager.class);
        }
        return soundManager;
    }

    public SoundSystem getSoundSystem()
    {
        return ReflectionUtil.getValueMatchingType(soundManager, SoundSystem.class);
    }

    public boolean loaded()
    {
        try
        {
            return fldLoaded.getBoolean(soundManager);
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public Map invPlayingSounds()
    {
        try
        {
            return (Map)fldInvPlayingSounds.get(soundManager);
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }
}
