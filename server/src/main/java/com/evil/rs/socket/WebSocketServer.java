package com.evil.rs.socket;

import cn.dev33.satoken.stp.StpUtil;
//import com.evil.rs.entity.NotifyMessage;
//import com.evil.rs.enums.CommonStringEnums;
//import com.evil.rs.model.AuthUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket逻辑控制
 */
@Component
@ServerEndpoint("/controller/{token}")
@Slf4j
public class WebSocketServer {
    public static ConcurrentHashMap<String,Session> webSocketMap = new ConcurrentHashMap<>();
    /**与某个控制端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收username*/
    private String id = "";

    /**
     * 连接建立成功调用的方法
     * @param token 授权码
     * */
    @OnOpen
    public void onOpen(Session session,
                       @PathParam("token") String token){
        this.session = session;
        this.id = (String) StpUtil.getLoginIdByToken(token);
        webSocketMap.put(id, session);
//        new NotifyMessage(CommonStringEnums.NOTIFY_TEAM_JOIN.getDescription(), id);
//        String name = id.substring(id.indexOf("=") + 1, id.indexOf(","));
        log.info("[+]用户[{}]加入了团队聊天室", id);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketMap.remove(this.id);
        log.info("[+]用户[{}]退出了团队聊天室", id);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param commandJson 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String commandJson, Session session) throws IOException {

    }

    public static void sendResultToClient(String target, String data) throws IOException {
        Session session = webSocketMap.get(target);
        if(session != null){
            log.info("[+]回返给用户[{}]内容：\n{}", target, data);
            session.getBasicRemote().sendText(data);
        }
    }

    // 通知所有人
    public static void notifyAllUser(String msg){
        Iterator<Session> onlineUser = webSocketMap.elements().asIterator();
        while (onlineUser.hasNext()){
            try {
                Session session = onlineUser.next();
                session.getBasicRemote().sendText(msg);
            } catch (IOException e) {
                log.info("通知消息发送出现错误", e);
            }
        }
    }

    // 通知消息到目标
    public static void notifyReceiver(String receiver, String msg){
        if (receiver == null){
            return;
        }
        String receiverId = (String) StpUtil.getLoginIdByToken(receiver);
        if (receiverId == null){
            return;
        }
        Session receiverSession = webSocketMap.get(receiverId);
        try{
            if (receiverSession != null){
                receiverSession.getBasicRemote().sendText(msg);
            }
        }catch (Exception e){
            log.info("通知消息目标token[{}]不存在", receiver);
        }
    }
}
