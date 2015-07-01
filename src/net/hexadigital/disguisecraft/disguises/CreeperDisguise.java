package net.hexadigital.disguisecraft.disguises;

public interface CreeperDisguise extends MonsterDisguise {

	/**Checks if this Creeper is powered (Electrocuted)
	 * @return boolean
	 */
	boolean isPowered();
	
	/**Sets the Powered status of this Creeper
	 * @param boolean
	 */
	void setPowered(boolean value);
}
