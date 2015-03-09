package tsuteto.mcmp.cassettetape;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import tsuteto.mcmp.core.audio.McmpSoundManager;
import tsuteto.mcmp.core.song.MediaSongEntry;
import tsuteto.mcmp.core.song.SongInfo;

public class ItemCassetteTape extends Item
{
    public static final int[] colors = new int[] {
        0x1e1b1b, // black
        0xe83929, // red
        0x3cb371, // green
        0x965042, // brown
        0x8080ff, // blue
        0xb872db, // purple
        0x40e0d0, // cyan
        0xc0c0c8, // silver
        0x808080, // gray
        0xffc0cb, // pink
        0x60ff60, // lime
        0xdecf2a, // yellow
        0xa0d8ef, // lightBlue
        0xe4007f, // magenta
        0xeb8844, // orange
        0xffffff // white
    };

    public ItemCassetteTape()
    {
        super();
        setMaxDamage(0);
        setHasSubtypes(true);
    }

    /**
     * Returns the metadata of the block which this Item (ItemBlock) can place
     */
    @Override
    public int getMetadata(int par1)
    {
        return par1;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack)
    {
        return super.getUnlocalizedName() + "." + ItemDye.field_150923_a[getDyeTypeFromDamage(par1ItemStack.getItemDamage())]; // dyeColorNames
    }

    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        return colors[getDyeTypeFromDamage(par1ItemStack.getItemDamage())];
    }

    public static int getDyeTypeFromDamage(int par0)
    {
        return ~par0 & 15;
    }

    public static int getDamageFromDyeType(int par0)
    {
        return ~par0 & 15;
    }

    public static void setSong(ItemStack itemstack, MediaSongEntry entry)
    {
        if (itemstack.stackTagCompound == null)
        {
            itemstack.setTagCompound(new NBTTagCompound());
        }

        itemstack.stackTagCompound.setTag("mcmp", new NBTTagList());

        NBTTagList nbttaglist = (NBTTagList) itemstack.stackTagCompound.getTag("mcmp");
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setByte("s", (byte) entry.source.ordinal());
        nbttagcompound.setString("t", entry.id);
        nbttaglist.appendTag(nbttagcompound);
    }

    public static MediaSongEntry getSong(ItemStack itemstack)
    {
        NBTTagCompound stackTagCompound = itemstack.getTagCompound();

        if (stackTagCompound != null && stackTagCompound.hasKey("mcmp"))
        {
            NBTTagList nbttaglist = (NBTTagList) stackTagCompound.getTag("mcmp");
            if (nbttaglist != null)
            {
                return new MediaSongEntry(
                        nbttaglist.getCompoundTagAt(0).getByte("s"),
                        nbttaglist.getCompoundTagAt(0).getString("t")
                        );
            }
        }
        return null;
    }

    @Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean bool)
    {
        MediaSongEntry song = getSong(itemstack);
        if (song == null)
        {
            return;
        }
        if (song.source == Source.HDD)
        {
            SongInfo info = McmpSoundManager.getInstance().getSongManager().getSongInfo(song);
            if (info != null)
            {
                list.add(info.songName);
            }
            else
            {
                list.add(StatCollector.translateToLocalFormatted("mcmp1.fileNotFound", song.id));
            }
        }
        else if (song.source == Source.RECORDS)
        {
            ItemRecord itemrecord = ItemRecord.getRecord("records." + song.id);
            if (itemrecord != null)
            {
                list.add(itemrecord.getRecordNameLocal());
            }
            else
            {
                list.add(StatCollector.translateToLocalFormatted("mcmp1.recordNotFound", song.id));
            }
        }
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     */
    @Override
    public void getSubItems(Item var1, CreativeTabs var2, List var3)
    {
        for (int var4 = 0; var4 < 16; ++var4)
        {
            var3.add(new ItemStack(var1, 1, var4));
        }
    }

    public enum Source
    {
        RECORDS, HDD;
    }
}
