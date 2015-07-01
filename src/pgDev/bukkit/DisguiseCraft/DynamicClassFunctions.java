package pgDev.bukkit.DisguiseCraft;

//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
import java.lang.reflect.Method;

import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
//import org.bukkit.inventory.ItemStack;

public class DynamicClassFunctions {
	
	//public static String nmsPrefix = "net.minecraft.server";
	//public static String obcPrefix = "org.bukkit.craftbukkit";
	
	public static String nmsPackage = "net.minecraft.server.v1_7_R3";
	public static String obcPackage = "org.bukkit.craftbukkit.v1_7_R3";
	
	public static HashMap<Player, Object> netServerHandlers = new HashMap<Player, Object>();

	public static boolean setPackages() {
		Server craftServer = Bukkit.getServer();
		if (craftServer != null) {
			try {
				Class<?> craftClass = craftServer.getClass();
				Method getHandle = craftClass.getMethod("getHandle");
				Class<?> returnType = getHandle.getReturnType();

				obcPackage = craftClass.getPackage().getName();
				nmsPackage = returnType.getPackage().getName();
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}
	
	/*
	public static HashMap<String, Class<?>> classes = new HashMap<String, Class<?>>();
	public static boolean setClasses() {
		try {
			// org.bukkit.craftbukkit
			classes.put("CraftPlayer", Class.forName(obcPackage + ".entity.CraftPlayer"));
			classes.put("CraftItemStack", Class.forName(obcPackage + ".inventory.CraftItemStack"));
			
			// net.minecraft.server
			classes.put("EntityPlayer", Class.forName(nmsPackage + ".EntityPlayer"));
			classes.put("PlayerConnection", Class.forName(nmsPackage + ".PlayerConnection"));
			classes.put("Packet", Class.forName(nmsPackage + ".Packet"));
			classes.put("MathHelper", Class.forName(nmsPackage + ".MathHelper"));
			classes.put("DataWatcher", Class.forName(nmsPackage + ".DataWatcher"));
			classes.put("ItemStack", Class.forName(nmsPackage + ".ItemStack"));
			classes.put("Entity", Class.forName(nmsPackage + ".Entity"));
			classes.put("World", Class.forName(nmsPackage + ".World"));
			classes.put("WatchableObject", Class.forName(nmsPackage + ".WatchableObject"));
			classes.put("EntityHuman", Class.forName(nmsPackage + ".EntityHuman"));
			
			// Packets
			classes.put("PacketPlayOutSpawnEntityLiving", Class.forName(nmsPackage + ".PacketPlayOutSpawnEntityLiving"));
			classes.put("PacketPlayOutNamedEntitySpawn", Class.forName(nmsPackage + ".PacketPlayOutNamedEntitySpawn"));
			classes.put("PacketPlayOutSpawnEntity", Class.forName(nmsPackage + ".PacketPlayOutSpawnEntity"));
			classes.put("PacketPlayOutEntityDestroy", Class.forName(nmsPackage + ".PacketPlayOutEntityDestroy"));
			classes.put("PacketPlayOutEntityEquipment", Class.forName(nmsPackage + ".PacketPlayOutEntityEquipment"));
			classes.put("PacketPlayOutEntityLook", Class.forName(nmsPackage + ".PacketPlayOutEntityLook"));
			classes.put("PacketPlayOutRelEntityMoveLook", Class.forName(nmsPackage + ".PacketPlayOutRelEntityMoveLook"));
			classes.put("PacketPlayOutEntityTeleport", Class.forName(nmsPackage + ".PacketPlayOutEntityTeleport"));
			classes.put("PacketPlayOutEntityMetadata", Class.forName(nmsPackage + ".PacketPlayOutEntityMetadata"));
			classes.put("PacketPlayOutPlayerInfo", Class.forName(nmsPackage + ".PacketPlayOutPlayerInfo"));
			classes.put("PacketPlayOutEntityHeadRotation", Class.forName(nmsPackage + ".PacketPlayOutEntityHeadRotation"));
			classes.put("PacketPlayOutAnimation", Class.forName(nmsPackage + ".PacketPlayOutAnimation"));
			classes.put("PacketPlayOutEntityStatus", Class.forName(nmsPackage + ".PacketPlayOutEntityStatus"));
			classes.put("PacketPlayOutCollect", Class.forName(nmsPackage + ".PacketPlayOutCollect"));
			classes.put("PacketPlayOutEntityVelocity", Class.forName(nmsPackage + ".PacketPlayOutEntityVelocity"));
			return true;
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not aquire a required class", e);
			return false;
		}
	}
	
	public static HashMap<String, Method> methods = new HashMap<String, Method>();
	public static boolean setMethods() {
		try {
			// org.bukkit.craftbukkit
			methods.put("CraftPlayer.getHandle()", classes.get("CraftPlayer").getDeclaredMethod("getHandle"));
			methods.put("CraftItemStack.asNMSCopy(ItemStack)", classes.get("CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class));
			
			// net.minecraft.server
			methods.put("PlayerConnection.sendPacket(Packet)", classes.get("PlayerConnection").getDeclaredMethod("sendPacket", classes.get("Packet")));
			methods.put("MathHelper.floor(double)", classes.get("MathHelper").getDeclaredMethod("floor", double.class));
			methods.put("WatchableObject.a()", classes.get("WatchableObject").getDeclaredMethod("a"));
			methods.put("WatchableObject.b()", classes.get("WatchableObject").getDeclaredMethod("b"));
			methods.put("WatchableObject.c()", classes.get("WatchableObject").getDeclaredMethod("c"));
			methods.put("DataWatcher.a(int, Object)", classes.get("DataWatcher").getDeclaredMethod("a", int.class, Object.class));
			methods.put("DataWatcher.watch(int, Object)", classes.get("DataWatcher").getDeclaredMethod("watch", int.class, Object.class));
			methods.put("EntityHuman.attack(Entity)", classes.get("EntityHuman").getDeclaredMethod("attack", classes.get("Entity")));
			return true;
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not find a required method", e);
			return false;
		}
	}
	
	public static HashMap<String, Field> fields = new HashMap<String, Field>();
	public static boolean setFields() {
		try {
			fields.put("EntityPlayer.playerConnection", classes.get("EntityPlayer").getDeclaredField("playerConnection"));
			fields.put("EntityPlayer.ping", classes.get("EntityPlayer").getDeclaredField("ping"));
			return true;
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not find a field class", e);
			return false;
		}
	}
	
	public static Object getCastTo(Object toCast, String newClass) {
		return classes.get(newClass).cast(toCast);
	}
	
	
	public static void addNSH(Player player) {
		try {
			Object entityPlayer = convertPlayerEntity(player);
			if (entityPlayer.getClass() == classes.get("EntityPlayer")) {
				netServerHandlers.put(player, fields.get("EntityPlayer.playerConnection").get(entityPlayer));
			}
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not obtain NSH of player: " + player.getName(), e);
		}
	}
	
	public static void removeNSH(Player player) {
		netServerHandlers.remove(player);
	}
	
	
	public static void sendPacket(Player player, Object packet) {
		if (netServerHandlers.containsKey(player)) {
			try {
				methods.get("PlayerConnection.sendPacket(Packet)").invoke(netServerHandlers.get(player), packet);
			} catch (Exception e) {
				DisguiseCraft.logger.log(Level.SEVERE, "Error sending packet to player: " + player.getName(), e);
			}
		}
	}
	
	// Math Floor
	public static int mathHelperFloor(double toFloor) {
		try {
			return (Integer) methods.get("MathHelper.floor(double)").invoke(null, toFloor);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not use MathHelper.floor(double)");
			return 0;
		}
	}
	
	// Convert Player-Entity
	public static Object convertPlayerEntity(Player player) {
		try {
			return methods.get("CraftPlayer.getHandle()").invoke(player);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not convert Player-Entity with name: " + player.getName(), e);
			return null;
		}
	}
	
	// Convert ItemStack
	public static Object convertItemStack(ItemStack item) {
		try {
			return methods.get("CraftItemStack.asNMSCopy(ItemStack)").invoke(null, item);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not convert an ItemStack", e);
			return null;
		}
	}
	
	// Get Player ping
	public static Integer getPlayerPing(Player player) {
		try {
			return fields.get("EntityPlayer.ping").getInt(convertPlayerEntity(player));
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not get ping of player: " + player.getName(), e);
			return null;
		}
	}
	
	// Player-Entity Attack
	public static void playerEntityAttack(Object attacker, Object victim) {
		try {
			methods.get("EntityHuman.attack(Entity)").invoke(attacker, victim);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Could not process a Player-Entity attack", e);
		}
	}
	
	// Packet Construction
	public static Object constructPacket(String packetName, LinkedList<PacketField> values) {
		try {
			Object packet = classes.get(packetName).newInstance();
			for (PacketField value : values) {
				Class<?> cls = packet.getClass();
				
				for (int i = value.superLocation; i > 0; i--) {
					cls = cls.getSuperclass();
				}
				
				Field field = cls.getDeclaredField(value.field);
				if (!value.accessible) field.setAccessible(true);
				field.set(packet, value.value);
			}
			return packet;
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Error constructing a " + packetName, e);
			return null;
		}
	}
	
	public static String equipmentChangePacketName = "PacketPlayOutEntityEquipment";
	public static Object constructEquipmentChangePacket(int entityID, short slot, ItemStack item) {
		try {
			Constructor<?> ctor = classes.get(equipmentChangePacketName).getConstructor(int.class, int.class, classes.get("ItemStack"));
			
			Object itemStack = null;
			if (item != null) {
				itemStack = convertItemStack(item);
			}
			
			return ctor.newInstance(entityID, slot, itemStack);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Error constructing a " + equipmentChangePacketName, e);
			return null;
		}
	}
	
	public static String entityMetadataPacketName = "PacketPlayOutEntityMetadata";
	public static Object constructMetadataPacket(int entityID, Object dataWatcher) {
		try {
			Constructor<?> ctor = classes.get(entityMetadataPacketName).getConstructor(int.class, classes.get("DataWatcher"), boolean.class);
			return ctor.newInstance(entityID, dataWatcher, true);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Error constructing a " + equipmentChangePacketName, e);
			return null;
		}
	}*/
}
