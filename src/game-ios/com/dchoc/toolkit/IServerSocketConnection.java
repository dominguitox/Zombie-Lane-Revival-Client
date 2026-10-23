package com.dchoc.toolkit;

public interface IServerSocketConnection {
   int CONNECTION_STATE_CONNECTED = 1;
   int CONNECTION_STATE_CONNECTING = 0;
   int CONNECTION_STATE_DISCONNECTED = -1;

   void connect(String var1, int var2);

   void disconnect();

   int getConnectionState();

   ServerSocketMessage getMessage();

   boolean isMessageAvailable();

   boolean sendMessage(ServerSocketMessage var1);
}
