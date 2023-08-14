package io.ikeyit.xanthium.sync;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class SyncApi {
    private final Map<InetSocketAddress, SyncClient> clientMap = new HashMap<>();

    private final Bootstrap bootstrap;

    public SyncApi() {
        final SslContext sslCtx = null;
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Http2ClientInitializer initializer = new Http2ClientInitializer(sslCtx, Integer.MAX_VALUE);

        try {
            bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.handler(initializer);


        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public void sendHeartBeat(InetSocketAddress address) {

    }
}
