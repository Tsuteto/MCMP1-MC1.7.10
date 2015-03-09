package tsuteto.mcmp.core.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.minecraft.entity.player.EntityPlayer;

public interface MessageToClient
{
    public IMessage handleClientSide(EntityPlayer player);
}
