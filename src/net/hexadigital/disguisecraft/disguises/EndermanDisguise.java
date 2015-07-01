package net.hexadigital.disguisecraft.disguises;

import org.bukkit.material.MaterialData;

public interface EndermanDisguise extends MonsterDisguise {

	/**Get the id and data of the block that the Enderman is carrying.
	 * 
	 */
	MaterialData getCarriedMaterial();
	
	/**Set the id and data of the block that the Enderman is carring.
	 * 
	 */
	void setCarriedMaterial(MaterialData material);
}
