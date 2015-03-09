package tsuteto.mcmp.core.song;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tsuteto.mcmp.core.audio.EnumSoundSystemType;
import tsuteto.mcmp.core.util.McmpLog;

public class SongPool
{
    public Map<String, SongInfo> songMap = new HashMap<String, SongInfo>();
    public List<SongInfo> songList = new ArrayList<SongInfo>();

    public SongPool(File songdir)
    {
        McmpLog.debug("Song dir: %s", songdir.getPath());
        if (!songdir.exists() && !songdir.mkdirs())
            return;

        File[] songFiles = songdir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name)
            {
                if (!new File(dir, name).isFile())
                    return false;

                int dotpos = name.lastIndexOf(".");

                if (dotpos <= 0)
                    return false;

                String ext = name.substring(dotpos + 1);

                return "mp4".equalsIgnoreCase(ext)
                        || "m4a".equalsIgnoreCase(ext)
                        || "aac".equalsIgnoreCase(ext)
                        || "mp3".equalsIgnoreCase(ext)
                        || "wav".equalsIgnoreCase(ext);
                        //|| "ogg".equalsIgnoreCase(ext)
                        //|| "mus".equalsIgnoreCase(ext);
            }
        });

        for (File file : songFiles)
        {
            String filename = file.getName();
            SongInfo info = new SongInfo();

            info.file = file;
            info.songName = filename.substring(0, filename.lastIndexOf("."));

            if (filename.toLowerCase().lastIndexOf(".mp3") + 4 == filename.length())
            {
                info.playerType = EnumSoundSystemType.MP3;
            }
            else if (filename.toLowerCase().lastIndexOf(".mp4") + 4 == filename.length()
                    || filename.toLowerCase().lastIndexOf(".m4a") + 4 == filename.length()
                    || filename.toLowerCase().lastIndexOf(".aac") + 4 == filename.length())
            {
                info.playerType = EnumSoundSystemType.AAC;
            }
            else if (filename.toLowerCase().lastIndexOf(".wav") + 4 == filename.length())
            {
                info.playerType = EnumSoundSystemType.WAV;
            }
            songMap.put(info.file.getName(), info);
            songList.add(info);
            McmpLog.info("Retrieved song file: " + filename);
        }
    }
}
