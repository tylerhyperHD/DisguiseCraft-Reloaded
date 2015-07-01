package net.hexadigital.disguisecraft.disguises;

/**
 * This represents a disguise of a sheep
 * Sheep can be sheared
 *
 * @author Devil Boy
 *
 */
public interface SheepDisguise extends AnimalDisguise, ColorableDisguise {

	/**
	 * Checks if this sheep disguise is sheared
	 * @return True if sheared, false if not
	 */
	boolean isSheared();
	
	/**
	 * Sets the sheared status of this disguise
	 * @param sheared Sheared if true, unsheared if false
	 */
	void setSheared(boolean sheared);
}
