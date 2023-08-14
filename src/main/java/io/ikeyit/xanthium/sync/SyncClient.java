package io.ikeyit.xanthium.sync;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpMethod.POST;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class SyncClient {
    private final InetSocketAddress address;
    private Channel channel;
    private Bootstrap bootstrap;

    public SyncClient(Bootstrap boostrap, InetSocketAddress address) {
        this.bootstrap = boostrap;
        this.address = address;
    }

    public void connect() {
        if (channel != null)
            return;

        channel = bootstrap.connect(address).syncUninterruptibly().channel();
        System.out.println("Connected to [" + HOST + ':' + PORT + ']');

        // Wait for the HTTP/2 upgrade to occur.
        Http2SettingsHandler http2SettingsHandler = initializer.settingsHandler();
        http2SettingsHandler.awaitSettings(5, TimeUnit.SECONDS);

        HttpResponseHandler responseHandler = initializer.responseHandler();
        int streamId = 3;
        HttpScheme scheme = SSL ? HttpScheme.HTTPS : HttpScheme.HTTP;
        AsciiString hostName = new AsciiString(HOST + ':' + PORT);
        System.err.println("Sending request(s)...");
        if (URL != null) {
            // Create a simple GET request.
            FullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, GET, URL, Unpooled.EMPTY_BUFFER);
            request.headers().add(HttpHeaderNames.HOST, hostName);
            request.headers().add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), scheme.name());
            request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);
            responseHandler.put(streamId, channel.write(request), channel.newPromise());
            streamId += 2;
        }
        if (URL2 != null) {
            // Create a simple POST request with a body.
            FullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, POST, URL2,
                    wrappedBuffer(URL2DATA.getBytes(CharsetUtil.UTF_8)));
            request.headers().add(HttpHeaderNames.HOST, hostName);
            request.headers().add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), scheme.name());
            request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            request.headers().add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);
            responseHandler.put(streamId, channel.write(request), channel.newPromise());
        }
        channel.flush();
        responseHandler.awaitResponses(5, TimeUnit.SECONDS);
        System.out.println("Finished HTTP/2 request(s)");

        // Wait until the connection is closed.
        channel.close().syncUninterruptibly();
    }
}
