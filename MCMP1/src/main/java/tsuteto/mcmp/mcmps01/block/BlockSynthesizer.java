package tsuteto.mcmp.mcmps01.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import tsuteto.mcmp.mcmps01.block.tileentity.TileEntitySynthesizer;

/**
 * Created by Tsuteto on 15/04/13.
 */
public class BlockSynthesizer extends BlockContainer
{
    protected BlockSynthesizer(Material p_i45394_1_)
    {
        super(p_i45394_1_);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntitySynthesizer();
    }
}
