package me.BL19.HomeLink.Plugin;

public class HLPlugin {

	private HomeLink homeLink;
	public String pluginName;
	public String neededPlugins;
	
	public HLPlugin(HomeLink hl) {
		homeLink = hl;
		onEnable();
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		homeLink = null;
	}
	
	public void trigger(TriggerElement te, String data) {
		
	}
	
}
