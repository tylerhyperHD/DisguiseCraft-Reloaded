package pgDev.bukkit.DisguiseCraft.update;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;


public class DCUpdateChecker {
	public static String dcInfoQuery = "https://api.curseforge.com/servermods/files?projectIds=37008";
	
	static JSONParser parser = new JSONParser();
	
	public static String getLatestVersion() throws DCUpdateException {
		URLConnection connection = null;
		
		// Connect
		try {
			URL devPage = new URL(dcInfoQuery);
			connection = devPage.openConnection();
			
			// Set user agent
			connection.setRequestProperty("User-Agent", "DisguiseCraft Update Checker");
		} catch (MalformedURLException e) {
			throw new DCUpdateException("Update URL was malformed", e);
		} catch (IOException e) {
			throw new DCUpdateException("Unable to connect to Curse for updates: " + e.getMessage(), e);
		}
		
		// Parse
		try {
			JSONArray fileArray = (JSONArray) parser.parse(new InputStreamReader(connection.getInputStream()));
			JSONObject latestFile = (JSONObject) fileArray.get(fileArray.size() - 1);
			String fileName = (String) latestFile.get("name");
			
			return fileName;
		} catch (Exception e) {
			throw new DCUpdateException("Unable to parse the response from Curse", e);
		}
	}
	
	// Compare version Strings
	public static boolean isUpToDate(String current, String latest) {
		// Strip letters and whitespace
		current = current.replace(" ", "").replaceAll("[^\\d.]", "");
		latest = latest.replace(" ", "").replaceAll("[^\\d.]", "");
		
		// Quick match compare
		if (current.equals(latest)) {
			return true;
		}
		
		// Split into comparable segments
		String[] cSegments = current.split("\\.");
		String[] lSegments = latest.split("\\.");
		
		// Compare the versions
		for (int i=0; i < cSegments.length || i < lSegments.length; i++) {
			if (i >= cSegments.length) {
				if (i >= lSegments.length) {
					return true;
				} else {
					if (Integer.decode(lSegments[i]) > 0) {
						return false;
					} else {
						return true;
					}
				}
			} else if (i >= lSegments.length) {
				return true;
			} else {
				if (Integer.decode(cSegments[i]) > Integer.decode(lSegments[i])) {
					return true;
				} else if (Integer.decode(lSegments[i]) > Integer.decode(cSegments[i])) {
					return false;
				}
			}
		}
		
		throw new NumberFormatException("Version parse loop failed");
	}
}
