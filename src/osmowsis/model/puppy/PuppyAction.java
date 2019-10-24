package osmowsis.model.puppy;

import osmowsis.misc.Square;

/**
 * 
 * 
 * Encapsulates an action for the puppy
 * 
 * @author Laura  Wang
 *
 */
public class PuppyAction {

	/**
	 * The {@link PuppyActionType} action type
	 */
	private PuppyActionType actionType;
	
	/**
	 * The new destination square in a {@link PuppyActionType#Move} type action
	 */
	private Square newLocation;
	
	/**
	 * Constructor
	 * @param type The {@link PuppyAcitonType} action type
	 * @param desination The destination square for {@link PuppyActionType#Move} type action
	 */
	public PuppyAction(PuppyActionType type, Square desination) {
		this.actionType = type;
		this.newLocation = desination;
	}
	
	/**
	 * Returns the action type
	 * @return The {@link PuppyMoveActionType} action type
	 */
	public PuppyActionType getType() {
		return this.actionType;
	}
	
	/**
	 * The destination square
	 * @return The square or null
	 */
	public Square getDestination() {
		return this.newLocation;
	}
}