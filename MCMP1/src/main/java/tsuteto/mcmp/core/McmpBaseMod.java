package tsuteto.mcmp.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import tsuteto.mcmp.core.sidedproxy.CommonProxy;
import cpw.mods.fml.common.SidedProxy;

abstract public class McmpBaseMod
{
    @SidedProxy(clientSide = "tsuteto.mcmp.core.sidedproxy.ClientProxy", serverSide = "tsuteto.mcmp.core.sidedproxy.CommonProxy")
    public static CommonProxy sidedProxy;

    public boolean onTickInGUI(Minecraft minecraft, GuiScreen guiscreen)
    {
        if (guiscreen != null && guiscreen instanceof GuiMainMenu)
        {
            resetPlayer(minecraft);
        }
        return true;
    }

    protected void resetPlayer(Minecraft minecraft)
    {
    }
}
