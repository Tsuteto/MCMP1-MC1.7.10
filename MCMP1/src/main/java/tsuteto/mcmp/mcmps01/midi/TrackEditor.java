package tsuteto.mcmp.mcmps01.midi;

import com.google.common.collect.Lists;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Track;
import java.util.ArrayList;
import java.util.List;

public class TrackEditor
{
    private Track track;
    private int resolution;
    private List<List<MidiEvent>> events;
    private List<MidiEvent> removed = Lists.newArrayList();
    private List<MidiEvent> added = Lists.newArrayList();

    public TrackEditor(Track track, int resolution)
    {
        this.track = track;
        this.resolution = resolution;
        this.update();
    }

    public void update()
    {
        this.events = new ArrayList<List<MidiEvent>>((int)(track.ticks() / resolution) + 1);

        for (int i = 0; i < track.size(); i++)
        {
            MidiEvent e = track.get(i);
            long tick = e.getTick();
            int beat = (int)(tick / resolution);
            if (events.get(beat) == null)
            {
                events.set(beat, new ArrayList<MidiEvent>());
            }
            events.get(beat).add(e);
        }
    }

    public List<MidiEvent> getEvents(int measureFrom, int measureTo)
    {
        List<MidiEvent> list = Lists.newArrayList();
        for (int m = measureFrom; m <= measureTo; m++)
        {
            list.addAll(events.get(m));
        }

        for (MidiEvent e : this.added)
        {
            if (e.getTick() >= measureFrom * resolution && e.getTick() < (measureTo + 1) * resolution)
            {
                list.add(e);
            }
        }
        return list;
    }

    public void addEvent(MidiEvent event)
    {
        this.added.add(event);
    }

    public void removeEvent(MidiEvent event)
    {
        if (this.added.contains(event))
        {
            this.added.remove(event);
        }
        else
        {
            this.removed.add(event);
        }
    }

    public void save()
    {
        for (MidiEvent e : this.added)
        {
            track.add(e);
        }

        for (MidiEvent e : this.removed)
        {
            track.remove(e);
        }

        this.added.clear();
        this.removed.clear();

        this.update();
    }
}
