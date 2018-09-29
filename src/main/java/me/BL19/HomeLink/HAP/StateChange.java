package me.BL19.HomeLink.HAP;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;

import me.BL19.HomeLink.HL;
import me.BL19.HomeLink.HAP.Devices.HAPDevice;
import me.BL19.HomeLink.Plugin.PluginLoader;

public class StateChange implements HomekitCharacteristicChangeCallback {

	private int id;

	public StateChange(int id) {
		this.id = id;
	}
	
	public void changed() {
		Object[] objs = HL.hap_devices.toArray();
		HAPDevice[] devs = new HAPDevice[objs.length];
		HAPDevice dev = null;
		for (int i = 0; i < devs.length; i++) {
			devs[i] = (HAPDevice) objs[i];
		}
		for (int i = 0; i < devs.length; i++) {
			if (devs[i].id == id) {
				dev = devs[i];
				break;
			}
		}
		// Send update to plugins
		PluginLoader.getInstance().sendChange(dev);
	}

}
