package tsuteto.mcmp.deck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import tsuteto.mcmp.core.Mcmp1Core;
import tsuteto.mcmp.core.mcmpplayer.controller.McmpPlacedPlayerController;
import tsuteto.mcmp.core.media.IMcmpMedia;
import tsuteto.mcmp.core.songselector.SongSelector;
import tsuteto.mcmp.core.songselector.SongSelectorNext;
import tsuteto.mcmp.core.util.McmpLog;

public class TileEntityDeck extends TileEntity implements ISidedInventory
{
    private static final int[] slots = new int[] {0};

    public McmpPlacedPlayerController controller;
    private SongSelector songSelector;

    private ItemStack[] media = new ItemStack[1];
    protected int timeInterval = 0;
    private String customName;

    public void setMedia(ItemStack itemStack)
    {
        this.media[0] = itemStack;
    }

    public ItemStack getMedia()
    {
        return media[0];
    }

    @Override
    public void updateEntity()
    {
        // TODO: this logic causes wrong behavior, it should be moved to server side
        if (this.worldObj.isRemote && this.controller != null)
        {
            if (songSelector == null)
            {
                songSelector = new SongSelectorNext(this.controller);
            }

            boolean sndPlaying = this.controller.getAudioPlayer().playing();

            if (this.controller.isPlayerPlaying && !sndPlaying && isActive())
            {
                if (this.timeInterval > 0)
                {
                    this.timeInterval--;
                }
                else
                {
                    ItemStack nextSong = songSelector.selectSong(media);
                    if (nextSong != null)
                    {
                        play(nextSong);
                    }
                    else
                    {
                        stop();
                        this.controller.playPos.slotPlaying = 0;
                        this.controller.playPos.playingInStack = 0;
                    }
                    this.timeInterval = 20;
                }
            }

            if (this.controller.isPlayerPaused && !this.controller.getAudioPlayer().paused())
            {
                pause();
            }
        }
    }

    private boolean isActive()
    {
        return this.worldObj.getBlock(xCoord, yCoord, zCoord) == Mcmp1Core.blockDeckActive;
    }

    public void play()
    {
        if (controller != null)
        {
            if (!worldObj.isRemote)
            {
                BlockDeck.updateBlockState(true, worldObj, xCoord, yCoord, zCoord);
                this.markDirty();
            }
            this.controller.isPlayerPlaying = true;
        }
    }

    private void play(ItemStack song)
    {
        this.controller.playAtPosition(worldObj, null, song);
    }

    public void pause()
    {
        this.controller.pause(worldObj);
    }

    public void resume()
    {
        this.controller.resume(worldObj);
    }

    public void stop()
    {
        if (!worldObj.isRemote && !tileEntityInvalid)
        {
            BlockDeck.updateBlockState(false, worldObj, xCoord, yCoord, zCoord);
            this.markDirty();
        }
        this.controller.stop(worldObj);
    }

    @Override
    public void onChunkUnload()
    {
        if (worldObj.isRemote && this.controller != null) this.stop();
    }

    @Override
    public void invalidate()
    {
        super.invalidate();
        if (worldObj.isRemote && this.controller != null)
        {
            this.controller.stop(worldObj);
            McmpLog.debug("invalidate");
//            TileEntity anotherTe = worldObj.getTileEntity(xCoord, yCoord, zCoord);
//            if (!(anotherTe instanceof TileEntityDeck))
//            {
//                this.controller.stop(worldObj);
//            }
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        this.writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 4, nbttagcompound);
    }

    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void readFromNBT(NBTTagCompound p_145839_1_)
    {
        super.readFromNBT(p_145839_1_);

        NBTTagList nbttaglist = p_145839_1_.getTagList("Items", 10);
        this.media = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.media.length)
            {
                this.media[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        if (this.controller == null)
        {
            this.controller = new McmpPlacedPlayerController(xCoord, yCoord, zCoord);
        }
        this.controller.isPlayerPlaying = this.media[0] != null;
        this.controller.isPlayerPaused = p_145839_1_.getBoolean("Paused");
        this.controller.readFromNBT(p_145839_1_);

        if (p_145839_1_.hasKey("CustomName", 8))
        {
            this.customName = p_145839_1_.getString("CustomName");
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound p_145841_1_)
    {
        super.writeToNBT(p_145841_1_);

        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.media.length; ++i)
        {
            if (this.media[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.media[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        p_145841_1_.setTag("Items", nbttaglist);
        p_145841_1_.setBoolean("Paused", this.controller.isPlayerPaused);
        this.controller.writeToNBT(p_145841_1_);

        if (this.hasCustomInventoryName())
        {
            p_145841_1_.setString("CustomName", this.customName);
        }

    }

    @Override
    public int[] getAccessibleSlotsFromSide(int p_94128_1_)
    {
        return slots;
    }

    @Override
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
    {
        return p_102007_2_ != null && p_102007_2_.getItem() instanceof IMcmpMedia;
    }

    @Override
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
    {
        return p_102008_2_ != null && p_102008_2_.getItem() instanceof IMcmpMedia;
    }

    @Override
    public int getSizeInventory()
    {
        return this.media.length;
    }

    @Override
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return this.media[p_70301_1_];
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        if (this.media[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.media[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.media[p_70298_1_];
                this.media[p_70298_1_] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.media[p_70298_1_].splitStack(p_70298_2_);

                if (this.media[p_70298_1_].stackSize == 0)
                {
                    this.media[p_70298_1_] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        if (this.media[p_70304_1_] != null)
        {
            ItemStack itemstack = this.media[p_70304_1_];
            this.media[p_70304_1_] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {
        this.media[p_70299_1_] = p_70299_2_;

        if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit())
        {
            p_70299_2_.stackSize = this.getInventoryStackLimit();
        }
    }

    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.customName : "container.mcmpDeck";
    }

    public boolean hasCustomInventoryName()
    {
        return this.customName != null && this.customName.length() > 0;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && p_70300_1_.getDistanceSq((double) this.xCoord + 0.5D, (double) this.yCoord + 0.5D, (double) this.zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return p_94041_2_ != null && p_94041_2_.getItem() instanceof IMcmpMedia;
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
    }
}
