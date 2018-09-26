package me.BL19.HomeLink.API;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import com.google.gson.Gson;

import me.BL19.HomeLink.HAP.Devices.HAPDevice;

public class HAPConnection {

	PrintWriter out = null;
	BufferedReader in = null;
	
	/**
	 * Initsitates a connection to the HAP Server to send / recive data.
	 * @param ip The ip of the HAP Server
	 * @param port The port of the HAP Server
	 */
	public HAPConnection(String ip, int port) {
		Socket s = null;
		try {
			s = new Socket(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(s != null)
		while(!s.isConnected()) {
		}
		

		try {
			out =new PrintWriter(s.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if(out == null || in == null) {
			System.err.println("[" + s.getInetAddress() + "] Failed to init in/out streams");
			throw new RuntimeException("Stream nullexception");
		}
	}
	
	public String sendCommand(String command) {
		out.println(command);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			return in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HAPDevice[] GetDevices() {
		return new Gson().fromJson(sendCommand("get:hap:devices"), HAPDevice[].class);
	}
	
	public boolean SwitchLight(String id, boolean state) {
		return (sendCommand("set:hap:device:" + id + ":state:" + state) == "true");
	}
	
	public boolean GetState(String id) {
		return (sendCommand("get:hap:device:" + id + ":state") == "true");
	}
	
}
