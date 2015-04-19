package tsuteto.mcmp.mcmps01.midi.inst;

import tsuteto.mcmp.mcmps01.midi.synthesizer.Note;

public class Instrument
{
    public final String baseName;
    public final int noteShift;
    public final float volume;

    public Instrument(String baseName, int noteShift)
    {
        this(baseName, noteShift, 1.0f);
    }

    public Instrument(String baseName, int noteShift, float volume)
    {
        this.baseName = baseName;
        this.noteShift = noteShift;
        this.volume = volume;
    }

    public Note getNote(int noteNo, int velocity)
    {
        return new Note(this.getFullSoundId(noteNo), noteNo + noteShift, this.getPitch(noteNo), (float)velocity / 127.0F * volume);
    }

    public String getFullSoundId(int noteNo)
    {
        String s;

        int range = (noteNo - 6 + 12 + noteShift) / 24 - 2;
        s = String.format("%s%+d", baseName, range);
        return s;
    }

    private float getPitch(int noteNo)
    {
        noteNo = (noteNo - 6 + 12 + noteShift) % 24;
        return (float)Math.pow(2.0D, (double)(noteNo - 12) / 12.0D);
    }
}
