package tsuteto.mcmp.mcmps01.device;

public class DebugDevice implements McmpSoundDevice
{
    @Override
    public void setVolume(float volume)
    {

    }

    @Override
    public void playSound(String inst, float pitch, float velocity)
    {
        System.out.println("Sound's out: " + inst + ", pitch=" + pitch + ", velo=" + velocity);
    }
}
