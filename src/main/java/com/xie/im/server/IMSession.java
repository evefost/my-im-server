package com.xie.im.server;


import com.xie.im.protocol.Message;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import static com.googlecode.protobuf.format.JsonFormat.printToString;

public class IMSession implements Serializable {

    protected  static Logger logger = LoggerFactory.getLogger(IMSession.class);


    public static final int TYPE_APP = 0;
    public static final int TYPE_WEB = 1;
    public static final int DECODE_TYPE_PROBUF = 0;
    public static final int DECODE_TYPE_WEB_TEXT = 1;
    public static final int DECODE_TYPE_WEB_BINARY = 2;
    private static final long serialVersionUID = 1L;

    private Channel channel;
    private String clientId;
    private String clientName;
    private String clientVersion;
    private long bindTime;
    private String uid;
    private long loginTime;
    private String encriptKey;
    private int sessionType;
    //解码类型
    private int decodeType;

    private IMSession(Channel channel) {
        this.channel = channel;
    }

    public static IMSession buildSesion(ChannelHandlerContext ctx, Message.Data data) {
        IMSession session = new IMSession(ctx.channel());
        session.setClientId(data.getClientId());
        session.setClientName(data.getClientName());
        session.setClientVersion(data.getClientVersion());
        session.setBindTime(new Date().getTime());
        session.setEncriptKey(UUID.randomUUID().toString().substring(0, 6));
        return session;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getDecodeType() {
        return decodeType;
    }

    public void setDecodeType(int decodeType) {
        this.decodeType = decodeType;
    }

    public int getSessionType() {
        return sessionType;
    }

    public void setSessionType(int sessionType) {
        this.sessionType = sessionType;
    }

    public String getEncriptKey() {
        return encriptKey;
    }

    public void setEncriptKey(String encriptKey) {
        this.encriptKey = encriptKey;
    }

    public long getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public boolean write(Message.Data msg) {
        if (channel != null && channel.isActive()) {

            if (decodeType == DECODE_TYPE_WEB_TEXT) {
                String txt = printToString(msg);
                logger.debug("传输格式:TextWebSocketFrame");
                TextWebSocketFrame txframe = new TextWebSocketFrame(txt);
                channel.writeAndFlush(txframe).awaitUninterruptibly(5000);

            } else if (decodeType == DECODE_TYPE_WEB_BINARY) {
                logger.debug("传输格式:BinaryWebSocketFrame");
                ByteBuf byteBuf = Unpooled.wrappedBuffer(msg.toByteArray());
                BinaryWebSocketFrame binaryWebSocketFrame = new BinaryWebSocketFrame(byteBuf);
                channel.writeAndFlush(binaryWebSocketFrame).awaitUninterruptibly(5000);

            } else {
                logger.debug("传输格式:probuf");
                return channel.writeAndFlush(msg).awaitUninterruptibly(5000);
            }

        }
        return false;
    }

    public void close(boolean immediately) {
        if (channel != null) {
            channel.disconnect();
            channel.close();
        }
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public long getBindTime() {
        return bindTime;
    }

    public void setBindTime(long bindTime) {
        this.bindTime = bindTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}