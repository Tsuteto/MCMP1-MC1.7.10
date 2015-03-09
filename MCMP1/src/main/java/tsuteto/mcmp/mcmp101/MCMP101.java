package tsuteto.mcmp.mcmp101;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.logging.log4j.Level;
import tsuteto.mcmp.core.McmpBaseMod;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import tsuteto.mcmp.core.registry.ItemRegister;

@Mod(modid = MCMP101.modId, name = "MCMP-101", version = "1.0.0-MC1.7.10")
public class MCMP101 extends McmpBaseMod
{
    public static final String modId = "mcmp101";
    public static boolean useSmallFont = false;

    @Mod.Instance(MCMP101.modId)
    public static MCMP101 instance;

    public static Item itemMCMP101;

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event)
    {
        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        cfg.load();

        Property propUseSmallFont = cfg.get("display", "useSmallFont", useSmallFont);
        useSmallFont = propUseSmallFont.getBoolean(false);

        cfg.save();

        ItemRegister.setResourceDomain("mcmp101:");

        itemMCMP101 = ItemRegister.of("mcmp101", new ItemMCMP101())
                .register();

        GameRegistry.addRecipe(new ItemStack(itemMCMP101),
                "XX",
                "YY",
                Character.valueOf('X'), Blocks.obsidian,
                Character.valueOf('Y'), Items.repeater);

        NetworkRegistry.INSTANCE.registerGuiHandler(this, new Mcmp101GuiHandler());
    }

    @Override
    public void resetPlayer(Minecraft minecraft)
    {
        ((ItemMCMP101) itemMCMP101).hasStartedUp = false;
    }
}
