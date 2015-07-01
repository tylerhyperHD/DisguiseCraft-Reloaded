package net.hexadigital.disguisecraft.disguises;

public interface GuardianDisguise extends MonsterDisguise {

	/**
	 * Check if the Guardian is an elder Guardian
	 */
	boolean isElder();
	
	/**
	 * Set the Guardian to an elder Guardian or not
	 */
	void setElder(boolean shouldBeElder);
}
