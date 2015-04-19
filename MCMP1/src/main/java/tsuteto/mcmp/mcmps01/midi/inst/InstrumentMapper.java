package tsuteto.mcmp.mcmps01.midi.inst;

import com.google.common.collect.Maps;

import java.util.Map;

public class InstrumentMapper
{
    private static Map<Integer, Instrument> instMap = Maps.newHashMap();
    private static Map<Integer, Percussion> percMap = Maps.newHashMap();

    static
    {
        registerInstrument(Instruments.BASS,
                32, // Acoustic Bass
                33, // Electric Bass (Finger)
                34, // Electric Bass (Pick)
                35, // Fret-less Bass
                36, // Slap Bass 1
                37, // Slap Bass 2
                38, // Synth Bass 1
                39, // Synth Bass 2
                43  // Double bass
                //58  // Tuba
        );

        registerPercussion(Instruments.BASSDRUM,
                35, // Acoustic Bass Drum
                36  // Bass Drum
        );

        registerPercussion(Instruments.SNARE,
                38, // Acoustic Snare
                40  // Electric Snare
        );
    }

    public static void registerInstrument(Instrument inst, int... instIds)
    {
        for (int id : instIds)
        {
            instMap.put(id, inst);
        }
    }

    public static void registerPercussion(Percussion perc, int... noteNos)
    {
        for (int no : noteNos)
        {
            percMap.put(no, perc);
        }
    }

    public static Instrument getInstrument(int midiInstNo)
    {
        if (instMap.containsKey(midiInstNo))
        {
            return instMap.get(midiInstNo);
        }
        else
        {
            return Instruments.PIANO;
        }
    }

    public static Percussion getPercussion(int midiNote)
    {
        if (percMap.containsKey(midiNote))
        {
            return percMap.get(midiNote);
        }
        else
        {
            return Instruments.CLICKS;
        }
    }
}
