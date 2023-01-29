package openmods.network;

import java.util.Collection;

import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.relauncher.Side;

public interface IPacketTargetSelector<T> {

    public boolean isAllowedOnSide(Side side);

    public T castArg(Object arg);

    public void listDispatchers(T arg, Collection<NetworkDispatcher> result);
}
