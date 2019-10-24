package osmowsis.model.lawnmower;

import osmowsis.misc.Direction;

/**
 * 
 * 
 * Encapsulates an action for the mower
 * 
 * @author Laura  Wang
 *
 */
public class MowerAction {
	
	/**
	 * The action type {@link MowerActionType}
	 */
	private MowerActionType actionType;
	
	/**
	 * Number of steps for a move type action
	 */
	private int moveSteps;
	
	/**
	 * The new direction for turning as part of move type action
	 */
	private Direction newDirection;
	
	/**
	 * Constructor
	 * @param type The {@link MowerActionType} action type
	 */
	public MowerAction(MowerActionType type){
		actionType = type;
	}
	
	/**
	 * Constructs a mower action
	 * @param type The {@link MowerActionType} action type
	 * @param steps The number of steps
	 * @param newDir THe new direction for move type action
	 */
	public MowerAction(MowerActionType type, int steps, Direction newDir) {
		actionType = type;
		moveSteps = steps;
		newDirection = newDir;
	}
	
	/**
	 * Returns the type of the action
	 * @return The {@link MowerActionType} type
	 */
	public MowerActionType getType() {
		return actionType;
	}
	
	/**
	 * Returns the number of steps for move type action only
	 * @return The number of steps
	 */
	public int getSteps() {
		return moveSteps;
	}

	/**
	 * Returns the new direction for turning in a move type action
	 * @return The {@link Direction}
	 */
	public Direction getNewDirection() {
		return newDirection;
	}
}
