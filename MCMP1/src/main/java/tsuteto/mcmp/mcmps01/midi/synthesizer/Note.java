package tsuteto.mcmp.mcmps01.midi.synthesizer;

/**
 * Created by Tsuteto on 15/04/11.
 */
public class Note
{
    public String soundId;
    public int noteNo;
    public float pitch;
    public float velocity;

    public Note(String soundId, int noteNo, float pitch, float velocity)
    {
        this.soundId = soundId;
        this.noteNo = noteNo;
        this.pitch = pitch;
        this.velocity = velocity;
    }

    @Override
    public int hashCode()
    {
        return noteNo;
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Note && ((Note) obj).noteNo == noteNo;
    }
}
