package net.hexadigital.disguisecraft.disguises;

/**
 * This represents a disguise of a Minecraft entity
 * Entities are not locked to the block grid and can move about the world
 *
 * @author Devil Boy
 *
 */
public interface EntityDisguise extends Disguise {

	/**
	 * Gets the entity ID this disguise uses
	 * @return A (hopefully) unique ID
	 */
	int getEntityId();
	
	/**
	 * Gets the X position of this entity disguise
	 * @return A double representing this disguise's block distance from the map origin in the X axis
	 */
	double getX();
	
	/**
	 * Gets the Y position of this entity disguise
	 * @returnA double representing this disguise's block distance from the map origin in the vertical axis
	 */
	double getY();
	
	/**
	 * Gets the Z position of this entity disguise
	 * @return A double representing this disguise's block distance from the map origin in the Z axis
	 */
	double getZ();
	
	/**
	 * Gets the yaw rotation of this entity disguise
	 * @return A float representing the degree of rotation for this entity disguise going clockwise
	 */
	float getYaw();
	
	/**
	 * Gets the pitch angle of this entity disguise
	 * @return A float representing the vertical angle (in degrees) of this entity disguise with positive being upward
	 */
	float getPitch();
	
	/**
	 * Gets the entity ID of the entity or disguise riding this disguise
	 * @return A unique ID
	 */
	int getRider();
	
	/**
	 * Sets the given entity ID as the rider of this disguise
	 * @param id The unique ID of the rider
	 */
	void setRider(int id);
}
