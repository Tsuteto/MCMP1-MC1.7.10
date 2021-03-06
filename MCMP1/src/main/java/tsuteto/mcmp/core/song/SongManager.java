package tsuteto.mcmp.core.song;

import java.util.List;

public class SongManager
{

    private SongPool songPool;

    public void setSongPool(SongPool songPool)
    {
        this.songPool = songPool;
    }

    public SongPool getSongPool()
    {
        return songPool;
    }

    public List<SongInfo> getSongList()
    {
        return songPool.songList;
    }

    public int countSong()
    {
        return songPool.songMap.size();
    }

    public SongInfo getSongInfo(MediaSongEntry entry)
    {
        if (songPool.songMap.containsKey(entry.id))
        {
            return songPool.songMap.get(entry.id);
        }
        else
        {
            for (String s : songPool.songMap.keySet())
            {
                SongInfo info = songPool.songMap.get(s);
                if (entry.id.equals(info.songName))
                {
                    return info;
                }
            }
            return null;
        }
    }
}
