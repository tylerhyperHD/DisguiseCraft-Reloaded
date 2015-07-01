package pgDev.bukkit.DisguiseCraft.mojangauth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EmptyUUIDS {
	static final String LOOKUP_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
	
	Queue<UUID> cache;
	
	public EmptyUUIDS() {
		cache = new ConcurrentLinkedQueue<UUID>();
	}
	
	 UUID findEmptyUUID() throws Exception {
		UUID output = null;
		do {
			output = UUID.randomUUID();
		} while (!checkIfEmpty(output));
		return output;
	}
	
	boolean checkIfEmpty(UUID uid) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) new URL(LOOKUP_URL + uid.toString().replace("-", "")).openConnection();
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		boolean result = br.readLine() == null;
		
		br.close();
		return result;
	}
}
