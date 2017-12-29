package com.xie.im.server.handler;

import com.xie.im.server.SessionManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

	//private MsgProcessor processor = new MsgProcessor();

	protected  static Logger logger = LoggerFactory.getLogger(IMChannelHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {  //(3)
		logger.debug("web client  收到消息 :{}" , ctx.channel().id());
		SessionManager.dispatcherMessage(ctx,msg);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {   //(2)
		logger.debug("web client  激活 :{}" ,ctx.channel().id());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {  //(4)
		logger.debug("web client  失活 :{}" ,ctx.channel().id());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {//异常
		logger.warn("Client " + ctx.channel().remoteAddress() + "异常");
		cause.printStackTrace();
		ctx.close();
	}



	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {  //(5)
		logger.info("Client " + ctx.channel().remoteAddress() + "离开");
		//processor.logout(ctx.channel());
	}

}
