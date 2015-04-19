package tsuteto.mcmp.core.audio.param;

public class McmpSoundBase implements IMcmpSound
{
    protected float volume = 1.0F;
    protected float pitch = 1.0F;
    protected float xPos;
    protected float yPos;
    protected float zPos;
    protected boolean repeat = false;
    protected int repeatDelay = 0;
    protected AttenuationType attenuationType = AttenuationType.LINEAR;

    public boolean canRepeat()
    {
        return this.repeat;
    }

    public int getRepeatDelay()
    {
        return this.repeatDelay;
    }

    public float getVolume()
    {
        return this.volume;
    }

    public float getPitch()
    {
        return this.pitch;
    }

    public float posX()
    {
        return this.xPos;
    }

    public float posY()
    {
        return this.yPos;
    }

    public float posZ()
    {
        return this.zPos;
    }

    public AttenuationType getAttenuationType()
    {
        return this.attenuationType;
    }
}
