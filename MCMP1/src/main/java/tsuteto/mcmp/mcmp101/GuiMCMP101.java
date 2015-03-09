package tsuteto.mcmp.mcmp101;

import java.awt.Color;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import tsuteto.mcmp.changer.InventoryChanger;
import tsuteto.mcmp.core.SEs;
import tsuteto.mcmp.core.mcmpplayer.ItemMcmpPlayer;
import tsuteto.mcmp.core.songselector.SongSelectorNext;
import tsuteto.mcmp.core.songselector.SongSelectorRandom;
import tsuteto.mcmp.core.songselector.SongSelectorSpecific;
import tsuteto.mcmp.core.util.BlankContainer;
import tsuteto.mcmp.core.util.GuiUtilities;
import tsuteto.mcmp.core.util.McmpLog;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiMCMP101 extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("mcmp101", "gui_mcmp101.png");
    private static final int displayWidth = 113;

    private ItemMCMP101 controller;
    private ItemStack memory;
    private InventoryPlayer playerInventory;
    private EntityPlayer entityPlayer;

    private int ticksActionIndicator = 0;
    private String display = null;

    public GuiMCMP101(ItemStack memory, EntityPlayer entityPlayer)
    {
        super(new BlankContainer(entityPlayer.inventory, new InventoryChanger(memory)));
        this.controller = (ItemMCMP101)MCMP101.itemMCMP101;
        this.memory = memory;
        this.playerInventory = entityPlayer.inventory;
        this.entityPlayer = entityPlayer;
        // Gui size
        xSize = 130;
        ySize = 69;
    }

    /**
     * Called when the mouse is clicked.
     */
    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        int ox = (width - xSize) / 2;
        int oy = (height - ySize) / 2;

        // Prev
        {
            int x = par1 - (ox + 13);
            int y = par2 - (oy + 32);
            if (x >= 0 && y >= 0 && x < 16 && y < 16)
            {
                mc.getSoundHandler().playSound(SEs.click());

                ItemStack song = controller.goBack(memory, playerInventory);

                if (controller.isPlaying)
                {
                    controller.play(memory, entityPlayer, song);

                    display = "PREV TRACK";
                    ticksActionIndicator = 30;
                }
            }
        }

        // Play
        {
            int x = par1 - (ox + 29);
            int y = par2 - (oy + 32);
            if (x >= 0 && y >= 0 && x < 30 && y < 16)
            {
                mc.getSoundHandler().playSound(SEs.click());

                if (!controller.isPlaying)
                {

                    ItemStack song;
                    song = new SongSelectorSpecific(controller).selectSongToPlay(memory, playerInventory);
                    if (song == null)
                    {
                        if (controller.isRandomPlaying)
                        {
                            song = new SongSelectorRandom(controller).selectSongToPlay(memory, playerInventory);
                        }
                        else
                        {
                        	song = new SongSelectorNext(controller).selectSongToPlay(memory, playerInventory);
                        }
                    }
                    if (song != null)
                    {
                        controller.play(memory, entityPlayer, song);
                        McmpLog.debug(Thread.currentThread().getName());
                        display = "PLAY";
                    }
                    else
                    {
                        display = "NO SONG";
                    }
                }
                else
                {
                    display = "STOP";
                    controller.stop(memory, entityPlayer);
                }
                controller.setNoInterval();
                ticksActionIndicator = 30;
            }
        }

        // Next
        {
            int x = par1 - (ox + 59);
            int y = par2 - (oy + 32);
            if (x >= 0 && y >= 0 && x < 16 && y < 16)
            {
                mc.getSoundHandler().playSound(SEs.click());

                ItemStack song = controller.goNext(memory, playerInventory);

                if (controller.isPlaying)
                {
                    controller.play(memory, entityPlayer, song);

                    display = "NEXT TRACK";
                    ticksActionIndicator = 30;
                }
            }
        }

        // Random
        {
            int x = par1 - (ox + 85);
            int y = par2 - (oy + 32);
            if (x >= 0 && y >= 0 && x < 16 && y < 16)
            {
                mc.getSoundHandler().playSound(SEs.click());
                controller.isRandomPlaying ^= true;
                controller.setSongSelector();
            }
        }
        // Repeat
        {
            int x = par1 - (ox + 101);
            int y = par2 - (oy + 32);
            if (x >= 0 && y >= 0 && x < 16 && y < 16)
            {
                mc.getSoundHandler().playSound(SEs.click());
                controller.isRepeatPlaying ^= true;
                controller.setSongSelector();
            }
        }
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        this.mc.getTextureManager().bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int ox = (width - xSize) / 2;
        int oy = (height - ySize) / 2;
        drawTexturedModalRect(ox, oy, 0, 0, xSize, ySize);

        /*
         * Control Panel
         */
        if (controller.isPlaying)
        {
            drawTexturedModalRect(ox + 29, oy + 32, 0, 70, 30, 16);

            GL11.glColor4f(0.3F, 0.7F, 0.3F, 1.0F);
            drawTexturedModalRect(ox + 5, oy + 10, 130, 0, 6, 5);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        else
        {
            GL11.glColor4f(0.8F, 0.3F, 0.3F, 1.0F);
            drawTexturedModalRect(ox + 5, oy + 17, 136, 0, 5, 5);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        if (controller.isRandomPlaying)
        {
            drawTexturedModalRect(ox + 85, oy + 32, 30, 70, 16, 16);
        }

        if (controller.isRepeatPlaying)
        {
            drawTexturedModalRect(ox + 101, oy + 32, 46, 70, 16, 16);
        }

        /*
         * Display
         */

        // Welcome!
        if (!controller.hasStartedUp)
        {
            display = "Welcome to MCMP-101!";
            controller.hasStartedUp = true;
            ticksActionIndicator = 30;

            // Seek a song
            ItemStack song = controller.getSelectedSong(memory, playerInventory);
            if (song == null)
            {
                new SongSelectorNext(controller).selectSongToPlay(memory, playerInventory);
            }
        }

        // Idling
        if (ticksActionIndicator <= 0 && display == null)
        {
            String songName;
            if (controller.itemPlaying != null)
            {
                // Track name
                songName = controller.getPlayingSongName(entityPlayer);
            }
            else
            {
                ItemStack song = controller.getSelectedSong(memory, playerInventory);
                if (song != null)
                {
                    songName = ItemMcmpPlayer.getSongName(song, entityPlayer);
                }
                else
                {
                    songName = null;
                }
            }
            if (songName != null)
            {
                display = songName;
            }
            else
            {
                display = "No song selected";
            }
        }

        // Show display
        if (display != null
                && (ticksActionIndicator % 4 > 1 || ticksActionIndicator < 15))
        {
            if (MCMP101.useSmallFont)
            {
                GuiUtilities.showLongString(display, ox + 14, oy + 12, displayWidth, Color.cyan.getRGB(), fontRendererObj);
            }
            else
            {
                GuiUtilities.showLongString1Line(display, ox + 14, oy + 12, displayWidth, Color.cyan.getRGB(), fontRendererObj);
            }
        }
    }

    @Override
    public void updateScreen()
    {
        if (ticksActionIndicator > 0)
        {
            ticksActionIndicator--;
        }
        else
        {
            display = null;
        }
    }
}
