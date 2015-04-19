package tsuteto.mcmp.mcmps01.midi.synthesizer;

import com.google.common.collect.Lists;
import tsuteto.mcmp.core.util.McmpLog;
import tsuteto.mcmp.mcmps01.device.McmpSoundDevice;
import tsuteto.mcmp.mcmps01.midi.inst.Instrument;
import tsuteto.mcmp.mcmps01.midi.inst.InstrumentMapper;
import tsuteto.mcmp.mcmps01.midi.inst.Instruments;
import tsuteto.mcmp.mcmps01.midi.inst.Percussion;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import java.util.ArrayList;

public class McmpMidiSynthesizer
{
    public static final int PERC_CHANNEL = 9;

    private boolean open = false;
    public final Object control_mutex = new Object();
    public Channel[] channels = new Channel[16];
    public McmpSoundDevice device;
    private ArrayList<Receiver> recvslist = Lists.newArrayList();
    private Mixer mixer;

    public McmpMidiSynthesizer(McmpSoundDevice device)
    {
        this.device = device;
    }

    public void open() throws MidiUnavailableException
    {
        if (!this.isOpen())
        {
            synchronized (this.control_mutex)
            {
                this.open = true;
                this.mixer = new Mixer(this);

                // Initialize channels
                for (int i = 0; i < channels.length; i++)
                {
                    if (i == PERC_CHANNEL)
                    {
                        channels[i] = new ChannelPerc(i);
                    }
                    else
                    {
                        channels[i] = new Channel(i);
                    }
                }
                McmpLog.debug("MCMP Synthesizer started up");
            }
        }
    }

    public boolean isOpen()
    {
        synchronized (this.control_mutex)
        {
            return this.open;
        }
    }

    public void removeReceiver(Receiver var1)
    {
        boolean var2 = false;
        synchronized (this.control_mutex)
        {
            if (this.recvslist.remove(var1) && this.recvslist.isEmpty())
            {
                var2 = true;
            }
        }

        if (var2)
        {
            this.close();
        }

    }

    public Receiver getReceiver() throws MidiUnavailableException
    {
        synchronized (this.control_mutex)
        {
            McmpReceiver var2 = new McmpReceiver(this);
            var2.open = this.open;
            this.recvslist.add(var2);
            return var2;
        }
    }

    public Mixer getMixer()
    {
        return !this.isOpen() ? null : this.mixer;
    }

    public void close()
    {
        synchronized (this.control_mutex)
        {
            boolean isClosing = this.open;

            if (this.mixer != null)
            {
                this.mixer.close();
            }

            this.open = false;
            this.mixer = null;
            this.channels = null;

            while (this.recvslist.size() != 0)
            {
                this.recvslist.get(this.recvslist.size() - 1).close();
            }

            if (isClosing)
            {
                McmpLog.debug("MCMP Synthesizer closed");
            }
        }
    }

    public class Channel
    {
        public final int id;
        public Instrument inst = Instruments.PIANO;
        public float volume = 1.0f;

        public Channel(int id)
        {
            this.id = id;
        }

        public Note getNote(int noteNo, int velocity)
        {
            return this.inst.getNote(noteNo, velocity);
        }
    }

    public class ChannelPerc extends Channel
    {
        public ChannelPerc(int id)
        {
            super(id);
        }

        public Note getNote(int noteNo, int velocity)
        {
            Percussion perc = InstrumentMapper.getPercussion(noteNo);
            return perc.getNote(velocity);
        }
    }

}
