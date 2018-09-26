package me.BL19.HomeLink.API.ParsedElements;

import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.HAP.Devices.HAPDeviceType;

public class PDevice {

	public HAPDeviceType type;
	public String manufacturer;
	public String model;
	public String SRNumber;
	public String name;
	public int id;

	public PDevice(HAPDevice d) {
		this.id = d.id;
		this.name = d.name;
		this.SRNumber = d.SRNumber;
		this.model = d.model;
		this.manufacturer = d.manufacturer;
		this.type = d.deviceType;
	}
	
}
