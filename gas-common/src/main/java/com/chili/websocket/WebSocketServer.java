package com.chili.websocket;

import com.chili.exception.BaseException;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket服务
 */
@Component
@ServerEndpoint("/ws/{role}/{sid}")
public class WebSocketServer {

    // 存放用户端的会话，键为用户的ID
    private static Map<Integer, Session> userSessions = new ConcurrentHashMap<>();

    // 存放管理端的会话，键为管理员的ID
    private static Map<Integer, Session> adminSessions = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("role") String role, @PathParam("sid") Integer sid) {
        System.out.println("客户端：" + sid + "建立连接，角色：" + role);
        if ("user".equals(role)) {
            userSessions.put(sid, session);  // 将用户端会话存储到 userSessions
        } else if ("admin".equals(role)) {
            adminSessions.put(sid, session); // 将管理端会话存储到 adminSessions
        }
    }

    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, @PathParam("sid") Integer sid) {
        System.out.println("收到来自客户端：" + sid + " 的信息: " + message);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("role") String role, @PathParam("sid") Integer sid) {
        System.out.println("连接断开: " + sid + "，角色：" + role);
        if ("user".equals(role)) {
            userSessions.remove(sid);  // 从用户端会话中移除
        } else if ("admin".equals(role)) {
            adminSessions.remove(sid); // 从管理端会话中移除
        }
    }

    /**
     * 向特定用户发送消息
     * @param sid 用户的ID
     * @param message 要发送的消息
     */
    public void sendMessageToUser(Integer sid, String message) {
        Session session = userSessions.get(sid);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                System.out.println("向用户 " + sid + " 发送消息: " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new BaseException("当前用户未连接");
        }
    }

    /**
     * 向所有用户广播消息
     * @param message 要发送的消息
     */
    public void sendToAllUsers(String message) {
        Collection<Session> sessions = userSessions.values();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                    System.out.println("向用户广播消息: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 向所有管理端广播消息
     * @param message 要发送的消息
     */
    public void sendToAllAdmins(String message) {
        Collection<Session> sessions = adminSessions.values();
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                    System.out.println("向管理端广播消息: " + message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
