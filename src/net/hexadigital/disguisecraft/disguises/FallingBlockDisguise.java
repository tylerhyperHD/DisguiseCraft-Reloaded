package net.hexadigital.disguisecraft.disguises;

import org.bukkit.Material;

public interface FallingBlockDisguise extends EntityDisguise {

	/**
	 * Get if the falling block will break into an item if it cannot be placed.
	 */
	boolean getDropItem();
	
	/**
	 * Get the Material of the falling block
	 */
	Material getMaterial();
	
	/**
	 * Set if the falling block will break into an item if it cannot be placed
	 */
	void setDropItem(boolean drop);
}
