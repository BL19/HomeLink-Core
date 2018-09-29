package me.BL19.HomeLink.Socket;

import java.io.IOException;
import java.net.ServerSocket;

import me.BL19.API.Logging.Logger;

public class SocketServer {

	public ServerSocket s;
	
	public SocketServer(int port) {
		try {
			s = new ServerSocket(port);
			Thread t = new Thread(new SocketAcceptor(s));
			t.start();
			
			new Logger(SocketServer.class).info("API-Server is running on port " + port);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
