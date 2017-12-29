package com.xie.im.util;




import com.xie.im.protocol.handler.ProtocolHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 协议加载器
 *
 * @author xieyang
 */
public class ProtocolHandllerLoader {


    private static Map<Integer, ProtocolHandler> handlers = new HashMap<Integer, ProtocolHandler>();

    static {
        System.out.println("加载protocol handlers...");
        try {
            Class cls = ProtocolHandler.class;
            List<String> packages = new ArrayList<String>();
            packages.add("com.xie.im.protocol.handler");
            List<Class<?>> tocalClasses = new ArrayList<Class<?>>();
            for (String pk : packages) {
                tocalClasses.addAll(ClassUtil.getClasses(pk));
            }
            List<Class<?>> classes = new ArrayList<Class<?>>();
            for (Class<?> c : tocalClasses) {
                if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                    System.out.println("handler[ " + c.getName() + " ]");
                    ProtocolHandler instance = (ProtocolHandler) c.newInstance();
                    handlers.put(instance.getCmd(), instance);
                }
            }
            System.out.println("handler size[ " + handlers.size() + " ]");
            for (Class<?> c : classes) {
                System.out.println(c.getName());
            }

        } catch (Exception e) {
            System.out.println("加载protocol handlers 失败:{}"+ e.toString());
        }
        if(handlers.isEmpty()){
            System.out.println("注意，没有加载任何消息处理器");
        }
    }

    public static ProtocolHandler getProtocolHandler(Integer cmd) {
        return handlers.get(cmd);
    }


}
