package com.xie.im.protocol.handler;

import com.xie.im.protocol.Message;
import com.xie.im.server.IMSession;
import com.xie.im.server.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xie.im.server.SessionManager.getSessionByUid;


public class ChatMsgHandler extends ProtocolHandler {

    protected   Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void handleRequest(ChannelHandlerContext ctx, Message.Data data) {
        logger.debug("普通聊天消息");

        //先保存消息，用户收到才删除
        saveMessage(data);
        //回应客户端
        Message.Data.Builder reply = Message.Data.newBuilder();
        reply.setId(data.getId());
        reply.setCmd(Message.Data.Cmd.CHAT_TXT_ECHO_VALUE);
        reply.setCreateTime(data.getCreateTime());
        getSessionByUid(data.getSenderId()).write(reply.build());
     //   ctx.writeAndFlush(reply);

        IMSession receiverSession = getSessionByUid(data.getReceiverId());
        if (receiverSession != null) {
            receiverSession.write(data);
        } else {
            //该用户不存在或没在线
            logger.debug(data.getReceiverId()+"该用户不存在或没在线...");
        }
        SessionManager.bradcast(data);


    }

    /**
     * 保存消息
     */
    private void saveMessage(Message.Data data) {

    }

    @Override
    public int getCmd() {
        return Message.Data.Cmd.CHAT_TXT_VALUE;
    }

}
