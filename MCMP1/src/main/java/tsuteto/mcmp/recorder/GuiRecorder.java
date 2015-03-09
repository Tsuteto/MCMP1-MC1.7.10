package tsuteto.mcmp.recorder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import tsuteto.mcmp.cassettetape.ItemCassetteTape.Source;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.SEs;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.network.PacketDispatcher;
import tsuteto.mcmp.core.song.SongInfo;
import tsuteto.mcmp.core.util.GuiUtilities;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiRecorder extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("mcmp1", "gui_dubbingmachine.png");

    private TileEntityRecorder recorderInventory;
    private final McmpSoundManager sndMgr;

    private static final int DISP_SONGLIST_LEN = 6;
    private ArrayList<String> dispSonglist = new ArrayList();

    private boolean isSonglistScrollUpBtnEnabled = false;
    private boolean isSonglistScrollDownBtnEnabled = false;
    private boolean isSonglistScrollUpBtnPressed = false;
    private boolean isSonglistScrollDownBtnPressed = false;
    private int timeWaitSonglistScrolling = -1;
    private int mouseDWheel = 0;

    public GuiRecorder(InventoryPlayer par1InventoryPlayer, TileEntityRecorder tileentity)
    {
        super(new ContainerRecorder(par1InventoryPlayer, tileentity));
        recorderInventory = tileentity;
        sndMgr = McmpSoundManager.getInstance();
        // Gui size
        xSize = 206;
        ySize = 186;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        // Locate a selected row in the song list
        if (recorderInventory.getHddFileNameSelected() != null)
        {
            List<SongInfo> songlist = sndMgr.getSongManager().getSongList();
            for (int l = 0; l < songlist.size(); l++)
            {
                if (recorderInventory.getHddFileNameSelected().equals(songlist.get(l).file.getName()))
                {
                    recorderInventory.setSonglistRowSelected(l);
                    break;
                }
            }
        }

        updateSonglist();
    }

    private void updateSonglist()
    {
        dispSonglist.clear();
        List<SongInfo> songlist = sndMgr.getSongManager().getSongList();
        for (int i = 0; i < DISP_SONGLIST_LEN && i + recorderInventory.posSonglist < songlist.size(); i++)
        {
            dispSonglist.add(songlist.get(i + recorderInventory.posSonglist).songName);
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        String s = StatCollector.translateToLocal(this.recorderInventory.getInventoryName());
        fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 5, 4210752);
//        fontRenderer.drawString(StatCollector.translateToLocal("container.mcmp1.Recorder"), 60, 5, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 23, (ySize - 96) + 2, 0x404040);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();

        if (isSonglistScrollUpBtnPressed && !isSonglistScrollUpBtnEnabled || isSonglistScrollDownBtnPressed
                && !isSonglistScrollDownBtnEnabled)
        {
            timeWaitSonglistScrolling = -1;
        }

        // Song list scrolling
        if (timeWaitSonglistScrolling > 0)
        {
            timeWaitSonglistScrolling--;
        }
        else if (timeWaitSonglistScrolling == 0)
        {
            if (isSonglistScrollUpBtnPressed)
            {
                recorderInventory.posSonglist -= 1;
            }
            else if (isSonglistScrollDownBtnPressed)
            {
                recorderInventory.posSonglist += 1;
            }

            updateSonglist();
            timeWaitSonglistScrolling = 2;
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        super.drawScreen(par1, par2, par3);
        mouseDWheel = 0;
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

        // Song list
        for (int l = 0; l < dispSonglist.size(); l++)
        {
            int x = par1 - (ox + 10);
            int y = par2 - (oy + 16 + 12 * l);

            if (x >= 0 && y >= 0 && x < 78 && y < 12)
            {
                if (recorderInventory.getSonglistRowSelected() != l + recorderInventory.posSonglist)
                {
                    recorderInventory.setSonglistRowSelected(l + recorderInventory.posSonglist);
                    recorderInventory.dubbingTime = 0;
                }
                else
                {
                    recorderInventory.setSonglistRowSelected(-1);
                }
                mc.getSoundHandler().playSound(SEs.click());
                PacketDispatcher.packet(recorderInventory.dispatchControlPacket()).sendToServer();
            }
        }

        // Song list scroll buttons
        // Up
        if (isSonglistScrollUpBtnEnabled)
        {
            int x = par1 - (ox + 89);
            int y = par2 - (oy + 15);
            if (x >= 0 && y >= 0 && x < 10 && y < 10)
            {
                recorderInventory.posSonglist -= 1;
                timeWaitSonglistScrolling = 10;
                isSonglistScrollUpBtnPressed = true;
                mc.getSoundHandler().playSound(SEs.click());
                updateSonglist();
            }
        }
        else
        {
            timeWaitSonglistScrolling = -1;
        }
        // Down
        if (isSonglistScrollDownBtnEnabled)
        {
            int x = par1 - (ox + 89);
            int y = par2 - (oy + 79);
            if (x >= 0 && y >= 0 && x < 10 && y < 10)
            {
                recorderInventory.posSonglist += 1;
                timeWaitSonglistScrolling = 10;
                isSonglistScrollDownBtnPressed = true;
                mc.getSoundHandler().playSound(SEs.click());
                updateSonglist();
            }
        }
        // Input source switch
        {
            int x = par1 - (ox + 116);
            int y = par2 - (oy + 46);
            if (x >= 0 && y >= 0 && x < 14 && y < 14)
            {
                if (recorderInventory.getInputSource() == Source.HDD)
                {
                    recorderInventory.setInputSource(Source.RECORDS);
                }
                else
                {
                    recorderInventory.setInputSource(Source.HDD);
                }
                recorderInventory.dubbingTime = 0;
                mc.getSoundHandler().playSound(SEs.click());

                PacketDispatcher.packet(recorderInventory.dispatchControlPacket()).sendToServer();
            }
        }
    }

    @Override
    protected void mouseMovedOrUp(int par1, int par2, int par3)
    {
        super.mouseMovedOrUp(par1, par2, par3);
        if (par3 == 0)
        {
            timeWaitSonglistScrolling = -1;
            isSonglistScrollUpBtnPressed = false;
            isSonglistScrollDownBtnPressed = false;
        }
    }

    @Override
    public void handleMouseInput()
    {
        mouseDWheel = Mouse.getEventDWheel();
        super.handleMouseInput();
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        this.mc.getTextureManager().bindTexture(texture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int ox = (width - xSize) / 2;
        int oy = (height - ySize) / 2;
        drawTexturedModalRect(ox, oy, 0, 0, xSize, ySize);

        // Tape dubbing progress
        int i1 = recorderInventory.getDubbingProgressScaled(24);
        drawTexturedModalRect(ox + 139, oy + 44, 232, 0, i1 + 1, 16);

        // Song list
        for (int i = 0; i < dispSonglist.size(); i++)
        {
            if (i == recorderInventory.getSonglistRowSelected() - recorderInventory.posSonglist)
            {
                drawTexturedModalRect(ox + 10, oy + 16 + 12 * i, 0, 244, 78, 12);
            }
            else
            {
                drawTexturedModalRect(ox + 10, oy + 16 + 12 * i, 0, 220, 78, 12);
            }
        }

        // Song list wheel scrolling
        if (mouseDWheel != 0)
        {
            int x = par2 - (ox + 10);
            int y = par3 - (oy + 16);
            if (x >= 0 && y >= 0 && x < 78 && y < 72)
            {
                if (mouseDWheel > 0 && recorderInventory.posSonglist != 0)
                {
                    recorderInventory.posSonglist -= Math.min(recorderInventory.posSonglist, mouseDWheel / 120);
                    updateSonglist();
                }

                int songs = sndMgr.getSongManager().countSong();
                if (mouseDWheel < 0 && recorderInventory.posSonglist + DISP_SONGLIST_LEN < songs)
                {
                    recorderInventory.posSonglist += Math.min(songs - DISP_SONGLIST_LEN - recorderInventory.posSonglist, -mouseDWheel / 120);
                    updateSonglist();
                }
            }
        }

        // Song list scroll buttons
        if (recorderInventory.posSonglist != 0)
        {
            isSonglistScrollUpBtnEnabled = true;
            if (isSonglistScrollUpBtnPressed)
            {
                drawTexturedModalRect(ox + 89, oy + 15, 236 + 10, 44, 10, 10);
            }
            else
            {
                drawTexturedModalRect(ox + 89, oy + 15, 236, 44, 10, 10);
            }
        }
        else
        {
            isSonglistScrollUpBtnEnabled = false;
            drawTexturedModalRect(ox + 89, oy + 15, 236 + 10, 44 + 20, 10, 10);
        }
        if (recorderInventory.posSonglist + DISP_SONGLIST_LEN < sndMgr.getSongManager().countSong())
        {
            isSonglistScrollDownBtnEnabled = true;
            if (isSonglistScrollDownBtnPressed)
            {
                drawTexturedModalRect(ox + 89, oy + 79, 236 + 10, 44 + 10, 10, 10);
            }
            else
            {
                drawTexturedModalRect(ox + 89, oy + 79, 236, 44 + 10, 10, 10);
            }
        }
        else
        {
            isSonglistScrollDownBtnEnabled = false;
            drawTexturedModalRect(ox + 89, oy + 79, 236 + 10, 44 + 30, 10, 10);
        }

        // Input source switch
        if (recorderInventory.getInputSource() == Source.HDD)
        {
            drawTexturedModalRect(ox + 116, oy + 45, 242, 16, 14, 14);
        }
        else
        {
            drawTexturedModalRect(ox + 116, oy + 45, 242, 16 + 14, 14, 14);
        }

        // Song list characters
        for (int i = 0; i < dispSonglist.size(); i++)
        {
            int color;
            if (i == recorderInventory.getSonglistRowSelected() - recorderInventory.posSonglist)
            {
                color = 0x24303D;
            }
            else
            {
                color = 0x4F493E;
            }
            if (Mcmp1Core.useSmallFont)
            {
                GuiUtilities.showLongString(dispSonglist.get(i), ox + 12, oy + 18 + 12 * i, 78 - 4, color, fontRendererObj);
            }
            else
            {
                GuiUtilities.showLongString1Line(dispSonglist.get(i), ox + 12, oy + 18 + 12 * i, 78 - 4, color, fontRendererObj);
            }
        }
    }
}
