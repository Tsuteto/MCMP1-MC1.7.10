package tsuteto.mcmp.deck;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tsuteto.mcmp.changer.ItemChanger;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlacedPlayerController;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlayerControllerBase;
import tsuteto.mcmp.core.media.IMcmpMedia;
import tsuteto.mcmp.core.util.BlockUtils;

import java.util.Random;

public class BlockDeck extends BlockContainer
{
    private final Random rand = new Random();
    boolean isActive;
    @SideOnly(Side.CLIENT)
    private IIcon iconFront;
    @SideOnly(Side.CLIENT)
    private IIcon iconTopH;
    @SideOnly(Side.CLIENT)
    private IIcon iconTopV;

    private static boolean keepInventory = false;

    public BlockDeck(boolean isActive)
    {
        super(Material.iron);
        this.isActive = isActive;
    }

    public int tickRate(World p_149738_1_)
    {
        return 4;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess iblockaccess, int x, int y, int z, int side)
    {
        int dir = iblockaccess.getBlockMetadata(x, y, z) & 3;

        if (side == 1) // top
        {
            return (dir >> 1) == 0 ? this.iconTopH : this.iconTopV;
        }
        else if (side == 0) // bottom
        {
            return this.blockIcon;
        }
        else
        {
            return side != dir + 2 ? this.blockIcon // side & back
                    : this.iconFront; // front
        }
    }

    @Override
    public IIcon getIcon(int par1, int par2)
    {
        return par1 == 1 ? this.iconTopH : par1 == 3 ? this.iconFront : this.blockIcon;
    }


    @Override
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return Item.getItemFromBlock(Mcmp1Core.blockDeckIdle);
    }

    public boolean isPlayableItem(ItemStack itemStack)
    {
        return itemStack != null
                && (itemStack.getItem() instanceof IMcmpMedia || itemStack.getItem() instanceof ItemChanger);
    }

    @Override
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        TileEntityDeck te = (TileEntityDeck)p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_);
        ItemStack itemToInsert = p_149727_5_.getHeldItem();
        ItemStack itemInserted = te.getMedia();
        if (itemInserted != null)
        {
            // Eject and stop
            te.setMedia(null);
            te.stop();
            if (!p_149727_1_.isRemote)
            {
                p_149727_1_.spawnEntityInWorld(new EntityItem(p_149727_1_, (double) p_149727_2_ + 0.5D, (double) p_149727_3_ + 1.5D, (double) p_149727_4_ + 0.5D, itemInserted));
            }
            return true;
        }
        else if (this.isPlayableItem(itemToInsert))
        {
            // Insert and play
            te.setMedia(itemToInsert);
            te.play();
            p_149727_5_.inventory.setInventorySlotContents(p_149727_5_.inventory.currentItem, null);
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_)
    {
        boolean flag = p_149695_1_.isBlockIndirectlyGettingPowered(p_149695_2_, p_149695_3_, p_149695_4_) || p_149695_1_.isBlockIndirectlyGettingPowered(p_149695_2_, p_149695_3_ + 1, p_149695_4_);
        int l = p_149695_1_.getBlockMetadata(p_149695_2_, p_149695_3_, p_149695_4_);
        boolean flag1 = (l & 8) != 0;

        if (flag && !flag1)
        {
            p_149695_1_.scheduleBlockUpdate(p_149695_2_, p_149695_3_, p_149695_4_, this, this.tickRate(p_149695_1_));
            p_149695_1_.setBlockMetadataWithNotify(p_149695_2_, p_149695_3_, p_149695_4_, l | 8, 4);
        }
        else if (!flag && flag1)
        {
            p_149695_1_.setBlockMetadataWithNotify(p_149695_2_, p_149695_3_, p_149695_4_, l & -9, 4);
        }
    }

    public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_)
    {
        TileEntityDeck te = (TileEntityDeck)p_149674_1_.getTileEntity(p_149674_2_, p_149674_3_, p_149674_4_);
        if (te.controller.isPlayerPaused)
        {
            te.controller.createPlayerCtlPacket(McmpPlayerControllerBase.PKT_TYPE_RESUME).sendPacketToAllPlayers();
        }
        else
        {
            te.controller.createPlayerCtlPacket(McmpPlayerControllerBase.PKT_TYPE_PAUSE).sendPacketToAllPlayers();
        }
    }

    public static void updateBlockState(boolean p_149931_0_, World p_149931_1_, int p_149931_2_, int p_149931_3_, int p_149931_4_)
    {
        int l = p_149931_1_.getBlockMetadata(p_149931_2_, p_149931_3_, p_149931_4_);
        TileEntity tileentity = p_149931_1_.getTileEntity(p_149931_2_, p_149931_3_, p_149931_4_);
        keepInventory = true;

        if (p_149931_0_)
        {
            p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, Mcmp1Core.blockDeckActive);
        }
        else
        {
            p_149931_1_.setBlock(p_149931_2_, p_149931_3_, p_149931_4_, Mcmp1Core.blockDeckIdle);
        }

        keepInventory = false;
        p_149931_1_.setBlockMetadataWithNotify(p_149931_2_, p_149931_3_, p_149931_4_, l, 2);

        if (tileentity != null)
        {
            tileentity.validate();
            p_149931_1_.setTileEntity(p_149931_2_, p_149931_3_, p_149931_4_, tileentity);
        }
    }

    @Override
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
        BlockUtils.setDefaultDirection4(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
    }

    @Override
    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        BlockUtils.orientDirection4(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_);

        TileEntityDeck tileentity = (TileEntityDeck)p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_);
        tileentity.controller = new McmpPlacedPlayerController(p_149689_2_, p_149689_3_, p_149689_4_);
        if (p_149689_6_.hasDisplayName())
        {
            tileentity.setCustomName(p_149689_6_.getDisplayName());
        }
    }

    @Override
    public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6)
    {
        if (!keepInventory)
        {
            TileEntityDeck var7 = (TileEntityDeck)par1World.getTileEntity(par2, par3, par4);

            if (var7 != null)
            {
                var7.controller.createPlayerCtlPacket(McmpPlayerControllerBase.PKT_TYPE_STOP).sendPacketToAllPlayers();

                for (int var8 = 0; var8 < var7.getSizeInventory(); ++var8)
                {
                    ItemStack var9 = var7.getStackInSlot(var8);

                    if (var9 != null)
                    {
                        float var10 = this.rand.nextFloat() * 0.8F + 0.1F;
                        float var11 = this.rand.nextFloat() * 0.8F + 0.1F;
                        float var12 = this.rand.nextFloat() * 0.8F + 0.1F;

                        while (var9.stackSize > 0)
                        {
                            int var13 = this.rand.nextInt(21) + 10;

                            if (var13 > var9.stackSize)
                            {
                                var13 = var9.stackSize;
                            }

                            var9.stackSize -= var13;
                            EntityItem var14 = new EntityItem(par1World, (par2 + var10), (par3 + var11), (par4 + var12), new ItemStack(var9.getItem(), var13, var9.getItemDamage()));

                            if (var9.hasTagCompound())
                            {
                                var14.getEntityItem().setTagCompound((NBTTagCompound)var9.getTagCompound().copy());
                            }

                            float var15 = 0.05F;
                            var14.motionX = ((float)this.rand.nextGaussian() * var15);
                            var14.motionY = ((float)this.rand.nextGaussian() * var15 + 0.2F);
                            var14.motionZ = ((float)this.rand.nextGaussian() * var15);
                            par1World.spawnEntityInWorld(var14);
                        }
                    }
                }
            }
        }

        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }

    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        return Container.calcRedstoneFromInventory((IInventory) p_149736_1_.getTileEntity(p_149736_2_, p_149736_3_, p_149736_4_));
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityDeck();
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon(this.getTextureName() + "_side");
        this.iconTopH = p_149651_1_.registerIcon(this.getTextureName() + "_top_H");
        this.iconTopV = p_149651_1_.registerIcon(this.getTextureName() + "_top_V");
        this.iconFront = p_149651_1_.registerIcon(this.getTextureName() + "_face_" + (isActive ? "active" : "idle"));
    }
}
