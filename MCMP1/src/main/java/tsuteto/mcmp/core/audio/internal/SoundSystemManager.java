package tsuteto.mcmp.core.audio.internal;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.audio.SoundManager;
import net.minecraftforge.client.event.sound.SoundLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Level;
import paulscode.sound.SoundSystem;
import tsuteto.mcmp.core.util.McmpLog;

public class SoundSystemManager
{
    public static final SoundSystemManager INSTANCE = new SoundSystemManager();

    public SoundSystem soundSystem;
    public SoundManager soundManager;
    public SoundManagerAccessor managerAccessor = new SoundManagerAccessor();
    public boolean enabled = true;

    private Thread initThread;

    public static void init()
    {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    private SoundSystemManager()
    {
    }

    @SubscribeEvent
    public void onSoundSetup(SoundLoadEvent event)
    {
        if (this.enabled)
        {
            //this.objectToAudioSourceMap.clear();
            Thread thread = this.initThread;
            if (thread != null)
            {
                thread.interrupt();

                try
                {
                    thread.join();
                }
                catch (InterruptedException ignored)
                {
                }
            }

            McmpLog.info("MCMP-1 audio starting.");
            this.soundManager = this.managerAccessor.getSoundManager();
            this.initThread = new Thread(new Runnable()
            {
                public void run()
                {
                    while (true)
                    {
                        try
                        {
                            if (!Thread.currentThread().isInterrupted())
                            {
                                boolean e = SoundSystemManager.this.managerAccessor.loaded();

                                if (!e)
                                {
                                    Thread.sleep(100L);
                                    continue;
                                }

                                soundSystem = SoundSystemManager.this.managerAccessor.getSoundSystem();
                                if (soundSystem == null)
                                {
                                    McmpLog.log(Level.WARN, "Failed to start MCMP-1 audio.");
                                    SoundSystemManager.this.enabled = false;
                                }
                                else
                                {
                                    McmpLog.info("MCMP-1 audio is ready.");
                                }
                            }
                        }
                        catch (InterruptedException ignored)
                        {
                        }

                        SoundSystemManager.this.initThread = null;
                        return;
                    }
                }
            }, "MCMP-1 Audio Initializer");
            this.initThread.setDaemon(true);
            this.initThread.start();
        }
    }
}
