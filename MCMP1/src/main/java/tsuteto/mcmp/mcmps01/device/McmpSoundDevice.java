package tsuteto.mcmp.mcmps01.device;

public interface McmpSoundDevice
{
    void setVolume(float volume);
    void playSound(String soundId, float pitch, float velocity);
}
