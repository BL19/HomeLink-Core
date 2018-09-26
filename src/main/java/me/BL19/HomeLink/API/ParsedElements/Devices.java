package me.BL19.HomeLink.API.ParsedElements;

import me.BL19.HomeLink.HAP.Devices.HAPDevice;

public class Devices {

	public PDevice[] devices;
	
	public Devices(HAPDevice[] devs) {
		devices = new PDevice[devs.length];
		for (int i = 0; i < devs.length; i++) {
			devices[i] = new PDevice(devs[i]);
		}
	}
	
}
