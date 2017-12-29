package com.xie.im.protocol.handler;




import com.xie.im.protocol.Message;
import com.xie.im.server.IMSession;
import io.netty.channel.ChannelHandlerContext;

import static com.xie.im.server.SessionManager.createScession;
import static com.xie.im.server.SessionManager.getSession;


/**
 * 绑定客户端信息:用户登录前
 */
public class BindClientHandler extends ProtocolHandler {

    @Override
    public void handleRequest(ChannelHandlerContext ctx, Message.Data data) {
        System.out.println("绑定客户端,消息...");
        Message.Data.Builder reply = Message.Data.newBuilder();
        reply.setId(data.getId());
        reply.setCmd(Message.Data.Cmd.BIND_CLIENT_VALUE);
        reply.setCreateTime(System.currentTimeMillis());
        IMSession session = getSession(data.getClientId());
        if (session != null) {
            System.out.println("已经绑定过:{}"+data.getClientId());
            reply.setContent("已经绑定过");
            session.write(reply.build());
            return;
        }
        System.out.println("绑定{}客户端,创建会话..."+data.getClientName());
        IMSession newSession = createScession(ctx, data);
        reply.setContent("绑定成功");
        newSession.write(reply.build());
    }

    @Override
    public int getCmd() {
        return Message.Data.Cmd.BIND_CLIENT_VALUE;
    }


}
