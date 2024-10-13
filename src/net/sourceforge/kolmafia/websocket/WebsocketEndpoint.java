package net.sourceforge.kolmafia.websocket;

import com.alibaba.fastjson2.JSON;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import net.sourceforge.kolmafia.listener.CharacterListener;
import net.sourceforge.kolmafia.listener.CharacterListenerRegistry;

@ServerEndpoint("/ws")
public class WebsocketEndpoint {

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("Client connected: " + session.getId());
    sendMessage(session, new CharacterSheetMessage());

    CharacterListenerRegistry.addCharacterListener(
        new CharacterListener(() -> sendMessage(session, new CharacterSheetMessage())));
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    System.out.println("Message received from client: " + message);
  }

  @OnClose
  public void onClose(Session session) {
    System.out.println("Client disconnected: " + session.getId());
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    System.out.println("Error occurred for client: " + session.getId());
    throwable.printStackTrace();
  }

  // Generic method to send any message that implements the Message interface
  private void sendMessage(Session session, WebsocketMessage message) {
    try {
      String jsonMessage = JSON.toJSONString(message);
      session.getBasicRemote().sendText(jsonMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
