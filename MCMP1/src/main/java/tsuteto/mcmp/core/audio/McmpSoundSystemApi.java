package tsuteto.mcmp.core.audio;

public class McmpSoundSystemApi
{
    public static SoundSystemType registerType(String id, String... extensions)
    {
        return new SoundSystemType(id, extensions);
    }

    public static void registerPlayerFactory(SoundSystemType type, McmpPlayerFactory factory)
    {
        McmpSoundManager.INSTANCE.registerFactory(type, factory);
    }
}
