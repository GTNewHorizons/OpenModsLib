package openmods.network;

import java.util.Collection;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.handshake.NetworkDispatcher;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.AttributeKey;

public class ExtendedOutboundHandler extends ChannelOutboundHandlerAdapter {

    public static final AttributeKey<IPacketTargetSelector<?>> MESSAGETARGET = new AttributeKey<IPacketTargetSelector<?>>(
            "om:outboundTarget");

    private static <T> Collection<NetworkDispatcher> getDispatchers(IPacketTargetSelector<T> target, Object arg) {
        final Collection<NetworkDispatcher> output = Lists.newArrayList();
        target.listDispatchers(target.castArg(arg), output);
        return output;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof FMLProxyPacket)) {
            ctx.write(msg);
            return;
        }

        final Channel channel = ctx.channel();

        final IPacketTargetSelector<?> target = channel.attr(MESSAGETARGET).get();
        if (target == null) {
            ctx.write(msg);
            return;
        }

        final FMLProxyPacket pkt = (FMLProxyPacket) msg;

        final Side channelSide = channel.attr(NetworkRegistry.CHANNEL_SOURCE).get();

        Preconditions.checkState(target.isAllowedOnSide(channelSide), "Packet not allowed on side");

        final String channelName = channel.attr(NetworkRegistry.FML_CHANNEL).get();

        Object arg = channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).get();

        try {
            final Collection<NetworkDispatcher> dispatchers = getDispatchers(target, arg);
            for (NetworkDispatcher dispatcher : dispatchers) dispatcher.sendProxy(pkt);

        } catch (Throwable t) {

            throw new IllegalStateException(
                    String.format(
                            "Failed to select and send message (selector %s, arg: %s, channel: %s, side: %s)",
                            target,
                            arg,
                            channelName,
                            channelSide),
                    t);
        }

    }

    public static void install(Map<Side, FMLEmbeddedChannel> channels) {
        for (Side side : Side.values()) install(channels.get(side));
    }

    public static void install(FMLEmbeddedChannel fmlEmbeddedChannel) {
        fmlEmbeddedChannel.pipeline().addAfter("fml:outbound", "om:outbound", new ExtendedOutboundHandler());
    }

}
