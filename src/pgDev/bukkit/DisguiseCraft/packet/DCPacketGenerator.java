package pgDev.bukkit.DisguiseCraft.packet;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.server.v1_8_R3.DataWatcher;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.MathHelper;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutAnimation;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutCollect;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutEntityLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityHeadRotation;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityStatus;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityEquipment;

import com.mojang.authlib.GameProfile;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;

import pgDev.bukkit.DisguiseCraft.*;
import pgDev.bukkit.DisguiseCraft.disguise.*;
import pgDev.bukkit.DisguiseCraft.mojangauth.ProfileCache;

public class DCPacketGenerator {
	final Disguise d;
	
	protected int encposX;
	protected int encposY;
	protected int encposZ;
	protected boolean firstpos = true;
	
	public DCPacketGenerator(final Disguise disguise) {
		d = disguise;
	}
	
	// Vital packet methods
	public Packet getSpawnPacket(Player disguisee, String name) {
		if (d.type.isMob()) {
			return getMobSpawnPacket(disguisee.getLocation(), name);
		} else if (d.type.isPlayer()) {
			return getPlayerSpawnPacket(disguisee.getLocation(), (short) disguisee.getItemInHand().getTypeId());
		} else {
			return getObjectSpawnPacket(disguisee.getLocation());
		}
	}
	
	public Packet getSpawnPacket(Location loc) {
		if (d.type.isMob()) {
			return getMobSpawnPacket(loc, null);
		} else if (d.type.isPlayer()) {
			return getPlayerSpawnPacket(loc, (short) 0);
		} else {
			return getObjectSpawnPacket(loc);
		}
	}
	
	public LinkedList<Packet> getArmorPackets(Player player) {
		LinkedList<Packet> packets = new LinkedList<Packet>();
		ItemStack[] armor;
		if (player == null) {
			armor = new  ItemStack[] {new ItemStack(0, 0), new ItemStack(0, 0), new ItemStack(0, 0), new ItemStack(0, 0)};
		} else {
			armor = player.getInventory().getArmorContents();
		}
		for (byte i=0; i < armor.length; i++) {
			packets.add(getEquipmentChangePacket((short) (i + 1), armor[i]));
		}
		return packets;
	}
	
	// Individual packet generation methods
	public int[] getLocationVariables(Location loc) {
		int x = MathHelper.floor(loc.getX() *32D);
		int y = MathHelper.floor(loc.getY() *32D);
		int z = MathHelper.floor(loc.getZ() *32D);
		if(firstpos) {
			encposX = x;
			encposY = y;
			encposZ = z;
			firstpos = false;
		}
		return new int[] {x, y, z};
	}
	
	public PacketPlayOutSpawnEntityLiving getMobSpawnPacket(Location loc, String name) {
		int[] locVars = getLocationVariables(loc);
		byte[] yp = getYawPitch(loc);
		
		DataWatcher metadata = d.metadata;
		if (name != null) {
			metadata = d.mobNameData(name);
		}
		
		PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field typeField = packet.getClass().getDeclaredField("b");
			Field xField = packet.getClass().getDeclaredField("c");
			Field yField = packet.getClass().getDeclaredField("d");
			Field zField = packet.getClass().getDeclaredField("e");
			Field yawField = packet.getClass().getDeclaredField("i");
			Field pitchField = packet.getClass().getDeclaredField("j");
			Field headYawField = packet.getClass().getDeclaredField("k");
			Field metadataField = packet.getClass().getDeclaredField("l");
			
			idField.setAccessible(true);
			typeField.setAccessible(true);
			xField.setAccessible(true);
			yField.setAccessible(true);
			zField.setAccessible(true);
			yawField.setAccessible(true);
			pitchField.setAccessible(true);
			headYawField.setAccessible(true);
			metadataField.setAccessible(true);
			
			idField.set(packet, d.entityID);
			typeField.set(packet, d.type.id);
			xField.set(packet, locVars[0]);
			yField.set(packet, locVars[1]);
			zField.set(packet, locVars[2]);
			yawField.setByte(packet, yp[0]);
			pitchField.setByte(packet, yp[1]);
			headYawField.setByte(packet, yp[0]);
			metadataField.set(packet, metadata);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for a " + d.type.name() +  " disguise!", e);
		}
		return packet;
	}
	
	public PacketPlayOutNamedEntitySpawn getPlayerSpawnPacket(Location loc, short item) {
		int[] locVars = getLocationVariables(loc);
		byte[] yp = getYawPitch(loc);
		
		PacketPlayOutNamedEntitySpawn packet = new PacketPlayOutNamedEntitySpawn();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field profileField = packet.getClass().getDeclaredField("b");
			Field xField = packet.getClass().getDeclaredField("c");
			Field yField = packet.getClass().getDeclaredField("d");
			Field zField = packet.getClass().getDeclaredField("e");
			Field yawField = packet.getClass().getDeclaredField("f");
			Field pitchField = packet.getClass().getDeclaredField("g");
			Field itemField = packet.getClass().getDeclaredField("h");
			Field metadataField = packet.getClass().getDeclaredField("i");
			
			idField.setAccessible(true);
			profileField.setAccessible(true);
			xField.setAccessible(true);
			yField.setAccessible(true);
			zField.setAccessible(true);
			yawField.setAccessible(true);
			pitchField.setAccessible(true);
			itemField.setAccessible(true);
			metadataField.setAccessible(true);
			
			idField.set(packet, d.entityID);
			profileField.set(packet, DisguiseCraft.profileCache.retrieveUUID(d.data.getFirst())); // Previously: GameProfile(UUID id, String name)
			xField.set(packet, locVars[0]);
			yField.set(packet, locVars[1]);
			zField.set(packet, locVars[2]);
			yawField.set(packet, yp[0]);
			pitchField.set(packet, yp[1]);
			itemField.set(packet, item);
			metadataField.set(packet, d.metadata);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for a player disguise!", e);
		}
		return packet;
	}
	
	public PacketPlayOutSpawnEntity getObjectSpawnPacket(Location loc) {
		int data = 0;
		
		// Block specific
    	if (d.type.isBlock()) {
    		loc.setY(loc.getY() + 0.5);
    		
    		Integer blockID = d.getBlockID();
    		if (blockID != null) {
    			data = blockID.intValue();
    			
    			Byte blockData = d.getBlockData();
    			if (blockData != null) {
    				data = data | (((int) blockData) << 0x10);
    			}
    		}
    	}
    	
    	// Vehicle specific
    	if (d.type.isVehicle()) {
    		Integer cartID = d.getMinecartType();
    		if (cartID != null) {
    			data = cartID.intValue();
    		}
    	}
    	
    	int[] locVars = getLocationVariables(loc);
    	byte[] yp = getYawPitch(loc);
    	
    	PacketPlayOutSpawnEntity packet = new PacketPlayOutSpawnEntity();
    	
    	try {
    		Field idField = packet.getClass().getDeclaredField("a");
    		Field locXField = packet.getClass().getDeclaredField("b");
    		Field locYField = packet.getClass().getDeclaredField("c");
    		Field locZField = packet.getClass().getDeclaredField("d");
    		Field velXField = packet.getClass().getDeclaredField("e");
    		Field velYField = packet.getClass().getDeclaredField("f");
    		Field velZField = packet.getClass().getDeclaredField("g");
    		Field pitchField = packet.getClass().getDeclaredField("h");
    		Field yawField = packet.getClass().getDeclaredField("i");
    		Field typeField = packet.getClass().getDeclaredField("j");
    		Field dataField = packet.getClass().getDeclaredField("k");
    		
    		idField.setAccessible(true);
    		locXField.setAccessible(true);
    		locYField.setAccessible(true);
    		locZField.setAccessible(true);
    		velXField.setAccessible(true);
    		velYField.setAccessible(true);
    		velZField.setAccessible(true);
    		pitchField.setAccessible(true);
    		yawField.setAccessible(true);
    		typeField.setAccessible(true);
    		dataField.setAccessible(true);
    		
    		idField.set(packet, d.entityID);
    		locXField.set(packet, locVars[0]);
    		locYField.set(packet, locVars[1]);
    		locZField.set(packet, locVars[2]);
    		velXField.set(packet, 0);
    		velYField.set(packet, 0);
    		velZField.set(packet, 0);
    		pitchField.set(packet, yp[1]);
    		yawField.set(packet, yp[0]);
    		typeField.set(packet, d.type.id);
    		dataField.set(packet, data);
    	} catch (Exception e) {
    		DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for a " + d.type.name() +  " disguise!", e);
		}
		
		return packet;
	}
	
	public PacketPlayOutEntityDestroy getEntityDestroyPacket() {
		return new PacketPlayOutEntityDestroy(new int[] {d.entityID});
	}
	
	public PacketPlayOutEntityEquipment getEquipmentChangePacket(short slot, ItemStack item) {
		return new PacketPlayOutEntityEquipment(d.entityID, slot, (item == null) ? null : CraftItemStack.asNMSCopy(item));
	}
	
	public byte[] getYawPitch(Location loc) {
		byte yaw = DisguiseCraft.degreeToByte(loc.getYaw());
		byte pitch = DisguiseCraft.degreeToByte(loc.getPitch());
		if (d.type == DisguiseType.EnderDragon) { // EnderDragon specific
			yaw = (byte) (yaw - 128);
		}/* else if (d.type == DisguiseType.Chicken) { // Chicken fix
			pitch = (byte) (pitch * -1);
		}*/ else if (d.type.isVehicle()) { // Vehicle fix
			yaw = (byte) (yaw - 64);
		}
		return new byte[] {yaw, pitch};
	}
	
	public PacketPlayOutEntityLook getEntityLookPacket(Location loc) {
		byte[] yp = getYawPitch(loc);
		return new PacketPlayOutEntityLook(d.entityID, yp[0], yp[1], true); // For now lets just set onGround to true
	}
	
	public PacketPlayOutRelEntityMoveLook getEntityMoveLookPacket(Location loc) {
		byte[] yp = getYawPitch(loc);
		
		MovementValues movement = getMovement(loc);
		encposX += movement.x;
		encposY += movement.y;
		encposZ += movement.z;
		
		return new PacketPlayOutRelEntityMoveLook(d.entityID,
				(byte) movement.x, (byte) movement.y, (byte) movement.z,
				yp[0], yp[1], true); // Again just setting onGround to true
	}
	
	public PacketPlayOutEntityTeleport getEntityTeleportPacket(Location loc) {
		byte[] yp = getYawPitch(loc);
		
		int x = (int) MathHelper.floor(32D * loc.getX());
		int y = (int) MathHelper.floor(32D * loc.getY());
		int z = (int) MathHelper.floor(32D * loc.getZ());
		
		encposX = x;
		encposY = y;
		encposZ = z;
		
		return new PacketPlayOutEntityTeleport(d.entityID,
				x, y, z,
				yp[0], yp[1], true); // Still just setting onGround to true
	}
	
	public PacketPlayOutEntityMetadata getEntityMetadataPacket() {
		return new PacketPlayOutEntityMetadata(d.entityID, (DataWatcher) d.metadata, true); // 1.4.2 update: true-same method as 1.3.2
	}
	
	public PacketPlayOutPlayerInfo getPlayerInfoPacket(Player player, boolean show) {
		if (d.type.isPlayer()) {
			EntityPlayer ep = ((CraftPlayer) player).getHandle();
			if (show) {
				return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep);
			} else {
				return new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep);
			}
		} else {
			return null;
		}
	}
	
	public MovementValues getMovement(Location to) {
		int x = MathHelper.floor(to.getX() *32D);
		int y = MathHelper.floor(to.getY() *32D);
		int z = MathHelper.floor(to.getZ() *32D);
		int diffx = x - encposX;
		int diffy = y - encposY;
		int diffz = z - encposZ;
		return new MovementValues(diffx, diffy, diffz, DisguiseCraft.degreeToByte(to.getYaw()), DisguiseCraft.degreeToByte(to.getPitch()));
	}
	
	public PacketPlayOutEntityHeadRotation getHeadRotatePacket(Location loc) {
		PacketPlayOutEntityHeadRotation packet = new PacketPlayOutEntityHeadRotation();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field yawField = packet.getClass().getDeclaredField("b");
			
			idField.setAccessible(true);
			yawField.setAccessible(true);
			
			idField.set(packet, d.entityID);
			yawField.set(packet, DisguiseCraft.degreeToByte(loc.getYaw()));
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for a head rotation packet!", e);
		}
		
		return packet;
	}
	
	public PacketPlayOutAnimation getAnimationPacket(int animation) {
		// Stolen from protocol wiki (http://wiki.vg/Protocol):
		// 0 - Swing arm
		// 1 - Damage animation
		// 2 - Leave bed
		// 3 - Eat food
		// 4 - Critical effect
		// 5 - Magic critical effect
		PacketPlayOutAnimation packet = new PacketPlayOutAnimation();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field animationField = packet.getClass().getDeclaredField("b");
			
			idField.setAccessible(true);
			animationField.setAccessible(true);
			
			idField.set(packet, d.entityID);
			animationField.set(packet, animation);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for an animation packet!", e);
		}
		
		return packet;
	}
	
	public PacketPlayOutEntityStatus getStatusPacket(int status) {
		// May no longer be up-to-date:
		// 0 - ?
		// 1 - entity hurt
		// 2 - ?
		// 3 - entity dead
		// 6 - wolf taming
		// 7 - wolf tamed
		// 8 - wolf shaking water
		// 10 - sheep eating grass
		PacketPlayOutEntityStatus packet = new PacketPlayOutEntityStatus();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field statusField = packet.getClass().getDeclaredField("b");
			
			idField.setAccessible(true);
			statusField.setAccessible(true);
			
			idField.setInt(packet, d.entityID);
			statusField.setByte(packet, (byte) status);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for an entity status packet!", e);
		}
		
		return packet;
	}

	public PacketPlayOutCollect getPickupPacket(int item) {
		return new PacketPlayOutCollect(item, d.entityID);
	}
	
	public PacketPlayOutEntityVelocity getVelocityPacket(int x, int y, int z) {
		PacketPlayOutEntityVelocity packet = new PacketPlayOutEntityVelocity();
		
		try {
			Field idField = packet.getClass().getDeclaredField("a");
			Field xField = packet.getClass().getDeclaredField("b");
			Field yField = packet.getClass().getDeclaredField("c");
			Field zField = packet.getClass().getDeclaredField("d");
			
			idField.setAccessible(true);
			xField.setAccessible(true);
			yField.setAccessible(true);
			zField.setAccessible(true);
			
			idField.setInt(packet, d.entityID);
			xField.set(packet, x);
			yField.set(packet, y);
			zField.set(packet, z);
		} catch (Exception e) {
			DisguiseCraft.logger.log(Level.SEVERE, "Unable to set a field for a velocity packet!", e);
		}
		
		return packet;
	}
}
