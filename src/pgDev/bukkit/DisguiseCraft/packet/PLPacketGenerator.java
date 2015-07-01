package pgDev.bukkit.DisguiseCraft.packet;

import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

import org.bukkit.Location;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.FieldAccessException;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import pgDev.bukkit.DisguiseCraft.*;
import pgDev.bukkit.DisguiseCraft.disguise.*;

public class PLPacketGenerator extends DCPacketGenerator {
	ProtocolManager pM = DisguiseCraft.protocolManager;

	public PLPacketGenerator(Disguise disguise) {
		super(disguise);
	}
	
	// Packet creation methods
	@Override
	public PacketPlayOutSpawnEntityLiving getMobSpawnPacket(Location loc, String name) {
		// Make values
		int[] locVars = getLocationVariables(loc);
		byte[] yp = getYawPitch(loc);
		int eID = d.entityID;
		int mobID = d.type.id;
		int xPos = locVars[0];
		int yPos = locVars[1];
		int zPos = locVars[2];
		
		// Make packet
		PacketContainer pC = pM.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		try {
			pC.getIntegers().
				write(0, eID).
				write(1, mobID).
				write(2, xPos).
				write(3, yPos).
				write(4, zPos);
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the integers for a " + d.type.name() +  " disguise!", e);
		}
		try {
			pC.getBytes().
				write(0, yp[0]).
				write(1, yp[1]).
				write(2, yp[0]);
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the bytes for a " + d.type.name() +  " disguise!", e);
		}
		
		DataWatcher metadata = d.metadata;
		if (name != null) {
			metadata = d.mobNameData(name);
		}
		
		try {
			pC.getDataWatcherModifier().
				write(0, new WrappedDataWatcher(metadata));
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the metadata for a " + d.type.name() +  " disguise!", e);
		}
		return (PacketPlayOutSpawnEntityLiving) pC.getHandle();
	}
	
	@Override
	public PacketPlayOutNamedEntitySpawn getPlayerSpawnPacket(Location loc, short item) {
		// Make Values
		int[] locVars = getLocationVariables(loc);
		byte[] yp = getYawPitch(loc);
		int eID = d.entityID;
        int xPos = locVars[0];
        int yPos = locVars[1];
        int zPos = locVars[2];
        
        // Make Packet
        PacketContainer pC = pM.createPacket(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
		try {
			pC.getIntegers().
				write(0, eID).
				write(1, xPos).
				write(2, yPos).
				write(3, zPos).
				write(4, (int) item);
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the integers for a player disguise!", e);
		}
		try {
			pC.getSpecificModifier(UUID.class).
				write(0, DisguiseCraft.profileCache.retrieveUUID(d.data.getFirst()));
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the UUID for a player disguise!", e);
		}
		try {
			pC.getBytes().
				write(0, yp[0]).
				write(1, yp[1]);
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the bytes for a player disguise!", e);
		}
		try {
			pC.getDataWatcherModifier().
				write(0, new WrappedDataWatcher(d.metadata));
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the metadata for a player disguise!", e);
		}
        return (PacketPlayOutNamedEntitySpawn) pC.getHandle();
	}
	
	@Override
	public PacketPlayOutEntityDestroy getEntityDestroyPacket() {
		PacketContainer pC = pM.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		try {
			pC.getIntegerArrays().
				write(0, new int[] {d.entityID});
		} catch (FieldAccessException e) {
			DisguiseCraft.logger.log(Level.SEVERE, "PL: Unable to modify the integer array for a destroy packet!", e);
		}
		return (PacketPlayOutEntityDestroy) pC.getHandle();
	}
}
