/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.xie.im.server;


import com.xie.im.protocol.IMDecoder;
import com.xie.im.protocol.Message;
import com.xie.im.server.handler.HttpHandler;
import com.xie.im.server.handler.IMChannelHandler;
import com.xie.im.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A HTTP server which serves Web Socket requests at:
 * <p/>
 * http://localhost:8080/websocket
 * <p/>
 * Open your browser at http://localhost:8080/, then the demo page will be loaded and a Web Socket connection will be
 * made automatically.
 * <p/>
 * This server illustrates support for the different web socket specification versions and will work with:
 * <p/>
 * <ul>
 * <li>Safari 5+ (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 6-13 (draft-ietf-hybi-thewebsocketprotocol-00)
 * <li>Chrome 14+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Chrome 16+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * <li>Firefox 7+ (draft-ietf-hybi-thewebsocketprotocol-10)
 * <li>Firefox 11+ (RFC 6455 aka draft-ietf-hybi-thewebsocketprotocol-17)
 * </ul>
 */
public class IMServer {

    protected  static Logger logger = LoggerFactory.getLogger(IMServer.class);


    public static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", SSL ? "8443" : "8080"));
    private static int port = 8888;


//    public int getPort() {
//        return port;
//    }
//
//    public void setPort(int port) {
//        this.port = port;
//    }

    public void start() {

        new Thread(new Runnable() {

            public void run() {
                try {
                    main(null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) throws Exception {
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            logger.info("新websocket接入 id:" + ch.id());

                            pipeline.addLast(new IMDecoder(Message.Data.getDefaultInstance()));
                            pipeline.addLast(new ProtobufEncoder());
                            pipeline.addLast(new IMChannelHandler());


                            //======== 对HTTP协议的支持  ==========
                            //Http请求解码器
                            pipeline.addLast(new HttpServerCodec());
                            //主要就是将一个http请求或者响应变成一个FullHttpRequest对象
                            pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                            //这个是用来处理文件流
                            pipeline.addLast(new ChunkedWriteHandler());
                            //处理HTTP请求的业务逻辑
                            pipeline.addLast(new HttpHandler());


                            //======== 对WebSocket协议的支持  ==========
                            //加上这个Handler就已经能够解析WebSocket请求了
                            //相当于WebSocket解码器
                            //im是为了和http请求区分开来，以im开头的请求都有websocket来解析
                            pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                            //实现处理WebSocket逻辑的Handler
                            pipeline.addLast(new WebSocketHandler());


                        }
                    });

            Channel ch = b.bind(port).sync().channel();

            logger.debug("Open your web browser and navigate to " +
                    (SSL ? "https" : "http") + "://127.0.0.1:" + port + '/');

            ch.closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
