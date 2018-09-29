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
import java.net.MalformedURLException;
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
import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.HomekitRoot;
import com.beowulfe.hap.HomekitServer;

import me.BL19.API.Logging.Logger;
import me.BL19.HomeLink.HAP.HAPAuth;
import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.HAP.Devices.HAPDeviceType;
import me.BL19.HomeLink.HAP.Devices.Light;
import me.BL19.HomeLink.Plugin.PluginLoader;
import me.BL19.HomeLink.Plugin.TriggerElement;
import me.BL19.HomeLink.Socket.SocketServer;
import net.glxn.qrgen.core.image.ImageType;
import net.glxn.qrgen.javase.QRCode;

public class HL {
	public static Properties c;

	// Logging
	static Logger l = new Logger(HL.class);

	// HAP
	public static HomekitServer hap_hks;
	public static HomekitRoot hap_bridge;
	public static HashMap<String, byte[]> userKeys = new HashMap<String, byte[]>();
	public static ArrayList<HAPDevice> hap_devices = new ArrayList<HAPDevice>();
	private static HomekitCharacteristicChangeCallback hap_callback;

	// API
	public static SocketServer api_ss;

	public static void main(String[] args) {
		new PluginLoader();
		Logger.setMainLoggerName("HomeLink");
		Logger.setPrinting(true, true);
		if (!new File("plugins").exists())
			new File("plugins").mkdir();

		l.info("Loading default settings");
		loadConfig();
		l.info("Config has been loaded");
		launchHAP();
		launchAPI();
		loadPlugins();
		saveConfig();
		PluginLoader.getInstance().broadcast(TriggerElement.HOMELINK, "LAUNCHED");
		PluginLoader.getInstance().broadcast(TriggerElement.HAP, hap_devices.size() + " devices loaded");
	}

	// Plugins
	private static void loadPlugins() {
		for (File plugin : new File("plugins").listFiles()) {
			if (!plugin.getName().equals("config")) {
				try {
					PluginLoader.getInstance().loadPlugin(plugin.getAbsolutePath());
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// API
	private static void launchAPI() {
		l.info("Launching api server");
		api_ss = new SocketServer(Integer.parseInt(get("API.port")));
	}

	// Config
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

	public static void loadConfig() {
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
	}

	public static String get(String p) {
		return c.getProperty(p);
	}

	// HAP
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
			l.info("Launching HAP Bridge");
			hap_hks = new HomekitServer(Integer.parseInt(get("HAP.port")));
			hap_bridge = hap_hks.createBridge(new HAPAuth(), get("HAP.name"), "BL19", "HomeLink", "7731jfa841");
			initStaticDevices();
			hap_bridge.start();
			l.info("Connect to: \"" + get("HAP.name") + "\" using code \"" + get("HAP.pin") + "\"");
			// System.out.println(uri());
			File f = QRCode.from(uri()).to(ImageType.JPG).withSize(400, 400).file();
			l.info(f.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		l.info("HAP Bridge has been initiated");
	}

	private static void initStaticDevices() {
		l.info("Loading staticdevices");
		try {
			if (!new File("staticdevices").exists())
				new File("staticdevices").createNewFile();
			BufferedReader r = new BufferedReader(new FileReader("staticdevices"));
			String line = "";
			try {
				while ((line = r.readLine()) != null || line != "") {
					System.out.println(line);
					if (line == null)
						break;
					String[] da = line.split(":");
					l.info("Loading device " + da[1] + ", type:" + da[0]);
					if (da[0].equals("light")) {
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
		l.info("StaticDevices have been loaded!");
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
