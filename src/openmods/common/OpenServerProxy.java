package openmods.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import openmods.interfaces.IOpenModsProxy;
import cpw.mods.fml.common.network.Player;

public class OpenServerProxy implements IOpenModsProxy {

    @Override
    public boolean isServerOnly() {
        return true;
    }

    @Override
    public boolean isServerThread() {
        return true;
    }

    /**
     * Checks if this game is SinglePlayer
     * 
     * @return true if this is single player
     */
    public boolean isSinglePlayer() {
        // Yeah I know it doesn't matter now but why not have it :P
        MinecraftServer serverInstance = MinecraftServer.getServer();
        if (serverInstance == null)
            return false;
        return serverInstance.isSinglePlayer();
    }

    @Override
    public EntityPlayer getThePlayer() {
        return null;
    }

    @Override
    public long getTicks(World worldObj) {
        return worldObj.getTotalWorldTime();
    }

    @Override
    public World getClientWorld() {
        return null;
    }

    @Override
    public World getServerWorld(int id) {
        return DimensionManager.getWorld(id);
    }

    @Override
    public void sendPacketToPlayer(Player player, Packet packet) {
        if (player instanceof EntityPlayerMP)
            ((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
        else
            throw new UnsupportedOperationException("HOW DO I PACKET?");
    }

    @Override
    public void sendPacketToServer(Packet packet) {}

}
