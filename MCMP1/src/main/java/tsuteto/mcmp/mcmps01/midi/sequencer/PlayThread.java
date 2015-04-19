package tsuteto.mcmp.mcmps01.midi.sequencer;

import tsuteto.mcmp.core.util.McmpLog;

import javax.sound.midi.Sequence;

class PlayThread implements Runnable
{
    McmpMidiSequencer sequencer;

    private Thread thread;
    private final Object lock = new Object();
    boolean interrupted = false;
    boolean isPumping = false;
    private final DataPump dataPump;

    PlayThread(McmpMidiSequencer sequencer)
    {
        this.sequencer = sequencer;
        this.dataPump = new DataPump(sequencer);
        this.thread = new Thread(this, "MCMP MIDI Sequencer");
        this.thread.setPriority(8);
        this.thread.start();
    }

    DataPump getDataPump()
    {
        return this.dataPump;
    }

    synchronized void setSequence(Sequence var1)
    {
        this.dataPump.setSequence(var1);
    }

    synchronized void start()
    {
        sequencer.running = true;
        if (!this.dataPump.hasCachedTempo())
        {
            long var1 = sequencer.getTickPosition();
            this.dataPump.setTempoMPQ(sequencer.tempoCache.getTempoMPQAt(var1));
        }

        this.dataPump.checkPointMillis = 0L;
        this.dataPump.clearNoteOnCache();
        this.dataPump.needReindex = true;
        this.dataPump.resetLoopCount();
        Object var5 = this.lock;
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
    }

    synchronized void stop()
    {
        this.playThreadImplStop();
        long var1 = System.nanoTime() / 1000000L;

        while (this.isPumping)
        {
            synchronized (this.lock)
            {
                try
                {
                    this.lock.wait(2000L);
                }
                catch (InterruptedException var6)
                {
                    ;
                }
            }

            if (System.nanoTime() / 1000000L - var1 > 1900L)
            {
                ;
            }
        }

    }

    void playThreadImplStop()
    {
        sequencer.running = false;
        Object var1 = this.lock;
        synchronized (this.lock)
        {
            this.lock.notifyAll();
        }
    }

    void close()
    {
        Thread var1 = null;
        synchronized (this)
        {
            this.interrupted = true;
            var1 = this.thread;
            this.thread = null;
        }

        if (var1 != null)
        {
            Object var2 = this.lock;
            synchronized (this.lock)
            {
                this.lock.notifyAll();
            }
        }

        if (var1 != null)
        {
            try
            {
                var1.join(2000L);
            }
            catch (InterruptedException var5)
            {
                ;
            }
        }

    }

    public void run()
    {
        while (!this.interrupted)
        {
            boolean var1 = false;
            boolean var2 = sequencer.running;
            this.isPumping = !this.interrupted && sequencer.running;

            while (!var1 && !this.interrupted && sequencer.running)
            {
                var1 = this.dataPump.pump();

                try
                {
                    Thread.sleep(1L);
                }
                catch (InterruptedException var8)
                {
                    ;
                }
            }
            this.playThreadImplStop();
            if (var2)
            {
                this.dataPump.notesOff(true);
            }

//            if (var1)
//            {
//                this.dataPump.setTickPos(sequencer.sequence.getTickLength());
//                MetaMessage var3 = new MetaMessage();
//
//                try
//                {
//                    var3.setMessage(47, new byte[0], 0);
//                }
//                catch (InvalidMidiDataException var7)
//                {
//                    ;
//                }
//
//                sequencer.sendMetaEvents(var3);
//            }

            Object var10 = this.lock;
            synchronized (this.lock)
            {
                this.isPumping = false;
                this.lock.notifyAll();

                if (var1 && var2)
                {
                    this.sequencer.onFinishedPlaying();
                }

                while (!sequencer.running && !this.interrupted)
                {
                    try
                    {
                        this.lock.wait();
                    }
                    catch (Exception var6)
                    {
                        ;
                    }
                }
            }
        }
        McmpLog.debug("Shut down");
    }
}
