package pgDev.bukkit.DisguiseCraft.disguise;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.DataWatcher.WatchableObject;
import net.minecraft.server.v1_8_R3.World;

import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Animals;

import pgDev.bukkit.DisguiseCraft.*;

/**
 * This is the list of possible disguises listed by
 * their Bukkit class name.
 * @author PG Dev Team (Devil Boy)
 */
public enum DisguiseType {
	//Player
	Player(0),
	
	// Mobs
	Bat(65),
	Blaze(61),
	CaveSpider(59),
	Chicken(93),
	Cow(92),
	Creeper(50),
	EnderDragon(63),
	Enderman(58),
	Endermite(67),
	Ghast(56),
	Giant(53),
	Guardian(68),
	Horse(100),
	IronGolem(99),
	MagmaCube(62),
	MushroomCow(96),
	Ocelot(98),
	Pig(90),
	PigZombie(57),
	Rabbit(101),
	Sheep(91),
	Silverfish(60),
	Skeleton(51),
	Slime(55),
	Snowman(97),
	Spider(52),
	Squid(94),
	Villager(120),
	Witch(66),
	Wither(64),
	Wolf(95),
	Zombie(54),
	
	// Vehicles
	Boat(1),
	Minecart(10),
	//PoweredMinecart(12),
	//StorageMinecart(11),
	
	//Blocks
	EnderCrystal(51),
	FallingBlock(70),
	TNTPrimed(50),
	ArmorStand(30);
	
	/**
	 * Entities that are listed in the DisguiseCraft database, but not in
	 * the current Minecraft server version
	 */
	public static LinkedList<DisguiseType> missingDisguises = new LinkedList<DisguiseType>();
	protected static HashMap<Byte, DataWatcher> modelData = new HashMap<Byte, DataWatcher>();
	
	public static Field mapField;
	public static List<Field> boolFields;
	
	public static void getDataWatchers(org.bukkit.World world) {
		// Get model datawatchers
    	try {
    		Field watcherField = Entity.class.getDeclaredField("datawatcher");
    		watcherField.setAccessible(true);
    		
			for (DisguiseType m : values()) {
				if (m.isMob()) {
					String mobClass = DynamicClassFunctions.nmsPackage + ".Entity" + m.name();
					if (m == DisguiseType.Giant) {
	    				mobClass = mobClass + "Zombie";
	    			}

	        		try {
	        			Object ent = Class.forName(mobClass).getConstructor(World.class).newInstance(((CraftWorld) world).getHandle());
	        			modelData.put(m.id, (DataWatcher) watcherField.get(ent));
	        		} catch (Exception e) {
	        			missingDisguises.add(m);
	        		}
				}
        	}
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not access datawatchers!");
		}
    	
    	// Begin: Store important fields
    	mapField = null;
    	boolFields = new LinkedList<Field>();
    	
    	// Search for the fields (Use Spigot's backward compatibility)
    	for (Field f : DataWatcher.class.getDeclaredFields()) {
    		if (f.getType() == Map.class && mapField == null) {
    			if (!Modifier.isStatic(f.getModifiers())) {
    				f.setAccessible(true);
    				mapField = f;
    			}
    		} else if (f.getType() == boolean.class) {
    			if (!Modifier.isStatic(f.getModifiers())) {
    				f.setAccessible(true);
    				boolFields.add(f);
    			}
    		}
    	}
    	
    	if (mapField == null) {
    		DisguiseCraft.logger.log(Level.SEVERE, "Could not find the DataWatcher Map");
    	}
    	if (boolFields.isEmpty()) {
    		DisguiseCraft.logger.log(Level.SEVERE, "Could not find any DataWatcher booleans!");
    	}
	}
	
	/**
	 * The entity-type ID.
	 */
	public final byte id;
	
	DisguiseType(int i) {
		id = (byte) i;
	}
	
	/**
	 * Check if the mob type is a subclass of an Entity class from Bukkit.
	 * This is extremely useful to seeing if a mob can have a certain
	 * subtype. For example: only members of the Animal class (and villagers)
	 * can have a baby form.
	 * @param cls The class to compare to
	 * @return true if the disguisetype is a subclass, false otherwise
	 */
	public boolean isSubclass(Class<?> cls) {
		try {
			return cls.isAssignableFrom(Class.forName("org.bukkit.entity." + name()));
		} catch (ClassNotFoundException e) {
			DisguiseCraft.logger.log(Level.WARNING, "Can't find entity class for the DisguiseType: " + name(), e);
		}
		return false;
	}
	
	/**
	 * Check if this is a humanoid.
	 * @return true if the type is of a humanoid (player, skeleton, zombie, pigzombie), false otherwise
	 */
	public boolean isHumanoid() {
		return isPlayer() || this == Skeleton || this == Zombie || this == PigZombie || this==ArmorStand;
	}
	
	/**
	 * Check if this is a player.
	 * @return true if the type is of a player, false otherwise
	 */
	public boolean isPlayer() {
		return this == Player;
	}
	
	/**
	 * Check if this is a mob.
	 * @return true if the type is of a mob, false otherwise
	 */
	public boolean isMob() {
		//return this != Player && isSubclass(LivingEntity.class);
		return !isPlayer() && !isObject();
	}
	
	/**
	 * Check if this is an object.
	 * @return true if the type is of an object, false otherwise
	 */
	public boolean isObject() {
		return isVehicle() || isBlock();
	}
	
	/**
	 * Check if this is a vehicle.
	 * @return true if the type is of a vehicle, false otherwise
	 */
	public boolean isVehicle() {
		//return this.isSubclass(Vehicle.class) && this != Pig;
		return this == Boat || this == Minecart;
	}
	
	/**
	 * Check if this is a block.
	 * @return true if the type is of a block, false otherwise
	 */
	public boolean isBlock() {
		return 	this == EnderCrystal || this == FallingBlock || this == TNTPrimed;
	}
	
	/**
	 * Checks if this disguise has a baby form.
	 * @return true if it can be a baby, false otherwise
	 */
	public boolean canBeBaby() {
		return isSubclass(Animals.class) || this == Villager
				|| this == Zombie || this == PigZombie;
	}
	
	/**
	 * Get the DisguiseType from its name
	 * Works like valueOf, but not case sensitive
	 * @param text The string to match with a DisguiseType
	 * @return The DisguiseType with the given name (null if none are found)
	 */
	public static DisguiseType fromString(String text) {
		for (DisguiseType m : DisguiseType.values()) {
			if (text.equalsIgnoreCase(m.name())) {
				if (missingDisguises.contains(m)) {
					return null;
				} else {
					return m;
				}
			}
		}
		return null;
	}
	
	public DataWatcher newMetadata() {
		if (modelData.containsKey(id)) {
			return copyDataWatcher(modelData.get(id));
		} else {
			try {
				return new DCDataWatcher();
			} catch (Exception e) {
				DisguiseCraft.logger.log(Level.SEVERE, "Could not construct a new DataWatcher", e);
				return null;
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static DataWatcher copyDataWatcher(DataWatcher dw) {
		DataWatcher w = new DCDataWatcher();
		
		// Clone Map
		try {
			Map<Integer, WatchableObject> modelMap = ((Map<Integer, WatchableObject>) mapField.get(dw));
			Map<Integer, WatchableObject> newMap = ((Map<Integer, WatchableObject>) mapField.get(w));
			for (Integer index : modelMap.keySet()) {
				newMap.put(index, copyWatchable(modelMap.get(index)));
			}
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not clone map in a datawatcher!", e);
		}
		
		// Clone boolean
		try {
			for (Field boolField : boolFields) {
				boolField.setBoolean(w, boolField.getBoolean(dw));
			}
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not clone boolean in a datawatcher!", e);
		}
		
		return w;
	}
	
	private static WatchableObject copyWatchable(WatchableObject watchable) {
		try {
			return new WatchableObject(watchable.c(), watchable.a(), watchable.b());
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not copy a WatchableObject", e);
			return null;
		}
	}
	
	/**
	 * Returns the type ID of a minecart type
	 * @param type The type of minecart (chest, furnace, hopper, etc.)
	 * @return The code-side ID or -1 if String not identified
	 */
	public static int getMinecartTypeID(String type) {
		int output = -1;
		if (type.equalsIgnoreCase("chest") || type.equalsIgnoreCase("storage")) {
			output = 1;
		} else if (type.equalsIgnoreCase("furnace") || type.equalsIgnoreCase("powered")) {
			output = 2;
		} else if (type.equalsIgnoreCase("tnt")) {
			output = 3;
		} else if (type.equalsIgnoreCase("mobspawner") || type.equalsIgnoreCase("spawner")) {
			output = 4;
		} else if (type.equalsIgnoreCase("hopper")) {
			output = 5;
		}
		return output;
	}
	
	/**
	 * Just a string containing the possible subtypes. This is mainly
	 * used for plugin help output.
	 */
	public static String subTypes = "baby, black, blue, brown, cyan, " +
		"gray, green, lightblue, lime, magenta, orange, pink, purple, red, " +
		"silver, white, yellow, sheared, charged, tiny, small, big, bigger, massive, godzilla, " +
		"tamed, aggressive, tabby, tuxedo, siamese, burning, saddled, " +
		"librarian, priest, blacksmith, butcher, generic, infected, wither, " +
		"storage, powered, tnt, spawner, hopper, donkey, mule, undead, skeletal, " +
		"hasarms, nobase, gold, blackwhite, saltpepper, killer";
}