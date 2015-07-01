package net.hexadigital.disguisecraft.disguises;

/**
 * This interface helps us track which disguises are naturally rideable
 *
 * @author Devil Boy
 *
 */
public interface VehicleDisguise extends EntityDisguise {

	/**
	 * Gets whether or not this disguise would normally be rideable in its current state
	 * An example of when this would change would be a pig and its saddle state
	 * @return True is rideable
	 */
	boolean isCurrentlyRideable();
}
