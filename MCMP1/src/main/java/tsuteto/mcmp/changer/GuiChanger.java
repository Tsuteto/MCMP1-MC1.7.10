package tsuteto.mcmp.changer;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiChanger extends GuiContainer
{
    private static final ResourceLocation texture = new ResourceLocation("mcmp1", "gui_cassettechanger.png");

    private InventoryChanger changerInventory;
    private int inventoryRows;

    private GuiTextField textBoxChangerName;

    public GuiChanger(InventoryPlayer par1InventoryPlayer, InventoryChanger inventory)
    {
        super(new ContainerChanger(par1InventoryPlayer, inventory));
        changerInventory = inventory;
        inventoryRows = inventory.getSizeInventory() / 9;

        // Gui size
        short var3 = 239;
        int var4 = var3 - 108;
        ySize = var4 + inventoryRows * 18;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        Keyboard.enableRepeatEvents(true);
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        textBoxChangerName = new GuiTextField(fontRendererObj, 0, 0, 100, 12);
        textBoxChangerName.setText(changerInventory.getChangerName());
    }

    @Override
    public void updateScreen()
    {
        textBoxChangerName.updateCursorCounter();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everythin in front of the
     * items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRendererObj.drawString(StatCollector.translateToLocal("container.mcmp1.Changer"), 8, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, (ySize - 96) + 2, 0x404040);
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
        int var5 = (this.width - this.xSize) / 2;
        int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.drawTexturedModalRect(var5, var6 + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 112);

        String lblChangerName = StatCollector.translateToLocal("mcmp1.changerName.label");
        int width = fontRendererObj.getStringWidth(lblChangerName);
        fontRendererObj.drawString(lblChangerName, var5 + 65 - width, var6 + 76, 0x404040);
        ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, this.textBoxChangerName, var5 + 68, "b", "xPosition");
        ObfuscationReflectionHelper.setPrivateValue(GuiTextField.class, this.textBoxChangerName, var6 + 74, "c", "yPosition");
        this.textBoxChangerName.drawTextBox();
    }

    @Override
    protected void keyTyped(char par1, int par2)
    {
        if (this.textBoxChangerName.isFocused())
        {
            this.textBoxChangerName.textboxKeyTyped(par1, par2);
            changerInventory.setNewChangerName(this.textBoxChangerName.getText());
        }
        else
        {
            super.keyTyped(par1, par2);
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3)
    {
        super.mouseClicked(par1, par2, par3);
        this.textBoxChangerName.mouseClicked(par1, par2, par3);
    }
}
