package tsuteto.mcmp.core.util;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockUtils
{
    /**
     * Determines block metadata for orientation from 4 directions, called from onBlockPlacedBy()
     */
    public static void orientDirection4(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving)
    {
        int i = MathHelper.floor_double(((par5EntityLiving.rotationYaw * 4F) / 360F) + 0.5D) & 3;

        if (i == 0)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 0, 2);
        }

        if (i == 1)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
        }

        if (i == 2)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 1, 2);
        }

        if (i == 3)
        {
            par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
        }

    }

    /**
     * Determines block metadata for orientation from surroundings, called from onBlockAdded()
     */
    public static void setDefaultDirection4(World par1World, int par2, int par3, int par4)
    {
        if (!par1World.isRemote)
        {
            Block block = par1World.getBlock(par2, par3, par4 - 1);
            Block block1 = par1World.getBlock(par2, par3, par4 + 1);
            Block block2 = par1World.getBlock(par2 - 1, par3, par4);
            Block block3 = par1World.getBlock(par2 + 1, par3, par4);
            byte b0 = 1;

            if (block.func_149730_j() && !block1.func_149730_j())
            {
                b0 = 1;
            }

            if (block1.func_149730_j() && !block.func_149730_j())
            {
                b0 = 0;
            }

            if (block2.func_149730_j() && !block3.func_149730_j())
            {
                b0 = 3;
            }

            if (block3.func_149730_j() && !block2.func_149730_j())
            {
                b0 = 2;
            }

            par1World.setBlockMetadataWithNotify(par2, par3, par4, b0, 2);
        }
    }

}
