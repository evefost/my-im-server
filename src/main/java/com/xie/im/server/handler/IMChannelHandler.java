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
package com.xie.im.server.handler;


import com.xie.im.protocol.Message;
import com.xie.im.server.SessionManager;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Handler implementation for the echo server.
 */
@Sharable
public class IMChannelHandler extends SimpleChannelInboundHandler<Message.Data> {

    protected  static Logger logger = LoggerFactory.getLogger(IMChannelHandler.class);


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        logger.debug("app client  激活 :" + ctx.channel().id());

    }



    /**
     * 检测到连接空闲事件，发送心跳请求命令
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //TODO
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.warn("exceptionCaught 异常关闭");
        cause.printStackTrace();
        ctx.close();
    }

    public void channelInactive(ChannelHandlerContext ctx) {
        logger.info("app client  失活 :" + ctx.channel().id());
        SessionManager.onChannelClose(ctx);
    }

    protected void channelRead0(ChannelHandlerContext ctx, Message.Data msg) throws Exception {
        logger.debug("app client  收到消息 :" + ctx.channel().id());
        SessionManager.dispatcherMessage(ctx,msg);
    }
}
