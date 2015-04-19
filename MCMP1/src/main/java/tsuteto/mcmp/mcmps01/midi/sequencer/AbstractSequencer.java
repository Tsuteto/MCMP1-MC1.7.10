package tsuteto.mcmp.mcmps01.midi.sequencer;

import com.sun.media.sound.ReferenceCountingDevice;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class AbstractSequencer implements MidiDevice, ReferenceCountingDevice
{
    private ArrayList<Receiver> receiverList;
    private TransmitterList transmitterList;
    private final MidiDevice.Info info;

    private final Object traRecLock = new Object();
    private boolean open = false;
    private List openKeepingObjects;
    private int openRefCount;
    protected long id = 0L;


    protected AbstractSequencer(Info var1)
    {
        this.info = var1;
        this.openRefCount = 0;
    }

    public final MidiDevice.Info getDeviceInfo()
    {
        return this.info;
    }

    public final void open() throws MidiUnavailableException
    {
        synchronized (this)
        {
            this.openRefCount = -1;
            this.doOpen();
        }
    }

    private void openInternal(Object var1) throws MidiUnavailableException
    {
        synchronized (this)
        {
            if (this.openRefCount != -1)
            {
                ++this.openRefCount;
                this.getOpenKeepingObjects().add(var1);
            }

            this.doOpen();
        }
    }

    private void doOpen() throws MidiUnavailableException
    {
        synchronized (this)
        {
            if (!this.isOpen())
            {
                this.implOpen();
                this.open = true;
            }

        }
    }

    public final void close()
    {
        synchronized (this)
        {
            this.doClose();
            this.openRefCount = 0;
        }
    }

    public final void closeInternal(Object var1)
    {
        synchronized (this)
        {
            if (this.getOpenKeepingObjects().remove(var1) && this.openRefCount > 0)
            {
                --this.openRefCount;
                if (this.openRefCount == 0)
                {
                    this.doClose();
                }
            }

        }
    }

    public final void doClose()
    {
        synchronized (this)
        {
            if (this.isOpen())
            {
                this.implClose();
                this.open = false;
            }

        }
    }

    public final boolean isOpen()
    {
        return this.open;
    }

    protected void implClose()
    {
        Object var1 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            if (this.receiverList != null)
            {
                for (int var2 = 0; var2 < this.receiverList.size(); ++var2)
                {
                    ((Receiver) this.receiverList.get(var2)).close();
                }

                this.receiverList.clear();
            }

            if (this.transmitterList != null)
            {
                this.transmitterList.close();
            }

        }
    }

    public final int getMaxReceivers()
    {
        return this.hasReceivers() ? -1 : 0;
    }

    public final int getMaxTransmitters()
    {
        return this.hasTransmitters() ? -1 : 0;
    }

    public final Receiver getReceiver() throws MidiUnavailableException
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            Receiver var1 = this.createReceiver();
            this.getReceiverList().add(var1);
            return var1;
        }
    }

    public final List<Receiver> getReceivers()
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            List var1;
            if (this.receiverList == null)
            {
                var1 = Collections.unmodifiableList(new ArrayList(0));
            }
            else
            {
                var1 = Collections.unmodifiableList((List) ((List) this.receiverList.clone()));
            }

            return var1;
        }
    }

    public final Transmitter getTransmitter() throws MidiUnavailableException
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            Transmitter var1 = this.createTransmitter();
            this.getTransmitterList().add(var1);
            return var1;
        }
    }

    public final List<Transmitter> getTransmitters()
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            List var1;
            if (this.transmitterList != null && this.transmitterList.transmitters.size() != 0)
            {
                var1 = Collections.unmodifiableList((List) ((List) this.transmitterList.transmitters.clone()));
            }
            else
            {
                var1 = Collections.unmodifiableList(new ArrayList(0));
            }

            return var1;
        }
    }

    final long getId()
    {
        return this.id;
    }

    public final Receiver getReceiverReferenceCounting() throws MidiUnavailableException
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            Receiver var1 = this.getReceiver();
            this.openInternal(var1);
            return var1;
        }
    }

    public final Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException
    {
        Object var2 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            Transmitter var1 = this.getTransmitter();
            this.openInternal(var1);
            return var1;
        }
    }

    private synchronized List getOpenKeepingObjects()
    {
        if (this.openKeepingObjects == null)
        {
            this.openKeepingObjects = new ArrayList();
        }

        return this.openKeepingObjects;
    }

    private List<Receiver> getReceiverList()
    {
        Object var1 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            if (this.receiverList == null)
            {
                this.receiverList = new ArrayList();
            }
        }

        return this.receiverList;
    }

    protected boolean hasReceivers()
    {
        return false;
    }

    protected Receiver createReceiver() throws MidiUnavailableException
    {
        throw new MidiUnavailableException("MIDI IN receiver not available");
    }

    final TransmitterList getTransmitterList()
    {
        Object var1 = this.traRecLock;
        synchronized (this.traRecLock)
        {
            if (this.transmitterList == null)
            {
                this.transmitterList = new TransmitterList();
            }
        }

        return this.transmitterList;
    }

    protected boolean hasTransmitters()
    {
        return false;
    }

    protected Transmitter createTransmitter() throws MidiUnavailableException
    {
        throw new MidiUnavailableException("MIDI OUT transmitter not available");
    }

    protected abstract void implOpen() throws MidiUnavailableException;

    protected final void finalize()
    {
        this.close();
    }

    public void onFinishedPlaying() {}

    final class TransmitterList
    {
        private final ArrayList<Transmitter> transmitters = new ArrayList();
        private Receiver midiOutReceiver;
        private int optimizedReceiverCount = 0;

        TransmitterList()
        {
        }

        private void add(Transmitter var1)
        {
            ArrayList var2 = this.transmitters;
            synchronized (this.transmitters)
            {
                this.transmitters.add(var1);
            }

            if (var1 instanceof BasicTransmitter)
            {
                ((BasicTransmitter) var1).setTransmitterList(this);
            }

        }

        private void remove(Transmitter var1)
        {
            ArrayList var2 = this.transmitters;
            synchronized (this.transmitters)
            {
                int var3 = this.transmitters.indexOf(var1);
                if (var3 >= 0)
                {
                    this.transmitters.remove(var3);
                }

            }
        }

        private void receiverChanged(BasicTransmitter var1, Receiver var2, Receiver var3)
        {
            ArrayList var4 = this.transmitters;
            synchronized (this.transmitters)
            {
                if (this.midiOutReceiver == var2)
                {
                    this.midiOutReceiver = null;
                }

                if (var3 != null && this.midiOutReceiver == null)
                {
                    this.midiOutReceiver = var3;
                }

                this.optimizedReceiverCount = this.midiOutReceiver != null ? 1 : 0;
            }
        }

        void close()
        {
            ArrayList var1 = this.transmitters;
            synchronized (this.transmitters)
            {
                for (int var2 = 0; var2 < this.transmitters.size(); ++var2)
                {
                    this.transmitters.get(var2).close();
                }

                this.transmitters.clear();
            }
        }

        void sendMessage(int var1, long var2)
        {
            try
            {
                ArrayList var4 = this.transmitters;
                synchronized (this.transmitters)
                {
                    int var5 = this.transmitters.size();
                    if (this.optimizedReceiverCount == var5)
                    {
                        if (this.midiOutReceiver != null)
                        {
                            this.midiOutReceiver.send(new ShortMessage(var1), var2);
                        }
                    }
                    else
                    {
                        for (int var6 = 0; var6 < var5; ++var6)
                        {
                            Receiver var7 = this.transmitters.get(var6).getReceiver();
                            if (var7 != null)
                            {
                                var7.send(new ShortMessage(var1), var2);
                            }
                        }
                    }
                }
            }
            catch (InvalidMidiDataException var10)
            {
                ;
            }

        }

        void sendMessage(byte[] var1, long var2)
        {
            try
            {
                ArrayList var4 = this.transmitters;
                synchronized (this.transmitters)
                {
                    int var5 = this.transmitters.size();

                    for (int var6 = 0; var6 < var5; ++var6)
                    {
                        Receiver var7 = this.transmitters.get(var6).getReceiver();
                        if (var7 != null)
                        {
                            var7.send(new SysexMessageEx(var1), var2);
                        }
                    }

                }
            }
            catch (InvalidMidiDataException var10)
            {
                ;
            }
        }

        void sendMessage(MidiMessage msg, long var2)
        {
            if (msg instanceof SysexMessage)
            {
                this.sendMessage(msg.getMessage(), var2);
            }
            else
            {
                ArrayList var4 = this.transmitters;
                synchronized (this.transmitters)
                {
                    int var5 = this.transmitters.size();
                    for (int var6 = 0; var6 < var5; ++var6)
                    {
                        Receiver var7 = this.transmitters.get(var6).getReceiver();
                        if (var7 != null)
                        {
                            var7.send(msg, var2);
                        }
                    }
                }
            }
        }
    }

    class BasicTransmitter implements Transmitter
    {
        private Receiver receiver = null;
        TransmitterList tlist = null;

        protected BasicTransmitter()
        {
        }

        private void setTransmitterList(TransmitterList var1)
        {
            this.tlist = var1;
        }

        public final void setReceiver(Receiver var1)
        {
            if (this.tlist != null && this.receiver != var1)
            {
                this.tlist.receiverChanged(this, this.receiver, var1);
                this.receiver = var1;
            }

        }

        public final Receiver getReceiver()
        {
            return this.receiver;
        }

        public final void close()
        {
            AbstractSequencer.this.closeInternal(this);
            if (this.tlist != null)
            {
                this.tlist.receiverChanged(this, this.receiver, (Receiver) null);
                this.tlist.remove(this);
                this.tlist = null;
            }

        }

        public final MidiDevice getMidiDevice()
        {
            return AbstractSequencer.this;
        }
    }

    abstract class AbstractReceiver implements Receiver
    {
        private boolean open = true;

        AbstractReceiver()
        {
        }

        public final synchronized void send(MidiMessage var1, long var2)
        {
            if (!this.open)
            {
                throw new IllegalStateException("Receiver is not open");
            }
            else
            {
                this.implSend(var1, var2);
            }
        }

        abstract void implSend(MidiMessage var1, long var2);

        public final void close()
        {
            this.open = false;
            synchronized (AbstractSequencer.this.traRecLock)
            {
                AbstractSequencer.this.getReceiverList().remove(this);
            }

            AbstractSequencer.this.closeInternal(this);
        }

        public final MidiDevice getMidiDevice()
        {
            return AbstractSequencer.this;
        }

        final boolean isOpen()
        {
            return this.open;
        }
    }

    private class SysexMessageEx extends SysexMessage
    {
        SysexMessageEx(byte[] var1) throws InvalidMidiDataException
        {
            super(var1);
            if (var1.length == 0 || (var1[0] & 255) != 240 && (var1[0] & 255) != 247)
            {
                super.setMessage(var1, var1.length);
            }

        }

        byte[] getReadOnlyMessage()
        {
            return this.data;
        }

        public void setMessage(byte[] var1, int var2) throws InvalidMidiDataException
        {
            if (var1.length == 0 || (var1[0] & 255) != 240 && (var1[0] & 255) != 247)
            {
                super.setMessage(var1, var1.length);
            }

            this.length = var2;
            this.data = new byte[this.length];
            System.arraycopy(var1, 0, this.data, 0, var2);
        }
    }
}
