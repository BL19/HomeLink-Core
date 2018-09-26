package me.BL19.HomeLink.HAP.Devices;

import java.util.Collection;

import com.beowulfe.hap.HomekitAccessory;
import com.beowulfe.hap.Service;

public class HAPDevice {

	public HAPDevice(HomekitAccessory acc, HAPDeviceType type) {
		id = acc.getId();
		name = acc.getLabel();
		SRNumber = acc.getSerialNumber();
		model = acc.getModel();
		manufacturer = acc.getManufacturer();
		deviceType = type;
		
		if(type == HAPDeviceType.LIGHT)
			aL = (Light) acc;
	}
	public int id;
	public String name;
	public String SRNumber;
	public String model;
	public String manufacturer;
	public HAPDeviceType deviceType;
	public Light aL;
	

	
}
