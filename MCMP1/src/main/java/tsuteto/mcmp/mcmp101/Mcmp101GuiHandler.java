package tsuteto.mcmp.mcmp101;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class Mcmp101GuiHandler implements IGuiHandler
{

    @Override
    public Object getServerGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
        return null;
    }

    @Override
    public Object getClientGuiElement(int guiId, EntityPlayer player, World world, int x, int y, int z)
    {
        ItemStack itemstack = player.getCurrentEquippedItem();
        if (itemstack.getItem() == MCMP101.itemMCMP101)
        {
            return new GuiMCMP101(itemstack, player);
        }
        return null;
    }
}
