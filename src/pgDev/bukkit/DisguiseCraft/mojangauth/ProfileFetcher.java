package pgDev.bukkit.DisguiseCraft.mojangauth;

import com.google.common.collect.ImmutableList;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
 
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
 
/**
 * This is evilmidget38's NameFetcher utility modified to fetch GameProfiles
 * 
 * @author evilmidget38, Devil Boy
 *
 */
public class ProfileFetcher implements Callable<Map<UUID, GameProfile>> {
    private static final String PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";
    private final JSONParser jsonParser = new JSONParser();
    private final List<UUID> uuids;
    public ProfileFetcher(List<UUID> uuids) {
        this.uuids = ImmutableList.copyOf(uuids);
    }
 
    @Override
    public Map<UUID, GameProfile> call() throws Exception {
        Map<UUID, GameProfile> uuidProfileMap = new HashMap<UUID, GameProfile>();
        for (UUID uuid: uuids) {
        	// Connect and parse
            HttpURLConnection connection = (HttpURLConnection) new URL(PROFILE_URL + uuid.toString().replace("-", "")).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
            
            // Get the player's name
            String playerName = (String) response.get("name");
            if (playerName == null) {
                continue;
            }
            
            // Check for an error
            String cause = (String) response.get("cause");
            String errorMessage = (String) response.get("errorMessage");
            if (cause != null && cause.length() > 0) {
                throw new IllegalStateException(errorMessage);
            }
            
            // Grab the array of properties
            JSONArray properties = (JSONArray) response.get("properties");
            
            // Store the profile
            GameProfile profile = new GameProfile(uuid, playerName);
            for (Object element : properties) {
            	JSONObject object = (JSONObject) element;
            	Property property = new Property((String) object.get("name"), (String) object.get("value"), (String) object.get("signature"));
            	profile.getProperties().put(property.getName(), property);
            }
            uuidProfileMap.put(uuid, profile);
        }
        return uuidProfileMap;
    }
}