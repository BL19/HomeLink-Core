package me.BL19.HomeLink;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.bouncycastle.util.encoders.Base64;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitAuthInfo;
import com.beowulfe.hap.HomekitRoot;
import com.beowulfe.hap.HomekitServer;

import me.BL19.HomeLink.HAP.HAPAuth;
import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.HAP.Devices.HAPDeviceType;
import me.BL19.HomeLink.HAP.Devices.Light;
import me.BL19.HomeLink.Socket.SocketServer;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class HL {
	public static Properties c;

	// HAP
	public static HomekitServer hap_hks;
	public static HomekitRoot hap_bridge;
	public static HashMap<String, byte[]> userKeys = new HashMap<String, byte[]>();
	public static ArrayList<HAPDevice> hap_devices = new ArrayList<HAPDevice>();
	public static SocketServer api_ss;

	public static void main(String[] args) {
		c = new Properties();
		try {
			c.setProperty("HAP.key", Base64.toBase64String(HomekitServer.generateKey()));
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
		c.setProperty("HAP.salt", HomekitServer.generateSalt() + "");
		c.setProperty("HAP.mac", HomekitServer.generateMac());
		c.setProperty("HAP.pin",
				(new SecureRandom().nextInt(100000000) + "").replaceFirst("(\\w{3})(\\w{2})(\\w{3})", "$1-$2-$3"));
		c.setProperty("HAP.name", "HomeLink");
		c.setProperty("HAP.port", "9841");
		c.setProperty("HAP.users", "");
		
		c.setProperty("API.port", "9840");
		if (!new File("config.xml").exists()) {
			try {
				new File("config.xml").createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				c.loadFromXML(new FileInputStream("config.xml"));
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (c.getProperty("HAP.users") != "") {
			String[] HAP_USERS = c.getProperty("HAP.users").split(":!!:");
			for (String string : HAP_USERS) {
				String[] usrName = string.split(":!:");
				if (!(usrName.length < 2))
					userKeys.put(usrName[0], Base64.decode(usrName[1]));
			}
		}
		launchHAP();
		launchAPI();
		saveConfig();
	}

	private static void launchAPI() {
		System.out.println("Starting api server...");
		api_ss = new SocketServer(Integer.parseInt(get("API.port")));
	}

	public static void saveConfig() {
		String uSt = "";
		for (String usrN : userKeys.keySet()) {
			uSt += usrN + ":!:" + Base64.toBase64String(userKeys.get(usrN)) + ":!!:";
		}

		if (uSt.length() != 0) {
			uSt = uSt.substring(0, uSt.length() - 4);
		}
		c.setProperty("HAP.users", uSt);

		try {
			c.storeToXML(new FileOutputStream("config.xml"), "HomeLink Config");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String p) {
		return c.getProperty(p);
	}

	public static int getHighestID() {
		int h = 1;
		for (HAPDevice device : hap_devices) {
			if (device.id > h)
				h = device.id;
		}
		return h;
	}

	public static void launchHAP() {
		try {
			hap_hks = new HomekitServer(Integer.parseInt(get("HAP.port")));
			hap_bridge = hap_hks.createBridge(new HAPAuth(), get("HAP.name"), "BL19", "HomeLink", "7731jfa841");
			//addDeviceToHAP(new Light("Base1", 2));
			initStaticDevices();
			//hap_bridge.allowUnauthenticatedRequests(true);
			hap_bridge.start();
			System.out.println("Connect to: \"" + get("HAP.name") + "\" using code \"" + get("HAP.pin") + "\"");
			System.out.println(uri());
			File f = QRCode.from(uri()).to(ImageType.JPG).withSize(400, 400).file();
			System.out.println(f.getAbsolutePath());
			//Thread.sleep(10000);
			//addDeviceToHAP(new Light("Base2", getHighestID() + 1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initStaticDevices() {
		try {
			if(!new File("staticdevices").exists())
				new File("staticdevices").createNewFile();
			BufferedReader r = new BufferedReader(new FileReader("staticdevices"));
			String line = "";
			try {
				while((line = r.readLine()) != null || line != "") {
					System.out.println(line);
					if(line == null)
						break;
					String[] da = line.split(":");
					if(da[0].equals("light")) {
						Light l = new Light(da[1], getHighestID() + 1);
						addDeviceToHAP(l, HAPDeviceType.LIGHT, false);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			r.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addDeviceToHAP(HomekitAccessory acc, HAPDeviceType type) {
		addDeviceToHAP(acc, type, true);
	}

	public static void addDeviceToHAP(HomekitAccessory acc, HAPDeviceType type, boolean initzialied) {
		hap_devices.add(new HAPDevice(acc, type)); // Parse the device to a HAPDevice so that it can be handled
		hap_bridge.addAccessory(acc);
		if (initzialied) {
			try {
				hap_bridge.setConfigurationIndex(2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String uri() {
		int payload = 2 << 31;
		payload |= 1 << 28;
		payload |= Integer.parseInt(get("HAP.pin").replace("-", ""));
		byte[] bytes = (payload + "").getBytes(StandardCharsets.UTF_8);
		String base36 = new BigInteger(1, bytes).toString(36);
		return "X-HM://00" + base36.toUpperCase() + "ABCD";
	}

}
