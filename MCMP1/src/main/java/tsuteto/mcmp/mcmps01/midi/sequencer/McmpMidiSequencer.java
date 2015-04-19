package tsuteto.mcmp.mcmps01.midi.sequencer;

import tsuteto.mcmp.core.util.McmpLog;

import javax.sound.midi.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class McmpMidiSequencer extends AbstractSequencer implements Sequencer
{
    boolean running = false;
    final TempoCache tempoCache = new TempoCache();
    private static final SyncMode[] masterSyncModes;
    private static final SyncMode[] slaveSyncModes;
    private static final SyncMode masterSyncMode;
    private static final SyncMode slaveSyncMode;
    Sequence sequence = null;
    private double cacheTempoMPQ = -1.0D;
    private float cacheTempoFactor = -1.0F;
    boolean[] trackMuted = null;
    boolean[] trackSolo = null;
    private PlayThread playThread;
    long loopStart = 0L;
    long loopEnd = -1L;
    int loopCount = 0;
    private final ArrayList metaEventListeners = new ArrayList();

    static
    {
        masterSyncModes = new SyncMode[]{SyncMode.INTERNAL_CLOCK};
        slaveSyncModes = new SyncMode[]{SyncMode.NO_SYNC};
        masterSyncMode = SyncMode.INTERNAL_CLOCK;
        slaveSyncMode = SyncMode.NO_SYNC;
    }

    public McmpMidiSequencer()
    {
        super(new SequencerInfo());
    }

    @Override
    public void setSequence(Sequence sequence) throws InvalidMidiDataException
    {
        this.sequence = sequence;
    }

    @Override
    public void setSequence(InputStream stream) throws IOException, InvalidMidiDataException
    {
        if (stream == null)
        {
            this.setSequence((Sequence) null);
        }
        else
        {
            Sequence var2 = MidiSystem.getSequence(stream);
            this.setSequence(var2);
        }
    }

    @Override
    public Sequence getSequence()
    {
        return this.sequence;
    }

    @Override
    public void start()
    {
        if (!this.isOpen())
        {
            McmpLog.warn("MIDI sequencer not open");
        }
        else if (this.sequence == null)
        {
            McmpLog.warn("MIDI sequence not set");
        }
        else if (!this.running)
        {
            this.implStart();
        }
    }

    @Override
    public void stop()
    {
        if (!this.isOpen())
        {
            McmpLog.warn("sequencer not open");
        }
        else
        {
            if (this.running)
            {
                this.implStop();
            }
        }
    }

    @Override
    public boolean isRunning()
    {
        return this.running;
    }

    public boolean isPlaying()
    {
        return this.playThread != null;
    }

    @Override
    public void startRecording()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stopRecording()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRecording()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordEnable(Track track, int channel)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recordDisable(Track track)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getTempoInBPM()
    {
        return (float) MidiUtils.convertTempo((double) this.getTempoInMPQ());
    }

    @Override
    public void setTempoInBPM(float bpm)
    {
        if (bpm <= 0.0F)
        {
            bpm = 1.0F;
        }

        this.setTempoInMPQ((float) MidiUtils.convertTempo((double) bpm));
    }

    @Override
    public float getTempoInMPQ()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTempoInMPQ(float mpq)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setTempoFactor(float factor)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getTempoFactor()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getTickLength()
    {
        return this.sequence == null ? 0L : this.sequence.getTickLength();
    }

    @Override
    public synchronized long getTickPosition()
    {
        return this.getDataPump() != null && this.sequence != null ? this.getDataPump().getTickPos() : 0L;
    }

    @Override
    public synchronized void setTickPosition(long tick)
    {
        if (tick >= 0L)
        {
            if (this.getDataPump() == null)
            {
                if (tick != 0L)
                {
                    ;
                }
            }
            else if (this.sequence == null)
            {
                if (tick != 0L)
                {
                    ;
                }
            }
            else
            {
                this.getDataPump().setTickPos(tick);
            }

        }
    }

    @Override
    public long getMicrosecondLength()
    {
        return this.sequence == null ? 0L : this.sequence.getMicrosecondLength();
    }

    public long getMicrosecondPosition()
    {
        if (this.getDataPump() != null && this.sequence != null)
        {
            TempoCache var1 = this.tempoCache;
            synchronized (this.tempoCache)
            {
                return MidiUtils.tick2microsecond(this.sequence, this.getDataPump().getTickPos(), this.tempoCache);
            }
        }
        else
        {
            return 0L;
        }
    }

    @Override
    public void setMicrosecondPosition(long microseconds)
    {
        if (microseconds >= 0L)
        {
            if (this.getDataPump() == null)
            {
                if (microseconds != 0L)
                {
                    ;
                }
            }
            else if (this.sequence == null)
            {
                if (microseconds != 0L)
                {
                    ;
                }
            }
            else
            {
                TempoCache var3 = this.tempoCache;
                synchronized (this.tempoCache)
                {
                    this.setTickPosition(MidiUtils.microsecond2tick(this.sequence, microseconds, this.tempoCache));
                }
            }

        }
    }

    @Override
    public void setMasterSyncMode(SyncMode sync)
    {

    }

    @Override
    public SyncMode getMasterSyncMode()
    {
        return masterSyncMode;
    }

    public SyncMode[] getMasterSyncModes()
    {
        SyncMode[] var1 = new SyncMode[masterSyncModes.length];
        System.arraycopy(masterSyncModes, 0, var1, 0, masterSyncModes.length);
        return var1;
    }

    @Override
    public void setSlaveSyncMode(SyncMode sync)
    {

    }

    @Override
    public SyncMode getSlaveSyncMode()
    {
        return slaveSyncMode;
    }

    public SyncMode[] getSlaveSyncModes()
    {
        SyncMode[] var1 = new SyncMode[slaveSyncModes.length];
        System.arraycopy(slaveSyncModes, 0, var1, 0, slaveSyncModes.length);
        return var1;
    }

    int getTrackCount()
    {
        Sequence var1 = this.getSequence();
        return var1 != null ? this.sequence.getTracks().length : 0;
    }

    @Override
    public void setTrackMute(int track, boolean mute)
    {
        int var3 = this.getTrackCount();
        if (track >= 0 && track < this.getTrackCount())
        {
            this.trackMuted = ensureBoolArraySize(this.trackMuted, var3);
            this.trackMuted[track] = mute;
            if (this.getDataPump() != null)
            {
                this.getDataPump().muteSoloChanged();
            }

        }
    }

    @Override
    public boolean getTrackMute(int track)
    {
        return track >= 0 && track < this.getTrackCount() && (this.trackMuted != null && this.trackMuted.length > track && this.trackMuted[track]);
    }

    @Override
    public void setTrackSolo(int track, boolean solo)
    {
        int var3 = this.getTrackCount();
        if (track >= 0 && track < this.getTrackCount())
        {
            this.trackSolo = ensureBoolArraySize(this.trackSolo, var3);
            this.trackSolo[track] = solo;
            if (this.getDataPump() != null)
            {
                this.getDataPump().muteSoloChanged();
            }

        }
    }

    @Override
    public boolean getTrackSolo(int track)
    {
        return track >= 0 && track < this.getTrackCount() && (this.trackSolo != null && this.trackSolo.length > track && this.trackSolo[track]);
    }

    @Override
    public boolean addMetaEventListener(MetaEventListener listener)
    {
        ArrayList var2 = this.metaEventListeners;
        synchronized (this.metaEventListeners)
        {
            if (!this.metaEventListeners.contains(listener))
            {
                this.metaEventListeners.add(listener);
            }

            return true;
        }
    }

    @Override
    public void removeMetaEventListener(MetaEventListener listener)
    {
        synchronized (this.metaEventListeners)
        {
            int var3 = this.metaEventListeners.indexOf(listener);
            if (var3 >= 0)
            {
                this.metaEventListeners.remove(var3);
            }

        }
    }

    @Override
    public int[] addControllerEventListener(ControllerEventListener listener, int[] controllers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int[] removeControllerEventListener(ControllerEventListener listener, int[] controllers)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLoopStartPoint(long tick)
    {
        if (tick <= this.getTickLength() && (this.loopEnd == -1L || tick <= this.loopEnd) && tick >= 0L)
        {
            this.loopStart = tick;
        }
        else
        {
            throw new IllegalArgumentException("invalid loop start point: " + tick);
        }
    }

    @Override
    public long getLoopStartPoint()
    {
        return this.loopStart;
    }

    @Override
    public void setLoopEndPoint(long tick)
    {
        if (tick <= this.getTickLength() && (this.loopStart <= tick || tick == -1L) && tick >= -1L)
        {
            this.loopEnd = tick;
        }
        else
        {
            throw new IllegalArgumentException("invalid loop end point: " + tick);
        }
    }

    @Override
    public long getLoopEndPoint()
    {
        return this.loopEnd;
    }

    @Override
    public void setLoopCount(int count)
    {
        if (count != -1 && count < 0)
        {
            throw new IllegalArgumentException("illegal value for loop count: " + count);
        }
        else
        {
            this.loopCount = count;
            if (this.getDataPump() != null)
            {
                this.getDataPump().resetLoopCount();
            }

        }
    }

    @Override
    public int getLoopCount()
    {
        return this.loopCount;
    }

    protected void implOpen() throws MidiUnavailableException
    {
        this.playThread = new PlayThread(this);
        if (this.sequence != null)
        {
            this.playThread.setSequence(this.sequence);
        }

        this.propagateCaches();
        McmpLog.debug("MCMP Sequencer started up");
    }

    private synchronized void setCaches()
    {
        this.cacheTempoFactor = this.getTempoFactor();
        this.cacheTempoMPQ = (double) this.getTempoInMPQ();
    }

    protected synchronized void implClose()
    {
        if (this.playThread != null)
        {
            this.playThread.close();
            this.playThread = null;
        }

        super.implClose();
        this.sequence = null;
        this.running = false;
        this.cacheTempoMPQ = -1.0D;
        this.cacheTempoFactor = -1.0F;
        this.trackMuted = null;
        this.trackSolo = null;
        this.loopStart = 0L;
        this.loopEnd = -1L;
        this.loopCount = 0;

        McmpLog.debug("MCMP Sequencer closed");
    }

    private synchronized void propagateCaches()
    {
        if (this.sequence != null && this.isOpen())
        {
            if (this.cacheTempoFactor != -1.0F)
            {
                this.setTempoFactor(this.cacheTempoFactor);
            }

            if (this.cacheTempoMPQ == -1.0D)
            {
                this.setTempoInMPQ((new TempoCache(this.sequence)).getTempoMPQAt(this.getTickPosition()));
            }
            else
            {
                this.setTempoInMPQ((float) this.cacheTempoMPQ);
            }
        }

    }

    void implStart()
    {
        if (this.playThread != null)
        {
            this.tempoCache.refresh(this.sequence);
            if (!this.running)
            {
                this.running = true;
                this.playThread.start();
            }

        }
    }

    void implStop()
    {
        if (this.playThread != null)
        {
            if (this.running)
            {
                this.running = false;
                this.playThread.stop();
            }
        }
    }

    public boolean needCaching()
    {
        return !this.isOpen() || this.sequence == null || this.playThread == null;
    }

    private DataPump getDataPump()
    {
        return this.playThread != null ? this.playThread.getDataPump() : null;
    }

    public TempoCache getTempoCache()
    {
        return this.tempoCache;
    }

    @Override
    protected Transmitter createTransmitter() throws MidiUnavailableException
    {
        return new SequencerTransmitter();
    }

    private static boolean[] ensureBoolArraySize(boolean[] var0, int var1)
    {
        if (var0 == null)
        {
            return new boolean[var1];
        }
        else if (var0.length < var1)
        {
            boolean[] var2 = new boolean[var1];
            System.arraycopy(var0, 0, var2, 0, var0.length);
            return var2;
        }
        else
        {
            return var0;
        }
    }

    private static class SequencerInfo extends MidiDevice.Info
    {
        private static final String name = "MCMP MIDI Sequencer";
        private static final String vendor = "MCMP";
        private static final String description = "MIDI sequencer for Note block instruments";
        private static final String version = "v1.0";

        private SequencerInfo()
        {
            super(name, vendor, description, version);
        }
    }

    private class SequencerTransmitter extends BasicTransmitter
    {
        private SequencerTransmitter() {
            super();
        }
    }

}
