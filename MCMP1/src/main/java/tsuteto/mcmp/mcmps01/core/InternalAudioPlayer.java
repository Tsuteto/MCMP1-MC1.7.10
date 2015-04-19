package tsuteto.mcmp.mcmps01.core;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tsuteto.mcmp.core.audio.internal.InternalAudioPlayerBase;
import tsuteto.mcmp.core.audio.internal.InternalSound;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

@SideOnly(Side.CLIENT)
public class InternalAudioPlayer
{
    public static Queue<InternalSound> soundQueue = new LinkedBlockingQueue<InternalSound>();

    private InternalAudioPlayerBase internalAudio = new InternalAudioPlayerBase();

    @SubscribeEvent
    public void tickEnd(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            InternalSound entry;
            while ((entry = soundQueue.poll()) != null)
            {
                internalAudio.play(entry);
            }
        }
    }
}
