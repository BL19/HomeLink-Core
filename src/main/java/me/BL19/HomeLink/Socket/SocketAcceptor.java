package me.BL19.HomeLink.Socket;

import java.io.IOException;
import java.net.ServerSocket;

public class SocketAcceptor implements Runnable {

	private ServerSocket s;

	public SocketAcceptor(ServerSocket s) {
		this.s = s;
	}

	public void run() {
		while(true) {
			Thread t;
			try {
				t = new Thread(new SocketHandler(s.accept()));
				t.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	
}
