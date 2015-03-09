package tsuteto.mcmp.core.util;

import cpw.mods.fml.common.FMLLog;
import org.apache.logging.log4j.Level;

/**
 * Logger
 *
 * @author Tsuteto
 *
 */
public class McmpLog
{
    public static String modId;
    public static boolean isDebug = Boolean.valueOf(System.getProperty("mcmp1.debug", "false"));

    public static void log(Level level, Throwable e, String format, Object... data)
    {
        FMLLog.log(modId, level, e, format, data);
    }

    public static void log(Level level, String format, Object... data)
    {
        FMLLog.log(modId, level, format, data);
    }

    public static void info(String format, Object... data)
    {
        FMLLog.log(modId, Level.INFO, format, data);
    }

    public static void warn(Throwable e, String format, Object... data)
    {
        FMLLog.log(modId, Level.WARN, e, format, data);
    }

    public static void warn(String format, Object... data)
    {
        FMLLog.log(modId, Level.WARN, format, data);
    }

    public static void debug(Object format, Object... data)
    {
        if (isDebug)
        {
            info("(DEBUG) " + format, data);
        }
    }
}
