package tsuteto.mcmp.mcmps01.midi.inst;

import tsuteto.mcmp.mcmps01.midi.synthesizer.Note;

public class Percussion extends Instrument
{
    public final int note;

    public Percussion(String baseName, int note, float volume)
    {
        super(baseName, 0, volume);
        this.note = note;
    }

    public Note getNote(int velocity)
    {
        return super.getNote(note, velocity);
    }

    public String getFullSoundId(int noteNo)
    {
        return baseName;
    }
}
