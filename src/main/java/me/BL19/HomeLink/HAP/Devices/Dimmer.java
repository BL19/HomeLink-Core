package me.BL19.HomeLink.HAP.Devices;

import java.util.concurrent.CompletableFuture;

import com.beowulfe.hap.HomekitCharacteristicChangeCallback;
import com.beowulfe.hap.accessories.DimmableLightbulb;

import me.BL19.HomeLink.HAP.StateChange;

public class Dimmer implements DimmableLightbulb{

	private boolean powerState = false;
	private HomekitCharacteristicChangeCallback subscribeCallback = null;
	private HomekitCharacteristicChangeCallback DsubscribeCallback = null;
	private int dim = 0;
	
	public int id;
	public String name;
	public String SRNumber;
	public String model;
	public String manufacturer;
	
	public Dimmer(String name, int id) {
		this.name = name;
		this.id = id;
		subscribeCallback = new StateChange(id);
		DsubscribeCallback = new StateChange(id);
	}
	
	public CompletableFuture<Boolean> getLightbulbPowerState() {
		return CompletableFuture.completedFuture(powerState);
	}

	public CompletableFuture<Void> setLightbulbPowerState(boolean powerState)
			throws Exception {
		this.powerState = powerState;
		if (subscribeCallback != null) {
			subscribeCallback.changed();
		}
		return CompletableFuture.completedFuture(null);
	}

	public void subscribeLightbulbPowerState(
			HomekitCharacteristicChangeCallback callback) {
		this.subscribeCallback = callback;
	}

	public void unsubscribeLightbulbPowerState() {
		this.subscribeCallback = null;
	}

	public int getId() {
		return id;
	}

	public String getLabel() {
		// TODO Auto-generated method stub
		return name;
	}

	public void identify() {
	}

	public String getSerialNumber() {
		return SRNumber;
	}

	public String getModel() {
		return null;
	}

	public String getManufacturer() {
		return null;
	}
	
	public CompletableFuture<Integer> getBrightness() {
		return CompletableFuture.completedFuture(dim);
	}

	public CompletableFuture<Void> setBrightness(Integer value)
			throws Exception {
		this.dim = value;
		if (DsubscribeCallback != null) {
			DsubscribeCallback.changed();
		}
		return CompletableFuture.completedFuture(null);
	}

	public void subscribeBrightness(HomekitCharacteristicChangeCallback callback) {
		DsubscribeCallback = callback;
	}

	public void unsubscribeBrightness() {
		DsubscribeCallback = null;
	}

}
