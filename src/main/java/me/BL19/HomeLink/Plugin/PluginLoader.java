package me.BL19.HomeLink.Plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import me.BL19.HomeLink.HAP.Devices.HAPDevice;

public class PluginLoader {

	public static ArrayList<HLPlugin> loadedPlugins = new ArrayList<HLPlugin>();
	public static PluginLoader instance;
	
	public PluginLoader() {
		instance = this;
	}
	
	public static PluginLoader getInstance() {
		return instance;
	}
	
	/**
	 * Loads a plugin into HomeLink and enables it
	 * @param path
	 * @return TRUE = Worked / Stared, FALSE = FILE NOT FOUND
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public boolean loadPlugin(String path) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		File dir = new File(path);
		if(!dir.exists())
			return false;
		URL loadPath = dir.toURI().toURL();
		URL[] classUrl = new URL[]{loadPath};

		ClassLoader cl = new URLClassLoader(classUrl, PluginLoader.class.getClassLoader());

		Class loadedClass = cl.loadClass("homelink.plugin"); // must be in package.class name format
		HLPlugin pl = (HLPlugin) loadedClass.newInstance();
		pl.init(new HomeLink());
		pl.onEnable();
		loadedPlugins.add(pl);
		return true;
	}
	
	/**
	 * Disables all plugins and unloads them from the memory
	 */
	public void disable() {
		HLPlugin[] plugins = new HLPlugin[loadedPlugins.size()];
		for (int i = 0; i < plugins.length; i++) {
			plugins[i] = loadedPlugins.get(i);
			plugins[i].onDisable();
			plugins[i] = null;
		}
		
		loadedPlugins.clear();
	}
	
	public void broadcast(TriggerElement t, String data) {
		for (HLPlugin hlPlugin : loadedPlugins) {
			hlPlugin.trigger(t, data);
		}
	}
	
	public void sendChange(HAPDevice d) {
		for (HLPlugin hlPlugin : loadedPlugins) {
			hlPlugin.deviceStateChange(d);
		}
	}
	
}
