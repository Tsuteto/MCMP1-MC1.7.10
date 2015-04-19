package tsuteto.mcmp.mcmps01.midi.synthesizer;

import tsuteto.mcmp.core.util.McmpLog;
import tsuteto.mcmp.mcmps01.midi.data.McmpMidiMessage;
import tsuteto.mcmp.mcmps01.midi.inst.InstrumentMapper;

import javax.sound.midi.MidiMessage;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Mixer
{
    private McmpMidiSynthesizer synth;
    public BlockingQueue<Object> messageQueue = new LinkedBlockingQueue<Object>();
    public Thread receiverThread;

    public Mixer(McmpMidiSynthesizer var1)
    {
        this.synth = var1;
        this.receiverThread = new Thread("MCMP MIDI Synthesizer")
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try
                    {
                        Object msg = Mixer.this.messageQueue.take();
                        Mixer.this.processMessage(msg);
                    }
                    catch (InterruptedException e)
                    {
                        break;
                    }
                }
                McmpLog.debug("Shut down");
            }
        };
        this.receiverThread.start();
    }

    public void processMessage(Object var1)
    {
        if (var1 instanceof MidiMessage)
        {
            this.processMessage(new McmpMidiMessage((MidiMessage) var1));
        }
        else if (var1 instanceof McmpMidiMessage)
        {
            this.processMessage((McmpMidiMessage) var1);
        }
    }

    public void processMessage(McmpMidiMessage msg)
    {
        if (msg.isChannelMessage())
        {
            McmpMidiSynthesizer.Channel ch = this.synth.channels[msg.getChannel()];
            switch (msg.getStatus())
            {
                case NOTE_ON:
                    if (msg.getData2() > 0)
                    {
                        Note note = ch.getNote(msg.getData1(), msg.getData2());
                        synth.device.playSound(note.soundId, note.pitch, note.velocity * ch.volume);
                    }
                    break;

                case PROGRAM_CHANGE:
                    ch.inst = InstrumentMapper.getInstrument(msg.getData1());
                    break;

                case CONTROL_CHANGE:
                    switch (msg.getData1())
                    {
                        case 7: // Volume
                            ch.volume = ((float) msg.getData2()) / 127.0F;
                    }
            }
        }
    }

    public void close()
    {
        this.messageQueue.clear();

        this.receiverThread.interrupt();
        try
        {
            this.receiverThread.join();
        }
        catch (InterruptedException ignored) {}

        this.synth = null;
        this.receiverThread = null;
    }
}
