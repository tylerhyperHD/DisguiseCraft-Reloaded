package net.hexadigital.disguisecraft.disguises;

public interface BoatDisguise extends VehicleDisguise {

	/**Gets the maximum speed of a boat
	 * @return double
	 */
	double getMaxSpeed();
	
	/**Get the deceleration rate (newSpeed = curSpeed*rate) of occupied boats
	 * @return double
	 */
	double getOccupiedDeceleration();
	
	/**Gets deceleration rate (newSpeed = curSpeed*rate) of unoccupied boats.
	 * @return double
	 */
	double getUnoccupiedDeceleration();
	
	/**Get whether boats can work on land.
	 * @return boolean
	 */
	boolean getWorkOnLand();
	
	/**Sets the maximum speed of a boat
	 * @param double
	 */
	void setMaxSpeed(double speed);
	
	/**Sets the deceleration rate (newSpeed = curSpeed * rate) of occupied boats.
	 * @param double
	 */
	void setOccupiedDeceleration(double rate);
	
	/**Sets the deceleration rate (newSpeed = curSpeed * rate) of unoccupied boats.
	 * @param double
	 */
	void setUnoccupiedDeceleration(double rate);
	
	/**Sets whether boats can work on land
	 * @param boolean
	 */
	void setWorkOnLand(boolean workOnLand);
}
