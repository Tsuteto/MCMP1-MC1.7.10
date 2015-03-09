package tsuteto.mcmp.changer;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import tsuteto.mcmp.core.Mcmp1Core;

public class ItemChanger extends Item
{

    public ItemChanger()
    {
        super();
        setMaxStackSize(1);
    }

    /**
     * Action when right-clicking
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemstack, World par2World, EntityPlayer entityplayer)
    {
        if (itemstack != null)
        {

            if (!itemstack.hasTagCompound())
            {
                itemstack.setTagCompound(new NBTTagCompound());
            }

            entityplayer.openGui(Mcmp1Core.instance, 1, entityplayer.worldObj, 0, 0, 0);
        }
        return itemstack;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
    {
        InventoryChanger changerInv = new InventoryChanger(itemstack);

        if (changerInv.getChangerName().length() != 0)
        {
            list.add(changerInv.getChangerName());
        }

        int numSongs = changerInv.getNumSongsInside();
        if (numSongs > 0)
        {
            list.add(StatCollector.translateToLocalFormatted("mcmp1.numSongs", numSongs,
                    StatCollector.translateToLocal((numSongs > 1) ? "mcmp1.songs" : "mcmp1.song")));
        }
    }
}
