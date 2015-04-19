package tsuteto.mcmp.core.media;

import net.minecraft.item.ItemStack;
import tsuteto.mcmp.core.song.MediaSongEntry;

public interface IMcmpMedia
{
    void setSong(ItemStack itemstack, MediaSongEntry entry);
    MediaSongEntry getSong(ItemStack itemstack);
    String getSongName(ItemStack itemstack);
}
