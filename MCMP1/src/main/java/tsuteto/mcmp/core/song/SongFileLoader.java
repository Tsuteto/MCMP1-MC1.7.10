package tsuteto.mcmp.core.song;

import com.google.common.collect.Lists;
import tsuteto.mcmp.core.audio.SoundSystemType;
import tsuteto.mcmp.core.util.McmpLog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;

public class SongFileLoader
{
    public static SongPool loadSongs(File songdir)
    {
        SongPool songPool = new SongPool();

        McmpLog.debug("Song dir: %s", songdir.getPath());
        if (!songdir.exists() && !songdir.mkdirs())
            return songPool;

        final List<String> availableExt = Lists.newArrayList();
        for (SoundSystemType type : SoundSystemType.typeList)
        {
            Collections.addAll(availableExt, type.getExtensions());
        }

        File[] songFiles = songdir.listFiles(new FilenameFilter()
        {
            @Override
            public boolean accept(File dir, String name)
            {
                if (!new File(dir, name).isFile())
                    return false;

                int dotpos = name.lastIndexOf(".");

                if (dotpos <= 0)
                    return false;

                String fileExt = name.substring(dotpos + 1);

                for (String ext : availableExt)
                {
                    if (ext.equalsIgnoreCase(fileExt))
                    {
                        return true;
                    }
                }
                return false;
            }
        });

        for (File file : songFiles)
        {
            String filename = file.getName();
            SongInfo info = new SongInfo();

            info.file = file;
            info.songName = filename.substring(0, filename.lastIndexOf("."));

            for (SoundSystemType type : SoundSystemType.typeList)
            {
                for (String ext : type.getExtensions())
                {
                    if (filename.toLowerCase().lastIndexOf("." + ext) + (ext.length() + 1) == filename.length())
                    {
                        info.playerType = type;
                        break;
                    }
                }
            }

            songPool.songMap.put(info.file.getName(), info);
            songPool.songList.add(info);
            McmpLog.info("Retrieved song file: " + filename);
        }

        return songPool;
    }
}
