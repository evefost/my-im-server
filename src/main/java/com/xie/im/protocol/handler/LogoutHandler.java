package com.xie.im.protocol.handler;



import com.xie.im.protocol.Message;
import com.xie.im.server.IMSession;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.xie.im.server.SessionManager.getSessionByUid;
import static com.xie.im.server.SessionManager.removeSession;


/***
 * 登出处理
 *
 * @author mis
 */
public class LogoutHandler extends ProtocolHandler {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void handleRequest(ChannelHandlerContext ctx, Message.Data data) {
        logger.debug("登出消息");
        try {
            IMSession session = getSessionByUid(data.getSenderId());

//			String account =ios.getTag(CIMConstant.SESSION_KEY).toString();
//			ios.removeTag(CIMConstant.SESSION_KEY);
            //getSessionManager().removeSession(account);
            removeSession(session);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCmd() {
        return Message.Data.Cmd.LOGOUT_VALUE;
    }

}
