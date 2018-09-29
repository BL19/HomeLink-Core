package me.BL19.HomeLink.Plugin;

import me.BL19.HomeLink.HAP.Devices.HAPDevice;

public class HLPlugin {

	public HomeLink homeLink;
	public String pluginName;
	public String neededPlugins;
	
	public HLPlugin() {

	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		homeLink = null;
	}
	
	public void trigger(TriggerElement te, String data) {
		
	}

	protected void init(HomeLink homeLink2) {
		this.homeLink = homeLink2;
	}
	
	public void deviceStateChange(HAPDevice d) {
		
	}
	
}
