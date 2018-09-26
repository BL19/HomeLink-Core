package me.BL19.HomeLink.Plugin;

import java.util.ArrayList;
import java.util.Properties;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.HomekitRoot;

import me.BL19.HomeLink.HL;
import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.HAP.Devices.HAPDeviceType;
import me.BL19.HomeLink.Socket.SocketServer;

public class HomeLink {

	public SocketServer getAPIServer() {
		return HL.api_ss;
	}
	
	public HomekitRoot getHAPBridge() {
		return HL.hap_bridge;
	}
	
	public ArrayList<HAPDevice> getHAPDevices() {
		return HL.hap_devices;
	}
	
	public Properties getConfig() {
		return HL.c;
	}
	
	public void addDeviceToHAP(HomekitAccessory dev, HAPDeviceType type) {
		HL.addDeviceToHAP(dev, type);
	}
	
	public String getHAPUri() {
		return HL.uri();
	}
	
}
