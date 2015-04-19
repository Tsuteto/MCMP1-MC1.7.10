package tsuteto.mcmp.mcmps01.midi.synthesizer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Queue;

public class McmpReceiver implements Receiver
{
    boolean open = true;
    private final Object controlMutex;
    private final McmpMidiSynthesizer synth;
    private Queue<Object> messageQueue;

    public McmpReceiver(McmpMidiSynthesizer var1)
    {
        this.controlMutex = var1.control_mutex;
        this.synth = var1;
        this.messageQueue = this.synth.getMixer().messageQueue;
    }

    public void send(MidiMessage var1, long var2)
    {
        synchronized (this.controlMutex)
        {
            if (!this.open)
            {
                throw new IllegalStateException("Receiver is not open");
            }
        }

        synchronized (this.controlMutex)
        {
            this.messageQueue.offer(var1.clone());
        }
    }

    public void close()
    {
        Object var1 = this.controlMutex;
        synchronized (this.controlMutex)
        {
            this.open = false;
        }

        this.synth.removeReceiver(this);
    }

}
