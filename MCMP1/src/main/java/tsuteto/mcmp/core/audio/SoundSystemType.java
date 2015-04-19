package tsuteto.mcmp.core.audio;

import com.google.common.collect.Lists;

import java.util.List;

public class SoundSystemType
{
    public static final List<SoundSystemType> typeList = Lists.newArrayList();

    public static final SoundSystemType WAV = new SoundSystemType("WAV", "wav");
    public static final SoundSystemType MP3 = new SoundSystemType("MP3", "mp3");
    public static final SoundSystemType AAC = new SoundSystemType("AAC", "m4a", "mp4", "aac");

    public final String id;
    public final String[] extensions;

    public SoundSystemType(String id, String... extensions)
    {
        this.id = id;
        this.extensions = extensions;
        typeList.add(this);
    }

    public String[] getExtensions()
    {
        return extensions;
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof SoundSystemType && this.id.equals(((SoundSystemType)obj).id);
    }
}
