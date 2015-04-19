package tsuteto.mcmp.mcmps01.midi.sequencer;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * Almost copied from com.sun.media.sound.MidiUtils
 */
public final class MidiUtils
{
    public static final int DEFAULT_TEMPO_MPQ = 500000;
    public static final int META_END_OF_TRACK_TYPE = 47;
    public static final int META_TEMPO_TYPE = 81;

    private MidiUtils()
    {
    }

    public static boolean isMetaEndOfTrack(MidiMessage var0)
    {
        if (var0.getLength() == 3 && var0.getStatus() == 255)
        {
            byte[] var1 = var0.getMessage();
            return (var1[1] & 255) == 47 && var1[2] == 0;
        }
        else
        {
            return false;
        }
    }

    public static boolean isMetaTempo(MidiMessage var0)
    {
        if (var0.getLength() == 6 && var0.getStatus() == 255)
        {
            byte[] var1 = var0.getMessage();
            return (var1[1] & 255) == 81 && var1[2] == 3;
        }
        else
        {
            return false;
        }
    }

    public static int getTempoMPQ(MidiMessage var0)
    {
        if (var0.getLength() == 6 && var0.getStatus() == 255)
        {
            byte[] var1 = var0.getMessage();
            if ((var1[1] & 255) == 81 && var1[2] == 3)
            {
                int var2 = var1[5] & 255 | (var1[4] & 255) << 8 | (var1[3] & 255) << 16;
                return var2;
            }
            else
            {
                return -1;
            }
        }
        else
        {
            return -1;
        }
    }

    public static double convertTempo(double var0)
    {
        if (var0 <= 0.0D)
        {
            var0 = 1.0D;
        }

        return 6.0E7D / var0;
    }

    public static long ticks2microsec(long var0, double var2, int var4)
    {
        return (long) ((double) var0 * var2 / (double) var4);
    }

    public static long microsec2ticks(long var0, double var2, int var4)
    {
        return (long) ((double) var0 * (double) var4 / var2);
    }

    public static long tick2microsecond(Sequence var0, long var1, TempoCache var3)
    {
        if (var0.getDivisionType() != 0.0F)
        {
            double var13 = (double) var1 / (double) (var0.getDivisionType() * (float) var0.getResolution());
            return (long) (1000000.0D * var13);
        }
        else
        {
            if (var3 == null)
            {
                var3 = new TempoCache(var0);
            }

            int var4 = var0.getResolution();
            long[] var5 = var3.ticks;
            int[] var6 = var3.tempos;
            int var7 = var6.length;
            int var8 = var3.snapshotIndex;
            int var9 = var3.snapshotMicro;
            long var10 = 0L;
            if (var8 <= 0 || var8 >= var7 || var5[var8] > var1)
            {
                var9 = 0;
                var8 = 0;
            }

            if (var7 > 0)
            {
                for (int var12 = var8 + 1; var12 < var7 && var5[var12] <= var1; var8 = var12++)
                {
                    var9 = (int) ((long) var9 + ticks2microsec(var5[var12] - var5[var12 - 1], (double) var6[var12 - 1], var4));
                }

                var10 = (long) var9 + ticks2microsec(var1 - var5[var8], (double) var6[var8], var4);
            }

            var3.snapshotIndex = var8;
            var3.snapshotMicro = var9;
            return var10;
        }
    }

    public static long microsecond2tick(Sequence var0, long var1, TempoCache var3)
    {
        if (var0.getDivisionType() != 0.0F)
        {
            double var16 = (double) var1 * (double) var0.getDivisionType() * (double) var0.getResolution() / 1000000.0D;
            long var17 = (long) var16;
            if (var3 != null)
            {
                var3.currTempo = (int) var3.getTempoMPQAt(var17);
            }

            return var17;
        }
        else
        {
            if (var3 == null)
            {
                var3 = new TempoCache(var0);
            }

            long[] var4 = var3.ticks;
            int[] var5 = var3.tempos;
            int var6 = var5.length;
            int var7 = var0.getResolution();
            long var8 = 0L;
            long var10 = 0L;
            boolean var12 = false;
            int var13 = 1;
            if (var1 > 0L && var6 > 0)
            {
                while (var13 < var6)
                {
                    long var14 = var8 + ticks2microsec(var4[var13] - var4[var13 - 1], (double) var5[var13 - 1], var7);
                    if (var14 > var1)
                    {
                        break;
                    }

                    var8 = var14;
                    ++var13;
                }

                var10 = var4[var13 - 1] + microsec2ticks(var1 - var8, (double) var5[var13 - 1], var7);
            }

            var3.currTempo = var5[var13 - 1];
            return var10;
        }
    }

    public static int tick2index(Track var0, long var1)
    {
        int var3 = 0;
        if (var1 > 0L)
        {
            int var4 = 0;
            int var5 = var0.size() - 1;

            while (var4 < var5)
            {
                var3 = var4 + var5 >> 1;
                long var6 = var0.get(var3).getTick();
                if (var6 == var1)
                {
                    break;
                }

                if (var6 < var1)
                {
                    if (var4 == var5 - 1)
                    {
                        ++var3;
                        break;
                    }

                    var4 = var3;
                }
                else
                {
                    var5 = var3;
                }
            }
        }

        return var3;
    }

}
