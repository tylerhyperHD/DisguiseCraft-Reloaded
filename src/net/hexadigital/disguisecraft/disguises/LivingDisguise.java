package net.hexadigital.disguisecraft.disguises;


/**
 * This represents a disguise of a living entity
 * Living entities can have potion effects applied to them
 * 
 * NOTE:  EntityEquipment methods were added here instead of
 * creating another interaface.
 *
 * @author Devil Boy
 *
 */
public interface LivingDisguise extends EntityDisguise {

	// TODO: Add methods for potion effects
	
	/**
	 * Gets a copy of the helmet currently being worn by the entity
	 * @return The name of the item being used as a helmet
	 */
	String getHelmet();
	
	/**
	 * Sets the helmet worn by the entity
	 * @param The name of the item for the disguise to use as a helmet
	 */
	void setHelmet(String itemName);
	
	/**
	 * Gets a copy of the chestplate currently being worn by the entity
	 * @return The name of the item being used as a chest plate
	 */
	String getChestPlate();
	
	/**
	 * Sets the chest plate worn by the entity
	 * @param The name of the item for the disguise to use as a chestplate
	 */
	void setChestPlate(String itemName);
	
	/**
	 * Gets a copy of the leggings currently being worn by the entity
	 * @return The name of the item being used as leggings
	 */
	String getLeggings();
	
	/**
	 * Sets the leggings worn by the entity
	 * @param The name of the item for the disguise to use as leggings
	 */
	void setLeggings(String itemName);
	
	/**
	 * Gets a copy of the boots currently being worn by the entity
	 * @return The name of the item being used as boots
	 */
	String getBoots();
	
	/**
	 * Sets the boots worn by the entity
	 * @param The name of the item for the disguise to use as boots
	 */
	void setBoots(String itemName);
	
	/**
	 * Gets a copy of the item the entity is currently holding
	 * @return The name of the item being held
	 */
	String getItemInHand();
	
	/**
	 * Sets the item the entity is holding
	 * @param The name of the item for the disguise to hold in hand
	 */
	void setItemInHand(String itemName);
}
