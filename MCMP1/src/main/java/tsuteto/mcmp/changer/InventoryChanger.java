package tsuteto.mcmp.changer;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import tsuteto.mcmp.cassettetape.ItemCassetteTape;
import tsuteto.mcmp.core.mcmpplayer.McmpPlayerManager;
import tsuteto.mcmp.core.network.PacketDispatcher;
import tsuteto.mcmp.core.network.PacketManager;
import tsuteto.mcmp.core.util.McmpLog;

import java.util.ArrayList;
import java.util.List;

public class InventoryChanger extends InventoryBasic
{
    private static final int contentsSize = 27;

    private EntityPlayer owner;
    private ItemStack changer;
    public int slotPlaying = 0;
    public int playingInStack = 1;
    private String changerName = "";
    private boolean isPlayerControl;
    private boolean isLoading = false;

    /**
     * Constructor
     * @param player
     * @param itemstack
     */
    public InventoryChanger(EntityPlayer player, ItemStack itemstack, boolean isPlayerControl)
    {
        super("", false, contentsSize);
        this.owner = player;
        this.changer = itemstack;
        this.isPlayerControl = isPlayerControl;
        this.readFromNBT(changer.getTagCompound());
    }

    /**
     * Constructor for read-only mode
     * @param itemstack
     */
    public InventoryChanger(ItemStack itemstack)
    {
        this(null, itemstack, false);
    }

    public void setChangerName(String name)
    {
        this.changerName = name;
    }

    public void setNewChangerName(String name)
    {
        this.changerName = name;
        this.dispatchRenamePacket(name);
    }

    public String getChangerName()
    {
        return this.changerName;
    }

    public List<ItemStack> getSongList()
    {
        List<ItemStack> songList = new ArrayList<ItemStack>();
        for (int i = 0; i < contentsSize; i++)
        {
            ItemStack itemstack = getStackInSlot(i);
            if (itemstack != null && itemstack.getItem() instanceof ItemCassetteTape
                    && ItemCassetteTape.getSong(itemstack) != null)
            {
                songList.add(itemstack);
            }
        }
        return songList;
    }

    public int getNumSongsInside()
    {
        int numSongs = 0;
        for (ItemStack itemstack : this.getSongList())
        {
            numSongs += itemstack.stackSize;
        }
        return numSongs;
    }

    @Override
    public String getInventoryName()
    {
        return "container.mcmp.CassetteChanger";
    }

    protected void dispatchStatePacket()
    {
    	int locChanger = this.findMeInPlayerInventory();
    	McmpLog.debug("LOC:" + locChanger);
    	if (locChanger < 0) return;
    	
		McmpLog.debug("CHANGER-POS(%d, %d)", slotPlaying, playingInStack);
        PacketDispatcher.packet(
                new PacketChangerState(isPlayerControl, locChanger, slotPlaying, playingInStack)).sendToServer();
    }

    protected void dispatchRenamePacket(String var1)
    {
        PacketDispatcher.packet(new PacketChangerRename(var1)).sendToServer();
    }
    
    private int findMeInPlayerInventory()
    {
    	ItemStack[] inv = FMLClientHandler.instance().getClient().thePlayer.inventory.mainInventory;
    	for (int i = 0; i < inv.length; i++)
    	{
    		if (changer == inv[i]) return i;
    	}
    	return -1;
    }

    /**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        if (par1NBTTagCompound != null && par1NBTTagCompound.hasKey("McmpChanger"))
        {
            isLoading = true;
            NBTTagCompound changer = par1NBTTagCompound.getCompoundTag("McmpChanger");

            slotPlaying = changer.getShort("SlotPlaying");
            playingInStack = changer.getShort("PlayingInStack");
            changerName = changer.getString("Name");

            NBTTagList cassettes = changer.getTagList("Cassettes", Constants.NBT.TAG_COMPOUND);

            for (int var3 = 0; var3 < cassettes.tagCount(); ++var3)
            {
                NBTTagCompound var4 = cassettes.getCompoundTagAt(var3);
                int var5 = var4.getByte("Slot") & 255;

                if (var5 >= 0 && var5 < this.getSizeInventory())
                {
                    this.setInventorySlotContents(var5, ItemStack.loadItemStackFromNBT(var4));
                }
            }
            isLoading = false;
        }
    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        NBTTagCompound changer = new NBTTagCompound();

        changer.setShort("SlotPlaying", (short) slotPlaying);
        changer.setShort("PlayingInStack", (short) playingInStack);
        changer.setString("Name", changerName);

        NBTTagList cassetes = new NBTTagList();

        for (int var3 = 0; var3 < this.getSizeInventory(); ++var3)
        {
            if (this.getStackInSlot(var3) != null)
            {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                this.getStackInSlot(var3).writeToNBT(var4);
                cassetes.appendTag(var4);
            }
        }

        changer.setTag("Cassettes", cassetes);

        par1NBTTagCompound.setTag("McmpChanger", changer);
    }

    private void setNBTtoItemStack()
    {
        ItemStack actualItem;
        if (owner != null && isPlayerControl)
        {
            actualItem = this.owner.getCurrentEquippedItem();
        }
        else
        {
            actualItem = McmpPlayerManager.getActivePlayer();
        }

        if (actualItem != null)
        {
            actualItem.setTagCompound(this.changer.getTagCompound());
        }
    }

    public void updateState()
    {
        this.dispatchStatePacket();
        this.saveInventory();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();

        if (!isLoading)
        {
            this.saveInventory();
        }
    }

    public void saveInventory()
    {
        if (changer.getTagCompound() != null)
        {
            this.writeToNBT(changer.getTagCompound());
            this.setNBTtoItemStack();
        }
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

}
