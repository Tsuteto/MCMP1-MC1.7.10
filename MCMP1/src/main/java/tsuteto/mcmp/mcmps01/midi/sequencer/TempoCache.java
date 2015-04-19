package tsuteto.mcmp.mcmps01.midi.sequencer;

import com.sun.media.sound.MidiUtils;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import java.util.ArrayList;

public class TempoCache {
    long[] ticks;
    int[] tempos;
    int snapshotIndex;
    int snapshotMicro;
    int currTempo;
    private boolean firstTempoIsFake;

    public TempoCache() {
        this.snapshotIndex = 0;
        this.snapshotMicro = 0;
        this.firstTempoIsFake = false;
        this.ticks = new long[1];
        this.tempos = new int[1];
        this.tempos[0] = 500000;
        this.snapshotIndex = 0;
        this.snapshotMicro = 0;
    }

    public TempoCache(Sequence var1) {
        this();
        this.refresh(var1);
    }

    public synchronized void refresh(Sequence var1) {
        ArrayList var2 = new ArrayList();
        Track[] var3 = var1.getTracks();
        int var5;
        int var6;
        MidiEvent var7;
        if(var3.length > 0) {
            Track var4 = var3[0];
            var5 = var4.size();

            for(var6 = 0; var6 < var5; ++var6) {
                var7 = var4.get(var6);
                MidiMessage var8 = var7.getMessage();
                if(MidiUtils.isMetaTempo(var8)) {
                    var2.add(var7);
                }
            }
        }

        int var9 = var2.size() + 1;
        this.firstTempoIsFake = true;
        if(var9 > 1 && ((MidiEvent)var2.get(0)).getTick() == 0L) {
            --var9;
            this.firstTempoIsFake = false;
        }

        this.ticks = new long[var9];
        this.tempos = new int[var9];
        var5 = 0;
        if(this.firstTempoIsFake) {
            this.ticks[0] = 0L;
            this.tempos[0] = 500000;
            ++var5;
        }

        for(var6 = 0; var6 < var2.size(); ++var5) {
            var7 = (MidiEvent)var2.get(var6);
            this.ticks[var5] = var7.getTick();
            this.tempos[var5] = MidiUtils.getTempoMPQ(var7.getMessage());
            ++var6;
        }

        this.snapshotIndex = 0;
        this.snapshotMicro = 0;
    }

    public int getCurrTempoMPQ() {
        return this.currTempo;
    }

    float getTempoMPQAt(long var1) {
        return this.getTempoMPQAt(var1, -1.0F);
    }

    synchronized float getTempoMPQAt(long var1, float var3) {
        for(int var4 = 0; var4 < this.ticks.length; ++var4) {
            if(this.ticks[var4] > var1) {
                if(var4 > 0) {
                    --var4;
                }

                if(var3 > 0.0F && var4 == 0 && this.firstTempoIsFake) {
                    return var3;
                }

                return (float)this.tempos[var4];
            }
        }

        return (float)this.tempos[this.tempos.length - 1];
    }
}
