package com.xie.im.protocol.handler;

import com.xie.im.protocol.Message;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author xieyang
 */
public abstract class ProtocolHandler {


    public abstract void handleRequest(ChannelHandlerContext ctx, Message.Data data);

    public abstract int getCmd();

}
