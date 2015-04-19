package tsuteto.mcmp.mcmps01.core;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import tsuteto.mcmp.core.audio.McmpSoundSystemApi;
import tsuteto.mcmp.core.audio.SoundSystemType;
import tsuteto.mcmp.mcmps01.midi.MidiPlayerFactory;

@Mod(modid = Mcms1Core.modId, name = "MCMP-S01", version = "0.1.0-MC1.7.10")
public class Mcms1Core
{
    public static final String modId = "mcmps01";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        SoundSystemType midi = McmpSoundSystemApi.registerType("MIDI", "mid", "midi");
        McmpSoundSystemApi.registerPlayerFactory(midi, new MidiPlayerFactory());
        FMLCommonHandler.instance().bus().register(new InternalAudioPlayer());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {

    }
}
