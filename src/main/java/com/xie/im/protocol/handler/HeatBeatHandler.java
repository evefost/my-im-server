package com.xie.im.protocol.handler;


import com.xie.im.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeatBeatHandler extends ProtocolHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void handleRequest(ChannelHandlerContext ctx, Message.Data data) {
        logger.debug("收到客户端心跳");
        try {
//            String key = "123";
//            String dstring = new String(XXTEA.decrypt(data.getBody().toByteArray(), key));
//            System.out.println("decrpt:" + dstring);
        } catch (Exception e) {
            logger.debug("Ex:" + e.toString());
        }
        ctx.writeAndFlush(data);
    }

    @Override
    public int getCmd() {
        return Message.Data.Cmd.HEARTBEAT_VALUE;
    }

}
