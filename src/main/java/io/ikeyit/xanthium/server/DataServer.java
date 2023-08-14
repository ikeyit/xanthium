package io.ikeyit.xanthium.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

/**
 *
 */
public class DataServer {
    private static final Logger log = LoggerFactory.getLogger(DataServer.class);

    private final ServerConfig serverConfig;

    private final int port;
    private final ScheduledThreadPoolExecutor heartbeatExecutor;

    public DataServer(final ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.port = serverConfig.getPort();
        this.heartbeatExecutor = new ScheduledThreadPoolExecutor(1, runnable -> new Thread(runnable, "heartbeatExecutor"));

    }

    public void start() throws InterruptedException {
        log.info("Start server at port: {}", port);
        startServer();
        startServices();
    }

    private void startServices() {
        heartbeatExecutor.s
    }

    private void startServer() {
        // 接受客户端链接的线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("bossGroup"));
        // 链接成功后，将交给这个线程组处理。这里指定2个EventLoop，每一个EventLoop单线程处理
        EventLoopGroup workerGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("workerGroup"));
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
                                    .addLast(new StringDecoder(StandardCharsets.UTF_8))
                                    .addLast(new StringEncoder(StandardCharsets.UTF_8))
                                    .addLast(new MessageHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        stopServices();
        stopServer();
    }

    private void stopServer() {

    }

    private void stopServices() {
        heartbeatExecutor.shutdown();
    }
}
