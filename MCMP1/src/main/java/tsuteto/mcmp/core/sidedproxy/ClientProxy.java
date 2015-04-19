package tsuteto.mcmp.core.sidedproxy;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraftforge.common.MinecraftForge;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.eventhandler.CommonTickHandler;
import tsuteto.mcmp.core.eventhandler.GuiEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    public void registerComponents(Mcmp1Core mod)
    {
        FMLCommonHandler.instance().bus().register(new CommonTickHandler());
        MinecraftForge.EVENT_BUS.register(new GuiEvent());
    }

    @Override
    public void installSoundFiles()
    {
        //McmpSoundManager.getInstance().registerWavFiles(McmpMain.songDir);

//        Minecraft mc = FMLClientHandler.instance().getClient();
//        File soundDir = new File(McmpSoundManager.getInstance().getAssetsDir(), "sound/MCMP1");
//        if (!soundDir.exists() && !soundDir.mkdirs())
//        {
//            ModLog.log(Level.WARNING, "Failed to make 'MCMP1' directory in " + soundDir.getAbsolutePath());
//            return;
//        }
//
//        ResourceInstaller installer = new ResourceInstaller(soundDir);
//        installer.addResource("/assets/mcmp1/sound/play.ogg", "play.ogg");
//        installer.addResource("/assets/mcmp1/sound/stop.ogg", "stop.ogg");
//        installer.install();
//
//        if (installer.hasInstalled())
//        {
//            ModLog.log(Level.INFO, "Done installing sound files");
//        }
    }
}
