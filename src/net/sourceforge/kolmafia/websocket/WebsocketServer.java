package net.sourceforge.kolmafia.websocket;

import java.util.TreeMap;
import org.glassfish.tyrus.server.Server;

public class WebsocketServer implements Runnable {

  public static final WebsocketServer INSTANCE = new WebsocketServer();

  private Thread thread = null;

  protected WebsocketServer() {}

  @Override
  public void run() {
    // TODO: find port
    Server server = new Server("localhost", 60079, "", new TreeMap<>(), WebsocketConfig.class);

    try {
      server.start();
      System.out.println("WebSocket server started at ws://localhost:60079/");
    } catch (Exception e) {
      e.printStackTrace();
      server.stop();
    }
  }

  public synchronized void startServer() {
    if (thread == null) {
      thread = new Thread(INSTANCE, "LocalWebsocketServer");
      thread.start();
    }
  }

  public synchronized boolean isRunning() {
    return thread != null && thread.isAlive();
  }
}
