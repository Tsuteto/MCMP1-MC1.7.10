package tsuteto.mcmp.recorder;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.util.BlockUtils;

import java.util.Random;

public class BlockRecorder extends BlockContainer
{
    private final boolean isActive;
    private Random machineRand;
    private IIcon iconSide;

    /**
     * This flag is used to prevent the furnace inventory to be dropped upon
     * block removal, is used internally when the furnace block changes from
     * idle to active and vice-versa.
     */
    private static boolean keepInventory = false;

    public BlockRecorder(boolean isActive)
    {
        super(Material.iron);
        this.isActive = isActive;
        machineRand = new Random();
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2)
    {
        return new TileEntityRecorder();
    }

    /**
     * Returns the ID of the items to drop on destruction.
     */
    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return Item.getItemFromBlock(Mcmp1Core.blockRecorderIdle);
    }

    /**
     * Returns the block texture based on the side being looked at. Args: side
     */
    @Override
    public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        int meta = iblockaccess.getBlockMetadata(x, y, z);

        if (side == 1) // top
        {
            return this.iconSide;
        }
        else if (side == 0) // bottom
        {
            return this.iconSide;
        }
        else
        {
            return side != (meta & 3) + 2 ? this.iconSide // side & back
                    : this.blockIcon; // front
        }
    }

    /**
     * Returns the block texture based on the side being looked at. Args: side
     */
    @Override
    public IIcon getIcon(int par1, int par2)
    {
        if (par1 == 3)
        {
            return this.blockIcon;
        }
        else
        {
            return iconSide;
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f, float g, float t)
    {
        TileEntity tile = world.getTileEntity(x, y, z);

        if (tile != null)
        {
            player.openGui(Mcmp1Core.instance, 0, world, x, y, z);
        }

        return true;
    }

    @Override
    public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLiving, ItemStack par6ItemStack)
    {
        BlockUtils.orientDirection4(par1World, par2, par3, par4, par5EntityLiving);

        if (par6ItemStack.hasDisplayName())
        {
            ((TileEntityRecorder)par1World.getTileEntity(par2, par3, par4)).setCustomName(par6ItemStack.getDisplayName());
        }
    }

    @Override
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        BlockUtils.setDefaultDirection4(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
    }

    /**
     * Update which block ID the furnace is using depending on whether or not it is burning
     */
    public static void updateFurnaceBlockState(boolean isActive, World par1World, int par2, int par3, int par4)
    {
        int var5 = par1World.getBlockMetadata(par2, par3, par4);
        TileEntity var6 = par1World.getTileEntity(par2, par3, par4);
        keepInventory = true;

        if (isActive)
        {
            par1World.setBlock(par2, par3, par4, Mcmp1Core.blockRecorderActive);
        }
        else
        {
            par1World.setBlock(par2, par3, par4, Mcmp1Core.blockRecorderIdle);
        }

        keepInventory = false;
        par1World.setBlockMetadataWithNotify(par2, par3, par4, var5, 2);

        if (var6 != null)
        {
            var6.validate();
            par1World.setTileEntity(par2, par3, par4, var6);
        }
    }

    /**
     * Called whenever the block is removed.
     */
    @Override
    public void breakBlock(World world, int par2, int par3, int par4, Block par5, int par6)
    {
        if (!keepInventory)
        {
            TileEntityRecorder tileentity = (TileEntityRecorder) world.getTileEntity(par2, par3, par4);

            if (tileentity != null)
            {
                label0:

                    for (int i = 0; i < tileentity.getSizeInventory(); i++)
                    {
                        ItemStack itemstack = tileentity.getStackInSlot(i);

                        if (itemstack == null)
                        {
                            continue;
                        }

                        float f = machineRand.nextFloat() * 0.8F + 0.1F;
                        float f1 = machineRand.nextFloat() * 0.8F + 0.1F;
                        float f2 = machineRand.nextFloat() * 0.8F + 0.1F;

                        do
                        {
                            if (itemstack.stackSize <= 0)
                            {
                                continue label0;
                            }

                            int j = machineRand.nextInt(21) + 10;

                            if (j > itemstack.stackSize)
                            {
                                j = itemstack.stackSize;
                            }

                            itemstack.stackSize -= j;
                            EntityItem entityitem = new EntityItem(world, par2 + f, par3 + f1, par4
                                    + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (float) machineRand.nextGaussian() * f3;
                            entityitem.motionY = (float) machineRand.nextGaussian() * f3 + 0.2F;
                            entityitem.motionZ = (float) machineRand.nextGaussian() * f3;
                            world.spawnEntityInWorld(entityitem);
                        } while (true);
                    }
            }
        }

        super.breakBlock(world, par2, par3, par4, par5, par6);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        this.blockIcon = par1IconRegister.registerIcon(this.isActive ? "mcmp1:dubbingMachine_front_active" : "mcmp1:dubbingMachine_front_idle");
        this.iconSide = par1IconRegister.registerIcon("mcmp1:dubbingMachine_side");
    }
}
