package tsuteto.mcmp.mcmps01.midi;

import tsuteto.mcmp.core.audio.extension.ExternalAudioPlayer;
import tsuteto.mcmp.mcmps01.device.McmpSoundDevice;
import tsuteto.mcmp.mcmps01.midi.sequencer.McmpMidiSequencer;
import tsuteto.mcmp.mcmps01.midi.synthesizer.McmpMidiSynthesizer;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MidiPlayer implements ExternalAudioPlayer
{
    McmpMidiSynthesizer synthesizer;
    McmpMidiSequencer sequencer;

    public MidiPlayer(File file, McmpSoundDevice device) throws IOException, MidiUnavailableException, InvalidMidiDataException
    {
        this.init(MidiSystem.getSequence(file), device);
    }

    public MidiPlayer(InputStream is, McmpSoundDevice device) throws IOException, MidiUnavailableException, InvalidMidiDataException
    {
        this.init(MidiSystem.getSequence(is), device);
    }

    private void init(Sequence seq, McmpSoundDevice device) throws IOException, MidiUnavailableException, InvalidMidiDataException
    {
        this.synthesizer = new McmpMidiSynthesizer(device);
        this.synthesizer.open();

        this.sequencer = new InstantSequencer(synthesizer);
        this.sequencer.setSequence(seq);
        Receiver rec = this.synthesizer.getReceiver();
        this.sequencer.getTransmitter().setReceiver(rec);
        this.sequencer.open();
    }

    @Override
    public void play() throws Exception
    {
        this.sequencer.start();
    }

    @Override
    public void pause()
    {
        this.sequencer.stop();
    }

    @Override
    public void resume()
    {
        this.sequencer.start();
    }

    @Override
    public void stop()
    {
        sequencer.close();
        synthesizer.close();
    }

    @Override
    public boolean playing()
    {
        return this.sequencer.isPlaying();
    }

    @Override
    public boolean paused()
    {
        return !this.sequencer.isRunning();
    }

    @Override
    public void setGain(float volume)
    {

    }

    @Override
    public void setPan(float ratio)
    {

    }

    @Override
    protected void finalize() throws Throwable
    {
        stop();
    }

    private class InstantSequencer extends McmpMidiSequencer
    {
        McmpMidiSynthesizer synthesizer;

        public InstantSequencer(McmpMidiSynthesizer synthesizer)
        {
            super();
            this.synthesizer = synthesizer;
        }

        @Override
        public void onFinishedPlaying()
        {
            this.close();
            this.synthesizer.close();
        }
    }
}
