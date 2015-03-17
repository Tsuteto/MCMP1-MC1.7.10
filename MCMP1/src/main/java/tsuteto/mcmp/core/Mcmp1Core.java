package tsuteto.mcmp.core;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.EnumHelper;
import org.apache.logging.log4j.Level;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.changer.PacketChangerRename;
import tsuteto.mcmp.changer.PacketChangerState;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;
import tsuteto.mcmp.core.mcmpplayer.PacketMcmpPlayerCtl;
import tsuteto.mcmp.core.network.PacketManager;
import tsuteto.mcmp.core.registry.BlockRegister;
import tsuteto.mcmp.core.registry.ItemRegister;
import tsuteto.mcmp.core.util.McmpLog;
import tsuteto.mcmp.core.util.UpdateNotification;
import tsuteto.mcmp.mcmp1.ItemMCMP1;
import tsuteto.mcmp.recorder.BlockRecorder;
import tsuteto.mcmp.recorder.PacketRecorderCtl;
import tsuteto.mcmp.recorder.TileEntityRecorder;

@Mod(
        modid = Mcmp1Core.modId,
        name = "MCMP-1",
        version = "1.3.2-MC1.7.10",
        acceptedMinecraftVersions = "[1.7.10,1.8)"
)
public class Mcmp1Core extends McmpBaseMod
{
    public static final String modId = "mcmp1";
    public static final String resourceDomain = "mcmp1:";

    public static String songDir = "MCMP-1 Songs";
    public static boolean useSmallFont = true;
    public static boolean updateCheck = true;

    @Mod.Instance(modId)
    public static Mcmp1Core instance;

    @Mod.Metadata(modId)
    public static ModMetadata metadata;

    public static Item itemMCMP1;
    public static Item itemCassetteNormal;
    public static Item itemChanger;
    public static Block blockRecorderIdle;
    public static Block blockRecorderActive;

    // Used in core mod
    public static final MusicTicker.MusicType MUSIC_TYPE_MCMP1;

    public static UpdateNotification update = null;
    public McmpSoundManager sndManager;

    static
    {
        MUSIC_TYPE_MCMP1 = EnumHelper.addEnum(MusicTicker.MusicType.class, "MCMP1",
                new Class<?>[]{ResourceLocation.class, int.class, int.class},
                new Object[]{new ResourceLocation("mcmp1", "dummy"), Integer.MAX_VALUE, Integer.MAX_VALUE});

        McmpLog.modId = "MCMP-1";
    }

    public Mcmp1Core()
    {
        sndManager = McmpSoundManager.getInstance();
    }

    @Mod.EventHandler
    public void earlyLoad(FMLPreInitializationEvent event)
    {
        ModInfo.load(metadata);

        Configuration cfg = new Configuration(event.getSuggestedConfigurationFile());
        try
        {
            cfg.load();

            songDir = cfg.get(Configuration.CATEGORY_GENERAL, "songDir", songDir).getString();
            updateCheck = cfg.get(Configuration.CATEGORY_GENERAL, "updateCheck", updateCheck).getBoolean(true);
            useSmallFont = cfg.get("gui", "useSmallFont", useSmallFont).getBoolean(false);
        }
        catch (Exception e)
        {
            McmpLog.log(Level.WARN, e, "Failed to read cfg file");
        }
        finally
        {
            cfg.save();
        }

        // Update check!
        if (updateCheck)
        {
            update = new UpdateNotification();
            update.checkUpdate();
        }

        /*
         * Define items, blocks
         */
        ItemRegister.setResourceDomain(resourceDomain);
        BlockRegister.setResourceDomain(resourceDomain);

        itemMCMP1 = ItemRegister.of("mcmp1", new ItemMCMP1())
                .register();

        itemCassetteNormal = ItemRegister.of("cassetteNormal", new ItemCassetteTape())
                .register()
                .setCreativeTab(CreativeTabs.tabMisc);

        itemChanger = ItemRegister.of("cassetteChanger", new ItemChanger())
                .register()
                .setCreativeTab(CreativeTabs.tabTools);

        blockRecorderIdle = BlockRegister.of("McmpRecorder", new BlockRecorder(false))
                .withResource("dubbingMachine")
                .register()
                .setHardness(3.5F)
                .setStepSound(Block.soundTypeMetal)
                .setCreativeTab(CreativeTabs.tabDecorations);

        blockRecorderActive = BlockRegister.of("McmpRecorderActive", new BlockRecorder(true))
                .withResource("dubbingMachine")
                .register()
                .setHardness(3.5F)
                .setStepSound(Block.soundTypeMetal)
                .setLightLevel(0.75F);

        /*
         * Register tile entities
         */
        GameRegistry.registerTileEntity(TileEntityRecorder.class, "McmpRecorder");

    }

    @Mod.EventHandler
    public void load(FMLInitializationEvent event)
    {
        /*
         * Register sided components
         */
        sidedProxy.registerComponents(this);

        /*
         * Register GUI
         */
        NetworkRegistry.INSTANCE.registerGuiHandler(this, new McmpGuiHandler());

        /*
         * Prepare network packet handlers
         */
        PacketManager.init(modId)
                .registerPacket(PacketRecorderCtl.class)
                .registerPacket(PacketChangerState.class)
                .registerPacket(PacketChangerRename.class)
                .registerPacket(PacketMcmpPlayerCtl.class);

        /*
         * Recipes
         */

        // MCMP-1
        GameRegistry.addRecipe(new ItemStack(itemMCMP1),
                "XX",
                "YY",
                Character.valueOf('X'), Items.iron_ingot,
                Character.valueOf('Y'), Items.repeater);

        // Cassette Tape
        GameRegistry.addRecipe(new ItemStack(itemCassetteNormal, 4),
                "XX",
                "YY",
                Character.valueOf('X'), Items.string,
                Character.valueOf('Y'), Blocks.planks);

        // Recorder
        GameRegistry.addRecipe(new ItemStack(blockRecorderIdle),
                " X ",
                "X*X",
                "---",
                Character.valueOf('X'), Items.iron_ingot,
                Character.valueOf('*'), Items.diamond,
                Character.valueOf('-'), Items.repeater);

        // Cassette Changer
        GameRegistry.addRecipe(new ItemStack(itemChanger),
                "XXX",
                "Y Y",
                "YYY",
                Character.valueOf('X'), Items.iron_ingot,
                Character.valueOf('Y'), Blocks.planks);

        // Color variation of cassette tapes
        for (int i = 0; i < 16; i++)
        {
            GameRegistry.addShapelessRecipe(new ItemStack(itemCassetteNormal, 1, ItemCassetteTape.getDamageFromDyeType(i)),
                    itemCassetteNormal,
                    new ItemStack(Items.dye, 1, i));

            GameRegistry.addRecipe(new ItemStack(itemCassetteNormal, 4, ItemCassetteTape.getDamageFromDyeType(i)),
                    " XX",
                    "DYY",
                    Character.valueOf('X'), Items.string,
                    Character.valueOf('Y'), Blocks.planks,
                    Character.valueOf('D'), new ItemStack(Items.dye, 1, i)
            );
        }

        // For making cassettes blank
        GameRegistry.addShapelessRecipe(new ItemStack(itemCassetteNormal),
                new ItemStack(itemCassetteNormal, 1, 0x7FFF),
                new ItemStack(Items.dye, 1, 15));
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event)
    {
        // Notify if update is available
        if (update != null && event.getSide() == Side.SERVER)
        {
            update.notifyUpdate(event.getServer(), event.getSide());
        }
    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        this.sndManager.stop();
    }

    @Override
    public boolean onTickInGUI(Minecraft minecraft, GuiScreen guiscreen)
    {
        if (guiscreen != null)
        {
            if (guiscreen instanceof GuiMainMenu && sndManager.playing())
            {
            }
            else
            {
                for (ItemMcmpPlayer mcmpPlayer : McmpPlayerManager.getPlayerList())
                {
                    mcmpPlayer.inInventory = true;
                }
            }
        }
        return true;
    }


}
