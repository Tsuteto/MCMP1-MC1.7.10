package tsuteto.mcmp.core.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import tsuteto.mcmp.core.util.McmpLog;

public class PacketManager
{
    private static SimpleNetworkWrapper networkHandler = null;
    private int id = 0;

    public static PacketManager init(String modId)
    {
        networkHandler = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
        return new PacketManager();
    }

    public static SimpleNetworkWrapper getNetworkHandler()
    {
        return networkHandler;
    }

    private PacketManager() {}

    @SuppressWarnings("unchecked")
    public PacketManager registerPacket(Class<? extends AbstractPacket> packetClass)
    {
        Class<AbstractPacket> message = (Class<AbstractPacket>)packetClass;
        if (MessageToServer.class.isAssignableFrom(packetClass))
        {
            networkHandler.registerMessage(packetClass, message, id, Side.SERVER);
            McmpLog.debug("Registered Packet: %s at ID %d", packetClass.getName(), id);
            id++;
        }

        if (MessageToClient.class.isAssignableFrom(packetClass))
        {
            networkHandler.registerMessage(packetClass, message, id, Side.CLIENT);
            McmpLog.debug("Registered Packet: %s at ID %d", packetClass.getName(), id);
            id++;
        }
        return this;
    }

}
