package pgDev.bukkit.DisguiseCraft.listeners.protocol;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.listeners.PlayerInvalidInteractEvent;
import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldServer;

public class DCPacketInListener extends PlayerConnection {
	public static boolean hookFail;
	
	static Field targetField;
	
	static {
		// Find the target entity field
		for (Field f : PacketPlayInUseEntity.class.getDeclaredFields()) {
			if (f.getType() == int.class && !Modifier.isStatic(f.getModifiers())) {
				f.setAccessible(true);
				targetField = f;
			}
		}
		
		hookFail = false;
		if (targetField == null) {
			DisguiseCraft.logger.log(Level.WARNING, "Attack hook could not find target entity field");
			hookFail = true;
		}
	}

	public DCPacketInListener(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
		super(minecraftserver, networkmanager, entityplayer);
	}

	@Override
	public void a(PacketPlayInUseEntity packet) {
		if (hookFail) {
			super.a(packet);
		} else {
			if (this.player.dead) return;
		    WorldServer worldserver = MinecraftServer.getServer().getWorldServer(this.player.dimension);
		    Entity entity = packet.a(worldserver);
			
		    //this.player.v();
		    if (entity == null) {
		    	int target = 0;
				try {
					target = targetField.getInt(packet);
				} catch (Exception e) {
					DisguiseCraft.logger.log(Level.WARNING, "Could not access a field in the use entity packet", e);
				}
				
				String action = packet.a().name();
		    	
		    	PlayerInvalidInteractEvent newEvent = new PlayerInvalidInteractEvent(((CraftServer) Bukkit.getServer()).getPlayer(this.player), target, action);
		        Bukkit.getServer().getPluginManager().callEvent(newEvent);
		    } else {
		    	super.a(packet);
		    }
		}
	}
	
	public static void overrideConnection(Player player) {
		EntityPlayer playerEntity = ((CraftPlayer) player).getHandle();
		PlayerConnection originalConnection = playerEntity.playerConnection;
		
		DCPacketInListener newConnection = new DCPacketInListener(MinecraftServer.getServer(), originalConnection.networkManager, playerEntity);
		newConnection.teleport(player.getLocation());
	}
}
