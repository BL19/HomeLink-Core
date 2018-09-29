package me.BL19.HomeLink.HAP;

import java.math.BigInteger;

import org.bouncycastle.util.encoders.Base64;

import com.beowulfe.hap.HomekitAuthInfo;

import me.BL19.API.Logging.Logger;
import me.BL19.HomeLink.HL;

public class HAPAuth implements HomekitAuthInfo {

		static Logger l = new Logger(HAPAuth.class);
		
		public void removeUser(String username) {
			HL.userKeys.remove(username);
			l.info("Removed user " + username);
			HL.saveConfig();
		}
		
		public byte[] getUserPublicKey(String username) {
			l.info("Returning key for " + username);
			return HL.userKeys.get(username);
		}
		
		public BigInteger getSalt() {
			l.info("SALT:" + HL.get("HAP.salt"));
			return new BigInteger(HL.get("HAP.salt"));
		}
		
		public byte[] getPrivateKey() {
			l.info("KEY: " + HL.get("HAP.key"));
			return Base64.decode(HL.get("HAP.key"));
		}
		
		public String getPin() {
			l.info("PIN: " + HL.get("HAP.pin"));
			return HL.get("HAP.pin");
		}
		
		public String getMac() {
			l.info("MAC: " + HL.get("HAP.mac"));
			return HL.get("HAP.mac");
		}
		
		public void createUser(String username, byte[] publicKey) {
			HL.userKeys.put(username, publicKey);
			l.info("Created user " + username);
			HL.saveConfig();
		}
	
}
