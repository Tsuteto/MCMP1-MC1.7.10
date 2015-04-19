package tsuteto.mcmp.mcmps01.midi.sequencer;

import com.sun.media.sound.MidiUtils;

import javax.sound.midi.*;

/**
 * Almost copied from com.sun.media.sound.RealTimeSequencer.DataPump
 */
class DataPump
{
    private final McmpMidiSequencer sequencer;

    private float currTempo;
    private float tempoFactor;
    private float inverseTempoFactor;
    private long ignoreTempoEventAt;
    private int resolution;
    private float divisionType;
    long checkPointMillis;
    private long checkPointTick;
    private int[] noteOnCache;
    private Track[] tracks;
    private boolean[] trackDisabled;
    private int[] trackReadPos;
    private long lastTick;
    boolean needReindex = false;
    private int currLoopCounter = 0;

    DataPump(McmpMidiSequencer sequencer)
    {
        this.sequencer = sequencer;
        this.init();
    }

    synchronized void init()
    {
        this.ignoreTempoEventAt = -1L;
        this.tempoFactor = 1.0F;
        this.inverseTempoFactor = 1.0F;
        this.noteOnCache = new int[128];
        this.tracks = null;
        this.trackDisabled = null;
    }

    synchronized void setTickPos(long var1)
    {
        this.lastTick = var1;
        if (sequencer.running)
        {
            this.notesOff(false);
        }

        if (!sequencer.running && var1 <= 0L)
        {
            this.needReindex = true;
        }
        else
        {
            this.chaseEvents(var1, var1);
        }

        if (!this.hasCachedTempo())
        {
            this.setTempoMPQ(sequencer.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
            this.ignoreTempoEventAt = -1L;
        }

        this.checkPointMillis = 0L;
    }

    long getTickPos()
    {
        return this.lastTick;
    }

    boolean hasCachedTempo()
    {
        if (this.ignoreTempoEventAt != this.lastTick)
        {
            this.ignoreTempoEventAt = -1L;
        }

        return this.ignoreTempoEventAt >= 0L;
    }

    synchronized void setTempoMPQ(float var1)
    {
        if (var1 > 0.0F && var1 != this.currTempo)
        {
            this.ignoreTempoEventAt = this.lastTick;
            this.currTempo = var1;
            this.checkPointMillis = 0L;
        }

    }

    float getTempoMPQ()
    {
        return this.currTempo;
    }

    synchronized void setTempoFactor(float var1)
    {
        if (var1 > 0.0F && var1 != this.tempoFactor)
        {
            this.tempoFactor = var1;
            this.inverseTempoFactor = 1.0F / var1;
            this.checkPointMillis = 0L;
        }

    }

    float getTempoFactor()
    {
        return this.tempoFactor;
    }

    synchronized void muteSoloChanged()
    {
        boolean[] var1 = this.makeDisabledArray();
        if (sequencer.running)
        {
            this.applyDisabledTracks(this.trackDisabled, var1);
        }

        this.trackDisabled = var1;
    }

    synchronized void setSequence(Sequence var1)
    {
        if (var1 == null)
        {
            this.init();
        }
        else
        {
            this.tracks = var1.getTracks();
            this.muteSoloChanged();
            this.resolution = var1.getResolution();
            this.divisionType = var1.getDivisionType();
            this.trackReadPos = new int[this.tracks.length];
            this.checkPointMillis = 0L;
            this.needReindex = true;
        }
    }

    synchronized void resetLoopCount()
    {
        this.currLoopCounter = sequencer.loopCount;
    }

    void clearNoteOnCache()
    {
        for (int var1 = 0; var1 < 128; ++var1)
        {
            this.noteOnCache[var1] = 0;
        }

    }

    void notesOff(boolean var1)
    {
        int var2 = 0;

        for (int var3 = 0; var3 < 16; ++var3)
        {
            int var4 = 1 << var3;

            for (int var5 = 0; var5 < 128; ++var5)
            {
                if ((this.noteOnCache[var5] & var4) != 0)
                {
                    this.noteOnCache[var5] ^= var4;
                    sequencer.getTransmitterList().sendMessage(144 | var3 | var5 << 8, -1L);
                    ++var2;
                }
            }

            sequencer.getTransmitterList().sendMessage(176 | var3 | 31488, -1L);
            sequencer.getTransmitterList().sendMessage(176 | var3 | 16384, -1L);
            if (var1)
            {
                sequencer.getTransmitterList().sendMessage(176 | var3 | 30976, -1L);
                ++var2;
            }
        }

    }

    private boolean[] makeDisabledArray()
    {
        if (this.tracks == null)
        {
            return null;
        }
        else
        {
            boolean[] var1 = new boolean[this.tracks.length];
            boolean[] var2;
            boolean[] var3;
            synchronized (sequencer)
            {
                var3 = sequencer.trackMuted;
                var2 = sequencer.trackSolo;
            }

            boolean var7 = false;
            int var5;
            if (var2 != null)
            {
                for (var5 = 0; var5 < var2.length; ++var5)
                {
                    if (var2[var5])
                    {
                        var7 = true;
                        break;
                    }
                }
            }

            if (var7)
            {
                for (var5 = 0; var5 < var1.length; ++var5)
                {
                    var1[var5] = var5 >= var2.length || !var2[var5];
                }
            }
            else
            {
                for (var5 = 0; var5 < var1.length; ++var5)
                {
                    var1[var5] = var3 != null && var5 < var3.length && var3[var5];
                }
            }

            return var1;
        }
    }

    private void sendNoteOffIfOn(Track var1, long var2)
    {
        int var4 = var1.size();
        int var5 = 0;

        try
        {
            for (int var6 = 0; var6 < var4; ++var6)
            {
                MidiEvent var7 = var1.get(var6);
                if (var7.getTick() > var2)
                {
                    break;
                }

                MidiMessage var8 = var7.getMessage();
                int var9 = var8.getStatus();
                int var10 = var8.getLength();
                if (var10 == 3 && (var9 & 240) == 144)
                {
                    int var11 = -1;
                    if (var8 instanceof ShortMessage)
                    {
                        ShortMessage var12 = (ShortMessage) var8;
                        if (var12.getData2() > 0)
                        {
                            var11 = var12.getData1();
                        }
                    }
                    else
                    {
                        byte[] var14 = var8.getMessage();
                        if ((var14[2] & 127) > 0)
                        {
                            var11 = var14[1] & 127;
                        }
                    }

                    if (var11 >= 0)
                    {
                        int var15 = 1 << (var9 & 15);
                        if ((this.noteOnCache[var11] & var15) != 0)
                        {
                            sequencer.getTransmitterList().sendMessage(var9 | var11 << 8, -1L);
                            this.noteOnCache[var11] &= ~var15;
                            ++var5;
                        }
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException var13)
        {
            ;
        }

    }

    private void applyDisabledTracks(boolean[] var1, boolean[] var2)
    {
        byte[][] var3 = null;
        synchronized (sequencer)
        {
            for (int var5 = 0; var5 < var2.length; ++var5)
            {
                if ((var1 == null || var5 >= var1.length || !var1[var5]) && var2[var5])
                {
                    if (this.tracks.length > var5)
                    {
                        this.sendNoteOffIfOn(this.tracks[var5], this.lastTick);
                    }
                }
                else if (var1 != null && var5 < var1.length && var1[var5] && !var2[var5])
                {
                    if (var3 == null)
                    {
                        var3 = new byte[128][16];
                    }

                    this.chaseTrackEvents(var5, 0L, this.lastTick, true, var3);
                }
            }

        }
    }

    private void chaseTrackEvents(int var1, long var2, long var4, boolean var6, byte[][] var7)
    {
        if (var2 > var4)
        {
            var2 = 0L;
        }

        byte[] var8 = new byte[16];

        int var10;
        for (int var9 = 0; var9 < 16; ++var9)
        {
            var8[var9] = -1;

            for (var10 = 0; var10 < 128; ++var10)
            {
                var7[var10][var9] = -1;
            }
        }

        Track var18 = this.tracks[var1];
        var10 = var18.size();

        int var11;
        int var15;
        try
        {
            for (var11 = 0; var11 < var10; ++var11)
            {
                MidiEvent var12 = var18.get(var11);
                if (var12.getTick() >= var4)
                {
                    if (var6 && var1 < this.trackReadPos.length)
                    {
                        this.trackReadPos[var1] = var11 > 0 ? var11 - 1 : 0;
                    }
                    break;
                }

                MidiMessage var13 = var12.getMessage();
                int var14 = var13.getStatus();
                var15 = var13.getLength();
                ShortMessage var16;
                byte[] var22;
                if (var15 == 3 && (var14 & 240) == 176)
                {
                    if (var13 instanceof ShortMessage)
                    {
                        var16 = (ShortMessage) var13;
                        var7[var16.getData1() & 127][var14 & 15] = (byte) var16.getData2();
                    }
                    else
                    {
                        var22 = var13.getMessage();
                        var7[var22[1] & 127][var14 & 15] = var22[2];
                    }
                }

                if (var15 == 2 && (var14 & 240) == 192)
                {
                    if (var13 instanceof ShortMessage)
                    {
                        var16 = (ShortMessage) var13;
                        var8[var14 & 15] = (byte) var16.getData1();
                    }
                    else
                    {
                        var22 = var13.getMessage();
                        var8[var14 & 15] = var22[1];
                    }
                }
            }
        }
        catch (ArrayIndexOutOfBoundsException var17)
        {
            ;
        }

        var11 = 0;

        for (int var19 = 0; var19 < 16; ++var19)
        {
            for (int var20 = 0; var20 < 128; ++var20)
            {
                byte var21 = var7[var20][var19];
                if (var21 >= 0)
                {
                    var15 = 176 | var19 | var20 << 8 | var21 << 16;
                    sequencer.getTransmitterList().sendMessage(var15, -1L);
                    ++var11;
                }
            }

            if (var8[var19] >= 0)
            {
                sequencer.getTransmitterList().sendMessage(192 | var19 | var8[var19] << 8, -1L);
            }

            if (var8[var19] >= 0 || var2 == 0L || var4 == 0L)
            {
                sequencer.getTransmitterList().sendMessage(224 | var19 | 4194304, -1L);
                sequencer.getTransmitterList().sendMessage(176 | var19 | 16384, -1L);
            }
        }

    }

    synchronized void chaseEvents(long var1, long var3)
    {
        byte[][] var5 = new byte[128][16];

        for (int var6 = 0; var6 < this.tracks.length; ++var6)
        {
            if (this.trackDisabled == null || this.trackDisabled.length <= var6 || !this.trackDisabled[var6])
            {
                this.chaseTrackEvents(var6, var1, var3, true, var5);
            }
        }

    }

    private long getCurrentTimeMillis()
    {
        return System.nanoTime() / 1000000L;
    }

    private long millis2tick(long var1)
    {
        if (this.divisionType != 0.0F)
        {
            double var3 = (double) var1 * (double) this.tempoFactor * (double) this.divisionType * (double) this.resolution / 1000.0D;
            return (long) var3;
        }
        else
        {
            return MidiUtils.microsec2ticks(var1 * 1000L, (double) (this.currTempo * this.inverseTempoFactor), this.resolution);
        }
    }

    private long tick2millis(long var1)
    {
        if (this.divisionType != 0.0F)
        {
            double var3 = (double) var1 * 1000.0D / ((double) this.tempoFactor * (double) this.divisionType * (double) this.resolution);
            return (long) var3;
        }
        else
        {
            return MidiUtils.ticks2microsec(var1, (double) (this.currTempo * this.inverseTempoFactor), this.resolution) / 1000L;
        }
    }

    private void ReindexTrack(int var1, long var2)
    {
        if (var1 < this.trackReadPos.length && var1 < this.tracks.length)
        {
            this.trackReadPos[var1] = MidiUtils.tick2index(this.tracks[var1], var2);
        }

    }

    private boolean dispatchMessage(int var1, MidiEvent var2)
    {
        boolean var3 = false;
        MidiMessage var4 = var2.getMessage();
        int var5 = var4.getStatus();
        int var6 = var4.getLength();
        int var10;
        if (var5 == 255 && var6 >= 2)
        {
            if (var1 == 0)
            {
                var10 = MidiUtils.getTempoMPQ(var4);
                if (var10 > 0)
                {
                    if (var2.getTick() != this.ignoreTempoEventAt)
                    {
                        this.setTempoMPQ((float) var10);
                        var3 = true;
                    }

                    this.ignoreTempoEventAt = -1L;
                }
            }

            //sequencer.sendMetaEvents(var4);
        }
        else
        {
            sequencer.getTransmitterList().sendMessage(var4, -1L);
            switch (var5 & 240)
            {
                case 128:
                    var10 = ((ShortMessage) var4).getData1() & 127;
                    this.noteOnCache[var10] &= '\uffff' ^ 1 << (var5 & 15);
                    break;
                case 144:
                    ShortMessage var7 = (ShortMessage) var4;
                    int var8 = var7.getData1() & 127;
                    int var9 = var7.getData2() & 127;
                    if (var9 > 0)
                    {
                        this.noteOnCache[var8] |= 1 << (var5 & 15);
                    }
                    else
                    {
                        this.noteOnCache[var8] &= '\uffff' ^ 1 << (var5 & 15);
                    }
                    break;
                case 176:
                    //sequencer.sendControllerEvents(var4);
            }
        }

        return var3;
    }

    synchronized boolean pump()
    {
        long var3 = this.lastTick;
        boolean var6 = false;
        boolean var7 = false;
        boolean var8 = false;
        long var1 = this.getCurrentTimeMillis();
        boolean var9 = false;

        do
        {
            var6 = false;
            int var10;
            if (this.needReindex)
            {
                if (this.trackReadPos.length < this.tracks.length)
                {
                    this.trackReadPos = new int[this.tracks.length];
                }

                for (var10 = 0; var10 < this.tracks.length; ++var10)
                {
                    this.ReindexTrack(var10, var3);
                }

                this.needReindex = false;
                this.checkPointMillis = 0L;
            }

            if (this.checkPointMillis == 0L)
            {
                var1 = this.getCurrentTimeMillis();
                this.checkPointMillis = var1;
                var3 = this.lastTick;
                this.checkPointTick = var3;
            }
            else
            {
                var3 = this.checkPointTick + this.millis2tick(var1 - this.checkPointMillis);
                if (sequencer.loopEnd != -1L && (sequencer.loopCount > 0 && this.currLoopCounter > 0 || sequencer.loopCount == -1) && this.lastTick <= sequencer.loopEnd && var3 >= sequencer.loopEnd)
                {
                    var3 = sequencer.loopEnd - 1L;
                    var7 = true;
                }

                this.lastTick = var3;
            }

            int var16 = 0;

            for (var10 = 0; var10 < this.tracks.length; ++var10)
            {
                try
                {
                    boolean var11 = this.trackDisabled[var10];
                    Track var12 = this.tracks[var10];
                    int var13 = this.trackReadPos[var10];
                    int var14 = var12.size();

                    MidiEvent var5;
                    while (!var6 && var13 < var14 && (var5 = var12.get(var13)).getTick() <= var3)
                    {
                        if (var13 == var14 - 1 && MidiUtils.isMetaEndOfTrack(var5.getMessage()))
                        {
                            var13 = var14;
                            break;
                        }

                        ++var13;
                        if (!var11 || var10 == 0 && MidiUtils.isMetaTempo(var5.getMessage()))
                        {
                            var6 = this.dispatchMessage(var10, var5);
                        }
                    }

                    if (var13 >= var14)
                    {
                        ++var16;
                    }

                    this.trackReadPos[var10] = var13;
                }
                catch (Exception var15)
                {
                    if (var15 instanceof ArrayIndexOutOfBoundsException)
                    {
                        this.needReindex = true;
                        var6 = true;
                    }
                }

                if (var6)
                {
                    break;
                }
            }

            var8 = var16 == this.tracks.length;
            if (var7 || (sequencer.loopCount > 0 && this.currLoopCounter > 0 || sequencer.loopCount == -1) && !var6 && sequencer.loopEnd == -1L && var8)
            {
                long var17 = this.checkPointMillis;
                long var18 = sequencer.loopEnd;
                if (var18 == -1L)
                {
                    var18 = this.lastTick;
                }

                if (sequencer.loopCount != -1)
                {
                    --this.currLoopCounter;
                }

                this.setTickPos(sequencer.loopStart);
                this.checkPointMillis = var17 + this.tick2millis(var18 - this.checkPointTick);
                this.checkPointTick = sequencer.loopStart;
                this.needReindex = false;
                var6 = false;
                var7 = false;
                var8 = false;
            }
        } while (var6);

        return var8;
    }
}
