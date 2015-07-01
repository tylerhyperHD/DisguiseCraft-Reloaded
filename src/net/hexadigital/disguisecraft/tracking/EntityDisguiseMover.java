package net.hexadigital.disguisecraft.tracking;

import net.hexadigital.disguisecraft.disguises.EntityDisguise;

/**
 * Implementations of this class can move entities about the world
 *
 * @author Devil Boy
 *
 */
public interface EntityDisguiseMover {

	/**
	 * Moves the specified disguise to the given position
	 * @param disguise The disguise to move
	 * @param x The X position to move to
	 * @param y The vertical position to move to
	 * @param z The Z position to move to
	 * @param yaw The disguise's new yaw
	 * @param pitch The disguise's new pitch
	 */
	void moveTo(EntityDisguise disguise, double x, double y, double z, float yaw, float pitch);
}
