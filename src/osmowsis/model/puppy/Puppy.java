package osmowsis.model.puppy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import osmowsis.misc.Direction;
import osmowsis.misc.Square;
import osmowsis.misc.SquareType;

/**
 * 
 * 
 * The Puppy model class. The puppy knows its position and can "see" the
 * landscape.
 * 
 * @author Laura  Wang
 *
 */
public class Puppy {

	private int stayPercent;
	private SquareType[][] lawn;
	private Square currentPosition;
	private int puppyId;
	private PuppyAction act;

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The id for the puppy (0, 1, 2 ...)
	 * @param stayPercent
	 *            The percentage of time puppy would choose to stay
	 * @param position
	 *            The starting position of the puppy
	 */
	public Puppy(int id, int stayPercent, Square position) {
		this.stayPercent = stayPercent;
		this.puppyId = id;
		this.currentPosition = position;
	}

	/**
	 * Puppy "updates" its knowledge of the lawn
	 * 
	 * @param lawn
	 *            The lawn landsacpe
	 */
	public void setLawn(SquareType[][] lawn) {
		this.lawn = lawn;
	}

	/**
	 * Returns the id of the uppy
	 * 
	 * @return
	 */
	public int getId() {
		return this.puppyId;
	}

	/**
	 * Decides the next action
	 * 
	 * @return The next {@link PuppyAction}
	 */
	public PuppyAction decidesNextAction() {
		// uniform between 0 and 100
		double random = new Random().nextInt(101);

		// random is less than or equal to stayPercent
		if (random <= stayPercent) {
			act = new PuppyAction(PuppyActionType.Stay, null);
		} else {
			List<Square> safeSquares = getSurroundingSafeSquares();

			if (!safeSquares.isEmpty()) {
				int size = safeSquares.size();
				int randomIndex = new Random().nextInt(size);
				act = new PuppyAction(PuppyActionType.Move, safeSquares.get(randomIndex));
			} else {
				act = new PuppyAction(PuppyActionType.Stay, null);
			}
		}
		printAction();
		return act;
	}

	/**
	 * Prints the puppy action
	 */
	private void printAction() {
		System.out.println("puppy," + (getId() + 1));
		if (act.getType() == PuppyActionType.Stay) {
			System.out.println("stay");
		} else {
			Square destination = act.getDestination();
			System.out.println("move," + destination.getX() + "," + (lawn.length - destination.getY() - 1));
		}
	}

	/**
	 * Process the response from the simulator
	 */
	public void processOkayResponse() {
		if (act.getType() == PuppyActionType.Move) {
			this.currentPosition = act.getDestination();
		}
	}

	/**
	 * Returns the current position of the puppy
	 * 
	 * @return The {@link Square} position
	 */
	public Square currentPostion() {
		return this.currentPosition;
	}

	/**
	 * Returns the list of "safe" square to move to. Safe is a square that is not a
	 * crater and is not occupied by a puppy.
	 * 
	 * @return THe list of squares
	 */
	private List<Square> getSurroundingSafeSquares() {
		List<Square> safeSquares = new ArrayList<Square>();
		for (Direction dir : getEightDirections()) {
			int x = getRelativeX(1, dir);
			int y = getRelativeY(1, dir);

			if (x < 0 || y < 0 || x > lawn[0].length - 1 || y > lawn.length - 1) {
				continue;
			}
			SquareType type = lawn[y][x];

			boolean notSafe = type == SquareType.Crater || type == SquareType.Puppy_empty
					|| type == SquareType.Puppy_grass || type == SquareType.Puppy_mower;

			if (!notSafe) {
				safeSquares.add(new Square(x, y));
			}
		}
		return safeSquares;
	}

	/**
	 * List of 8 {@link Direction} directions
	 * 
	 * @return The directions
	 */
	private List<Direction> getEightDirections() {
		List<Direction> scanDirections = new ArrayList<Direction>();
		scanDirections.add(Direction.NORTH);
		scanDirections.add(Direction.NORTH_EAST);
		scanDirections.add(Direction.EAST);
		scanDirections.add(Direction.SOUTH_EAST);
		scanDirections.add(Direction.SOUTH);
		scanDirections.add(Direction.SOUTH_WEST);
		scanDirections.add(Direction.WEST);
		scanDirections.add(Direction.NORTH_WEST);
		return scanDirections;
	}

	/**
	 * Returns the x coordinate after taking the given number of steps in the given
	 * direction from the given x starting point
	 * 
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result x coordinate
	 */
	private int getRelativeX(int steps, Direction dir) {
		int x = currentPosition.getX();
		if (dir == Direction.NORTH_EAST || dir == Direction.SOUTH_EAST || dir == Direction.EAST) {
			x += steps;
		} else if (dir == Direction.NORTH_WEST || dir == Direction.SOUTH_WEST || dir == Direction.WEST) {
			x -= steps;
		}
		return x;
	}

	/**
	 * Returns the y coordinate after taking the given number of steps in the given
	 * direction from the given y starting point
	 * 
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result x coordinate
	 */
	private int getRelativeY(int steps, Direction dir) {
		int y = currentPosition.getY();
		if (dir == Direction.NORTH || dir == Direction.NORTH_EAST || dir == Direction.NORTH_WEST) {
			y -= steps;
		} else if (dir == Direction.SOUTH_WEST || dir == Direction.SOUTH_EAST || dir == Direction.SOUTH) {
			y += steps;
		}
		return y;
	}

}