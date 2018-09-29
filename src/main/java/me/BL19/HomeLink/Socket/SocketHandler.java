package me.BL19.HomeLink.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import me.BL19.HomeLink.HL;
import me.BL19.HomeLink.API.ParsedElements.Devices;
import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.HAP.Devices.Light;

public class SocketHandler implements Runnable {

	private Socket s;

	public SocketHandler(Socket s) {
		this.s = s;
	}

	public void run() {
		PrintWriter out = null;
		BufferedReader in = null;
		try {
			out = new PrintWriter(s.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (out == null || in == null) {
			System.err.println("[" + s.getInetAddress() + "] Failed to init in/out streams");
			throw new RuntimeException("Stream nullexception");
		}

		while (true) {
			try {
				if (in.ready()) {
					String call = in.readLine();
					
					System.out.println(call);
					
					// Peform command
					String[] args = call.split(":");
					if (args[0].equals("get")) {
						if (args[1].equals("hap")) {
							if (args[2].equals("devices")) {
								Object[] objs = HL.hap_devices.toArray();
								HAPDevice[] devs = new HAPDevice[objs.length];
								for (int i = 0; i < devs.length; i++) {
									devs[i] = (HAPDevice) objs[i];
								}
								out.println(new Gson().toJson(new Devices(devs)));
							}else if(args[2].equals("device")) {
								if(args[3].equals("state")) {
									HAPDevice d = getDevice(args[4]);
									if (d == null) {
										out.println("[HAP-NOT-FOUND]");
									} else {
										out.println(((Light)d.device).getLightbulbPowerState().join());
									}
								}
							} 
						}
					} else if (args[0].equals("set")) {
						if (args[1].equals("hap")) {
							if (args[2].equals("device")) {
								if (args[4].equals("state")) {
									int deviceID = Integer.parseInt(args[3]);
									HAPDevice device = null;
									Object[] objs = HL.hap_devices.toArray();
									HAPDevice[] devs = new HAPDevice[objs.length];
									for (int i = 0; i < devs.length; i++) {
										devs[i] = (HAPDevice) objs[i];
									}
									for (int i = 0; i < devs.length; i++) {
										if (devs[i].id == deviceID) {
											((Light)devs[i].device).setLightbulbPowerState((args[5].equals("true")));
											out.println("true");
											break;
										}
									}
								}
							}
						}
					} else if (args[0].equals("plugin")) {

					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public HAPDevice getDevice(String did) {
		int deviceID = Integer.parseInt(did);
		HAPDevice device = null;
		for (HAPDevice d : HL.hap_devices) {
			if (d.id == deviceID) {
				device = d;
				break;
			}
		}
		return device;
	}

}
