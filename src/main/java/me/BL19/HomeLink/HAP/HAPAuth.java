package me.BL19.HomeLink.HAP;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Base64;

import com.beowulfe.hap.HomekitAuthInfo;

import me.BL19.HomeLink.HL;

public class HAPAuth implements HomekitAuthInfo {

		
		public void removeUser(String username) {
			HL.userKeys.remove(username);
			System.out.println("Removed user " + username);
			HL.saveConfig();
		}
		
		public byte[] getUserPublicKey(String username) {
			System.out.println("Returning key for " + username);
			return HL.userKeys.get(username);
		}
		
		public BigInteger getSalt() {
			System.out.println("SALT:" + HL.get("HAP.salt"));
			return new BigInteger(HL.get("HAP.salt"));
		}
		
		public byte[] getPrivateKey() {
			System.out.println("KEY: " + HL.get("HAP.key"));
			return Base64.decode(HL.get("HAP.key"));
		}
		
		public String getPin() {
			System.out.println("PIN: " + HL.get("HAP.pin"));
			return HL.get("HAP.pin");
		}
		
		public String getMac() {
			System.out.println("MAC: " + HL.get("HAP.mac"));
			return HL.get("HAP.mac");
		}
		
		public void createUser(String username, byte[] publicKey) {
			HL.userKeys.put(username, publicKey);
			System.out.println("Created user " + username);
			HL.saveConfig();
		}
	
}
