package tsuteto.mcmp.core.util;

import org.apache.logging.log4j.Level;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourceInstaller
{
    private File destDir;
    private List<Entry> srcList = new ArrayList<Entry>();
    private Class<?> srcClass;
    private boolean hasInstalled = false;
    
    public ResourceInstaller(File destDir)
    {
        this.destDir = destDir;
        this.srcClass = this.getClass();
    }

    public void addResource(String path, String filename)
    {
        URL url = srcClass.getResource(path);
        Entry entry = new Entry(url, filename);
        this.srcList.add(entry);
    }

    public void install()
    {
        for (Entry resource : srcList)
        {
            File destFile = new File(this.destDir, resource.filename);
            
            FileCopy fc = new FileCopy(resource.url, destFile);
            if (fc.verify()) continue;
            McmpLog.log(Level.INFO, "Installing: %s", destFile.getPath());
            hasInstalled = true;

            fc.copy();
            if (!fc.verify())
            {
            	McmpLog.log(Level.INFO, "Failed to install: %s", destFile.getPath());
                destFile.delete();
            }
        }
    }

    public void setResourceClass(Class<?> clazz)
    {
        this.srcClass = clazz;
    }
    
    public boolean hasInstalled()
    {
        return this.hasInstalled;
    }
    
    public class Entry
    {
        String filename;
        URL url;
        
        public Entry(URL url, String filename)
        {
            this.url = url;
            this.filename = filename;
        }
    }
}
