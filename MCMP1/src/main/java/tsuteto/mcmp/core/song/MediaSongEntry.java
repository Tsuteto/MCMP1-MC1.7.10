package tsuteto.mcmp.core.song;

import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.cassettetape.ItemCassetteTape.Source;

public class MediaSongEntry
{
    public Source source;
    public String id;

    public MediaSongEntry(byte source, String id)
    {
        this.source = Source.values()[source];
        this.id = id;
    }

    public MediaSongEntry(Source source, String id)
    {
        this.source = source;
        this.id = id;
    }
}