package openmods.network.senders;

import java.util.Collection;

import io.netty.channel.Channel;
import openmods.utils.NetUtils;

public class PacketSenderBase implements IPacketSender {

    private final Channel channel;

    public PacketSenderBase(Channel channel) {
        this.channel = channel;
    }

    protected void configureChannel(Channel channel) {}

    protected void cleanupChannel(Channel channel) {}

    @Override
    public void sendMessage(Object msg) {
        configureChannel(channel);
        channel.writeAndFlush(msg).addListener(NetUtils.LOGGING_LISTENER);
    }

    @Override
    public void sendMessages(Collection<Object> msgs) {
        configureChannel(channel);

        for (Object msg : msgs) channel.write(msg).addListener(NetUtils.LOGGING_LISTENER);

        channel.flush();

        cleanupChannel(channel);
    }
}
