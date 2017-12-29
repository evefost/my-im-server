package com.xie.im.server;

import com.googlecode.protobuf.format.JsonFormat;

import com.xie.im.protocol.Message;
import com.xie.im.protocol.handler.ProtocolHandler;
import com.xie.im.util.ProtocolHandllerLoader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionManager {

    protected  static Logger logger = LoggerFactory.getLogger(SessionManager.class);

    /**
     * key:clientId;
     * value:IMSession
     */
    private static HashMap<String, IMSession> sessions = new HashMap<String, IMSession>();
    /**
     * 用户maping
     * key:uid;
     * value:clientId
     */
    private static HashMap<String, String> loginUsers = new HashMap<String, String>();


    /**
     * bind的通导
     * key:channeladdress;
     * value:clientId
     */
    private static HashMap<String, String> bindChannels = new HashMap<String, String>();


    public static void dispatcherMessage(ChannelHandlerContext ctx, Object msg) {
        Message.Data data = null;
        if (msg instanceof Message.Data) {
            data = (Message.Data) msg;
            logger.debug("消息类型[probuf]:{}",data.getContent());
        } else if (msg instanceof TextWebSocketFrame) {
            logger.debug("消息类型[TextWebSocketFrame]");
            TextWebSocketFrame textframe = (TextWebSocketFrame) msg;
            String jsonFormat = textframe.text();

            Message.Data.Builder builder = Message.Data.newBuilder();
            try {
                logger.debug("json转成probuf[{}" ,builder.getContent());
                JsonFormat.merge(jsonFormat, builder);
                builder.setDecodeType(IMSession.DECODE_TYPE_WEB_TEXT);
                data = builder.build();

            } catch (JsonFormat.ParseException e) {
                logger.warn("TextWebSocketFrame 消息解码失败:{}",jsonFormat);
            }
        } else if (msg instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) msg;
            ByteBuf buf = binaryWebSocketFrame.content();
            byte[] b = new byte[buf.readableBytes()];
            buf.readBytes(b);
            try {
                data = Message.Data.newBuilder().mergeFrom(b).setDecodeType(IMSession.DECODE_TYPE_WEB_BINARY).build();
                logger.debug("消息类型[BinaryWebSocketFrame]{}",data.getContent());
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("BinaryWebSocketFrame 消息解码失败");
            }
        } else {
            logger.warn("消息类型[不支持该类型]");
        }
        if (data != null) {
            ProtocolHandler protocolHandler = ProtocolHandllerLoader.getProtocolHandler(data.getCmd());
            if (protocolHandler != null) {
                protocolHandler.handleRequest(ctx, data);
            }else {
                logger.warn("找不到对应的消息处理器");
            }
        }

    }

    public static IMSession createScession(ChannelHandlerContext ctx, Message.Data data) {
        IMSession newSession = IMSession.buildSesion(ctx, data);
        newSession.setDecodeType(data.getDecodeType());
        sessions.put(newSession.getClientId(), newSession);
        bindChannels.put(ctx.channel().remoteAddress().toString(), data.getClientId());
        return newSession;
    }


    public static void onLogin(IMSession newSession) {
        sessions.put(newSession.getClientId(), newSession);
        loginUsers.put(newSession.getUid(), newSession.getClientId());
    }

    public static IMSession getSession(String clientId) {
        return sessions.get(clientId);
    }

    public static IMSession getSessionByUid(String uid) {
        String clientId = loginUsers.get(uid);
        if(clientId == null){
            return null;
        }
        return sessions.get(clientId);
    }

    public static void removeSession(IMSession session) {
        sessions.remove(session.getClientId());
        loginUsers.remove(session.getUid());
        session.close(true);
    }


    public static void reply(IMSession newSession, String msgId, Integer cmd) {
        Message.Data.Builder reply = Message.Data.newBuilder();
        reply.setId(msgId);
        reply.setCmd(cmd);
        reply.setCreateTime(System.currentTimeMillis());
        newSession.write(reply.build());
    }

    public static void onChannelClose(ChannelHandlerContext ctx) {
        System.out.println("销毁会话...");
        String clienId = bindChannels.get(ctx.channel().remoteAddress().toString());
        if (clienId != null) {
            IMSession imSession = sessions.get(clienId);
            sessions.remove(clienId);
            if (imSession != null) {
                String uid = imSession.getUid();
                if (uid != null) {
                    loginUsers.remove(uid);
                }
            }

        }
    }

    public static void bradcast(Message.Data data) {

        Set<Map.Entry<String, IMSession>> entries = sessions.entrySet();
        for (Map.Entry<String, IMSession> entry : entries) {
            entry.getValue().write(data);
        }
    }

}
