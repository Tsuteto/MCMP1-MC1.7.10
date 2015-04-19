package tsuteto.mcmp.mcmps01.midi.inst;

public class Instruments
{
    public static final Instrument PIANO = new Instrument("mcmps01:harp", 0);
    public static final Instrument BASS = new Instrument("mcmps01:bassattack", 12);

    public static final Percussion SNARE = new Percussion("mcmps01:snare", 60, 1.0F);
    public static final Percussion BASSDRUM = new Percussion("mcmps01:bd", 60, 0.5F);
    public static final Percussion CLICKS = new Percussion("mcmps01:hat", 60, 0.5F);
}
