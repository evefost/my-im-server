package com.xie.im.protocol;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.protobuf.ProtobufDecoder;

import java.util.List;

/**
 * IM协议解码器，输入，客户端请求过来以后，要解码
 * @author Tom
 *
 */
public class IMDecoder extends ProtobufDecoder {


    public IMDecoder(MessageLite prototype) {
        super(prototype);
    }

    public IMDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
        super(prototype, extensionRegistry);
    }

    public IMDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
        super(prototype, extensionRegistry);
    }


    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        try {
            super.decode(ctx, msg, out);
//            msg.clear();
        }catch (Exception e){
            ctx.channel().pipeline().remove(this);
            msg.retain();
            ctx.fireChannelRead(msg );

            //ctx.fireChannelRead(msg);
            //msg.release();
        }

    }
}

