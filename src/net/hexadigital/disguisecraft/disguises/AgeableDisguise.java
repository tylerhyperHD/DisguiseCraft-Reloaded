package net.hexadigital.disguisecraft.disguises;

/**
 * This represents an ageable disguise
 * Typically, these are disguises of entities that have a baby form
 *
 * @author Devil Boy
 *
 */
public interface AgeableDisguise extends CreatureDisguise {

	/**
	 * Gets whether or not this is an adult
	 * @return True if it is an adult
	 */
	boolean isAdult();
	
	/**
	 * Sets this to be an adult
	 */
	void setAdult();
	
	/**
	 * Sets this to be a baby
	 */
	void setBaby();
}
