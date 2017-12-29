package com.xie.im.client;

import com.xie.im.protocol.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;
import java.util.UUID;

public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    private String nickName;
    private ChannelHandlerContext ctx;


    public ChatClientHandler(String nickName) {
        this.nickName = nickName;
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;

        System.out.println("成功连接至服务器，已执行登录动作");
        session();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if(msg instanceof Message.Data){
            System.out.println("app client 收到消息:"+((Message.Data) msg).getContent());
        }
    }

    /**
     * 从控制输入消息内容，搞一个独立的线程
     */
    private void session() {
        new Thread() {

            public void run() {
                System.out.println(nickName + ",你好，请在控制台输入消息内容");
                Message.Data.Builder msg = null;
                Scanner sc = new Scanner(System.in);
                do {
                    String content = sc.nextLine();
                     msg = Message.Data.newBuilder();

                    if ("login".equals(content)) {
                        msg.setCmd(Message.Data.Cmd.LOGIN_VALUE);
                    } else  {
                        msg.setCmd(Message.Data.Cmd.CHAT_TXT_VALUE);
                    }

                    msg.setId(UUID.randomUUID().toString());

                    msg.setCreateTime(System.currentTimeMillis());
                    msg.setSenderId("777777");
                    msg.setReceiverId("666666");
                    msg.setContent(content);
                } while (sendMsg(msg));
                sc.close();
            }

        }.start();
    }

    /**
     * 往服务器端发送消息
     *
     * @param msg
     * @return
     */
    private boolean sendMsg(Message.Data.Builder msg) {
        ctx.channel().writeAndFlush(msg);
        System.out.println("消息已发送至服务器,请继续输入");
        return true;
    }

}
