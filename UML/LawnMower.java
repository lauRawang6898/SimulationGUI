package osmowsis.model.lawnmower;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import osmowsis.misc.Direction;
import osmowsis.misc.SharedScanInfoRepository;
import osmowsis.misc.SharedVisitInfoRepository;
import osmowsis.misc.Square;
import osmowsis.misc.SquareType;

/**
 * 
 * 
 * The LawnMower model class. The LawnMower starts off knowing its initial
 * direction and direction
 * 
 * @author Laura  Wang
 *
 */
public class LawnMower {

	/**
	 * Current direction
	 */
	private Direction currentDirection;

	/**
	 * Current position
	 */
	private Square currentPosition;

	/**
	 * All directions and number of steps for each move the mower makes
	 */
	private Stack<Direction> pathDirections = new Stack<Direction>();
	private Stack<Integer> pathSteps = new Stack<Integer>();

	/**
	 * Shared scan repository
	 */
	private SharedScanInfoRepository scanInfo = SharedScanInfoRepository.getInstance();

	/**
	 * Shared visit repository
	 */
	private SharedVisitInfoRepository visitInfo = SharedVisitInfoRepository.getInstance();

	/**
	 * Backtracking boolean
	 */
	private boolean backTracking = false;

	/**
	 * The action the mower makes
	 */
	private MowerAction action;

	/**
	 * The mowerId (0,1,2...)
	 */
	private int mowerId;

	/**
	 * If a puppy grass square is the only grass square available in surrounding
	 * area
	 */
	private boolean onlyPuppyGrassSquareAvailable;

	/**
	 * Constructor
	 * 
	 * @param direction
	 *            The initial direction the LawnMower is facing
	 * @param sqr
	 *            The initial lawn mower position
	 */
	public LawnMower(int id, Direction direction, Square sqr) {
		currentDirection = direction;
		int x = sqr.getX();
		int y = sqr.getY();
		currentPosition = sqr;
		visitInfo.put(calculateHashCode(x, y), true);
		mowerId = id;
	}

	/**
	 * The id the mower
	 * 
	 * @return The id
	 */
	public int getId() {
		return mowerId;
	}

	/**
	 * Returns the current direction of the mower
	 * 
	 * @return The current direction
	 */
	public Square currentPostion() {
		return currentPosition;
	}

	/**
	 * Updates the shared scan info repository
	 * 
	 * @param scanResult
	 *            The scan result
	 */
	public void updateSharedScanInfo(List<String> scanResult) {
		int x = currentPosition.getX();
		int y = currentPosition.getY();
		int hashCode = calculateHashCode(x, y);
		scanInfo.put(hashCode, SquareType.Mower);

		List<Direction> scanDirections = getEightDirections();
		for (int i = 0; i < scanResult.size(); i++) {
			Direction dir = scanDirections.get(i);
			x = getRelativeX(1, dir);
			y = getRelativeY(1, dir);

			hashCode = calculateHashCode(x, y);

			String scan = scanResult.get(i);
			SquareType type = SquareType.fromName(scan);

			if ((type == SquareType.Grass || type == SquareType.Puppy_grass) && !scanInfo.hasKey(hashCode)) {
				scanInfo.incrementSquareToExplore();
			}

			scanInfo.put(hashCode, type);

			if (type == SquareType.Empty || type == SquareType.Puppy_empty) {
				visitInfo.put(hashCode, true);
			} else if (type == SquareType.Mower || type == SquareType.Puppy_mower) {
				visitInfo.put(hashCode, true);
			}
		}
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
	 * If a (x,y) coordinate is a {@link SquareType#Grass} square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return True or false
	 */
	private boolean isGrass(int x, int y) {
		int hashCode = calculateHashCode(x, y);
		return scanInfo.isGrass(hashCode);
	}

	/**
	 * If a (x,y) coordinate is a {@link SquareType#Puppy_grass} square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return True or false
	 */
	private boolean isPuppyGrass(int x, int y) {
		int hashCode = calculateHashCode(x, y);
		return scanInfo.isPuppyGrass(hashCode);
	}

	/**
	 * If a (x,y) coordinate is a {@link SquareType#Mower} square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return True or false
	 */
	private boolean isMower(int x, int y) {
		int hashCode = calculateHashCode(x, y);
		return scanInfo.isMower(hashCode);
	}

	/**
	 * If a (x,y) coordinate is a {@link SquareType#Puppy_empty} or
	 * {@link SquareType#Puppy_mower} square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return True or false
	 */
	private boolean occupiedByPuppy(int x, int y) {
		int hashCode = calculateHashCode(x, y);
		return scanInfo.occupiedByPuppy(hashCode);
	}

	/**
	 * If a (x,y) coordinate has been visited by any lawnmower before
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            THe y coordinate
	 * @return True or false
	 */
	private boolean visited(int x, int y) {
		int hashCode = calculateHashCode(x, y);
		return visitInfo.hasVisited(hashCode);
	}

	/**
	 * Returns the current direction
	 * 
	 * @return The current {@link Direction}
	 */
	public Direction currentDirection() {
		return currentDirection;
	}

	/**
	 * Determines the next move
	 * 
	 * @return The next move action {@link MowerAction}
	 */
	public MowerAction decidesNextAction() {
		boolean powerOff = false;
		int frontX = getRelativeX(1, currentDirection);
		int frontY = getRelativeY(1, currentDirection);
		int frontHashCode = calculateHashCode(frontX, frontY);

		if (!scanInfo.hasKey(frontHashCode) || !knowAllEightSurrounding()) {
			action = new MowerAction(MowerActionType.Scan);
		} else if (backTracking) {
			if (scanInfo.getSquaresToExplore() == 0) {
				powerOff = true;
			} else {
				int x = getRelativeX(1, currentDirection);
				int y = getRelativeY(1, currentDirection);

				if (isMower(x, y) || occupiedByPuppy(x, y)) {
					action = new MowerAction(MowerActionType.Scan);
				} else {
					int steps = pathSteps.peek();

					if (steps > 1) {
						x = getRelativeX(2, currentDirection);
						y = getRelativeY(2, currentDirection);

						if (isMower(x, y) || occupiedByPuppy(x, y)) {
							action = new MowerAction(MowerActionType.Scan);
						} else {
							pathSteps.pop();
							action = new MowerAction(MowerActionType.Move, steps, currentDirection);
						}
					} else {
						pathSteps.pop();
						action = new MowerAction(MowerActionType.Move, steps, currentDirection);
					}
				}
			}
		} else if (isGrass(frontX, frontY) && !visited(frontX, frontY)) {
			int endX = frontX;
			int endY = frontY;
			Direction currentDir = currentDirection;
			int front2X = getRelativeX(2, currentDir);
			int front2Y = getRelativeY(2, currentDir);

			boolean canMove2 = isGrass(front2X, front2Y) && !visited(front2X, front2Y);
			int steps = 1;
			if (canMove2) {
				steps = 2;
				endX = front2X;
				endY = front2Y;
			}

			boolean deadEnd = knowInAdvanceArrivingAtDeadEnd(endX, endY);
			boolean cannotGoStraightFurther = knowInAdvanceCannotGoStraightFurther(endX, endY);
			Direction newDir = currentDir;

			if (deadEnd && scanInfo.getSquaresToExplore() > 0) {
				newDir = getReverseDirection(currentDir);
			} else if (cannotGoStraightFurther) {
				List<Direction> surroundingDirections = getEightDirections();
				Direction puppyGrassDir = null;
				for (Direction dir : surroundingDirections) {
					int x = getRelativeX(endX, 1, dir);
					int y = getRelativeY(endY, 1, dir);

					if (isGrass(x, y) && !visited(x, y)) {
						newDir = dir;
						break;
					} else if (isPuppyGrass(x, y) && !visited(x, y)) {
						puppyGrassDir = dir;
					}
				}

				if (newDir == currentDir && puppyGrassDir != null) {
					newDir = puppyGrassDir;
					onlyPuppyGrassSquareAvailable = true;
				}
			}

			pathDirections.push(currentDir);

			action = new MowerAction(MowerActionType.Move, steps, newDir);
		} else {
			if (isPuppyGrass(frontX, frontY) && !visited(frontX, frontY) && onlyPuppyGrassSquareAvailable) {
				action = new MowerAction(MowerActionType.Move, 1, currentDirection);
				pathDirections.push(currentDirection);
			} else {
				List<Direction> surroundingDirections = getEightDirections();
				boolean foundGrass = false;
				Direction puppyGrassDir = null;

				for (Direction dir : surroundingDirections) {
					int x = getRelativeX(1, dir);
					int y = getRelativeY(1, dir);

					if (isGrass(x, y) && !visited(x, y)) {
						foundGrass = true;
						action = new MowerAction(MowerActionType.Move, 0, dir);
						break;
					} else if (isPuppyGrass(x, y) && !visited(x, y)) {
						puppyGrassDir = dir;
					}
				}
				if (!foundGrass && puppyGrassDir != null) {
					if (currentDirection == puppyGrassDir) {
						action = new MowerAction(MowerActionType.Move, 1, currentDirection);
						pathDirections.push(currentDirection);
					} else {
						onlyPuppyGrassSquareAvailable = true;
						action = new MowerAction(MowerActionType.Move, 0, puppyGrassDir);
					}
				} else if (!foundGrass) {
					if (scanInfo.getSquaresToExplore() == 0) {
						powerOff = true;
					} else if (pathDirections.isEmpty()) {
						action = new MowerAction(MowerActionType.Turn_off);
					} else {
						Direction dir = getReverseDirection(pathDirections.pop());

						if (dir == currentDirection) {
							int steps = pathSteps.pop();
							action = new MowerAction(MowerActionType.Move, steps, dir);
							backTracking = true;
						} else {
							action = new MowerAction(MowerActionType.Move, 0, dir);
							backTracking = true;
						}
					}
				}
			}

		}

		if (powerOff) {
			action = new MowerAction(MowerActionType.Turn_off);
		}

		printMowerAction();
		return action;
	}

	/**
	 * Prints the mower action
	 */
	private void printMowerAction() {
		System.out.println("mower," + (getId() + 1));
		if (action.getType() == MowerActionType.Scan) {
			System.out.println("scan");
		} else if (action.getType() == MowerActionType.Turn_off) {
			System.out.println("turn_off");
		} else {
			System.out.println("move," + action.getSteps() + "," + action.getNewDirection().getName().toLowerCase());
		}
	}

	/**
	 * Returns the reverse of the given direction
	 * 
	 * @param dir
	 *            The direction
	 * @return The reverse direction
	 */
	private Direction getReverseDirection(Direction dir) {
		if (dir == Direction.NORTH) {
			return Direction.SOUTH;
		} else if (dir == Direction.SOUTH) {
			return Direction.NORTH;
		} else if (dir == Direction.EAST) {
			return Direction.WEST;
		} else if (dir == Direction.WEST) {
			return Direction.EAST;
		} else if (dir == Direction.NORTH_EAST) {
			return Direction.SOUTH_WEST;
		} else if (dir == Direction.NORTH_WEST) {
			return Direction.SOUTH_EAST;
		} else if (dir == Direction.SOUTH_EAST) {
			return Direction.NORTH_WEST;
		} else if (dir == Direction.SOUTH_WEST) {
			return Direction.NORTH_EAST;
		} else {
			throw new RuntimeException("Unrecognized direction!");
		}
	}

	/**
	 * Moves to a new position
	 * 
	 * @param steps
	 *            The number of steps
	 * @param newDir
	 *            The new direction to turn at end of the move
	 */
	private void move(int steps, Direction newDir) {
		int currentX = currentPosition.getX();
		int currentY = currentPosition.getY();

		scanInfo.put(calculateHashCode(currentX, currentY), SquareType.Empty);
		for (int s = 1; s <= steps; s++) {
			int x = getRelativeX(1, currentDirection);
			int y = getRelativeY(1, currentDirection);
			int hashCode = calculateHashCode(x, y);
			visitInfo.put(hashCode, true);

			currentPosition = new Square(x, y);

			if (scanInfo.isGrass(hashCode) || scanInfo.isPuppyGrass(hashCode)) {
				scanInfo.decrementSquaresToExplore(1);
			}
			scanInfo.put(hashCode, SquareType.Empty);

			if (s == steps) {
				scanInfo.put(hashCode, SquareType.Mower);
			}
		}
		currentDirection = newDir;
	}

	/**
	 * Processes okay response from the simulator
	 */
	public void processOkResponse() {
		if (action.getType() == MowerActionType.Move) {
			if (action.getSteps() > 0) {
				if (backTracking) {
					backTracking = false;
				} else {
					pathSteps.push(action.getSteps());

					if (onlyPuppyGrassSquareAvailable) {
						onlyPuppyGrassSquareAvailable = false;
					}
				}
			}
			move(action.getSteps(), action.getNewDirection());
		}
	}

	/**
	 * Processes crash response from the simulator; should never happen
	 */
	public void processCrashResponse() {
		// do nothing since it has been totally destroyed in a crash
	}

	/**
	 * Processed the stall response from the simulator
	 * 
	 * @param steps
	 *            The number of steps
	 */
	public void processStallResponse(int steps) {
		if (backTracking == true) {
			if (steps < action.getSteps()) {
				pathSteps.add(action.getSteps() - steps);
			} else {
				backTracking = false;
			}
		} else if (steps > 0) {
			pathSteps.push(steps);

			if (onlyPuppyGrassSquareAvailable) {
				onlyPuppyGrassSquareAvailable = false;
			}
		} else if (steps == 0) {
			pathDirections.pop();
		}

		move(steps, currentDirection);
		int hashCode = calculateHashCode(currentPosition.getX(), currentPosition.getY());
		scanInfo.put(hashCode, SquareType.Puppy_mower);
	}

	/**
	 * Returns the x coordinate after taking the given number of steps in the given
	 * direction
	 * 
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result x coordinate
	 */
	private int getRelativeX(int steps, Direction dir) {
		return getRelativeX(currentPosition.getX(), steps, dir);
	}

	/**
	 * Returns the y coordinate after taking the given number of steps in the given
	 * direction
	 * 
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result y coordinate
	 */
	private int getRelativeY(int steps, Direction dir) {
		return getRelativeY(currentPosition.getY(), steps, dir);
	}

	/**
	 * Returns the x coordinate after taking the given number of steps in the given
	 * direction from the given x starting point
	 * 
	 * @param startingX
	 *            The starting x coordinate
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result x coordinate
	 */
	private int getRelativeX(int startingX, int steps, Direction dir) {
		int x = startingX;
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
	 * @param startingX
	 *            The starting x coordinate
	 * @param steps
	 *            The number of steps
	 * @param dir
	 *            The direction {@link Direction} for moving
	 * @return The result x coordinate
	 */
	private int getRelativeY(int startingY, int steps, Direction dir) {
		int y = startingY;
		if (dir == Direction.NORTH || dir == Direction.NORTH_EAST || dir == Direction.NORTH_WEST) {
			y -= steps;
		} else if (dir == Direction.SOUTH_WEST || dir == Direction.SOUTH_EAST || dir == Direction.SOUTH) {
			y += steps;
		}
		return y;
	}

	/**
	 * Calculates a hashcode of (x,y) coordinate
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @return The hashcode
	 */
	private int calculateHashCode(int x, int y) {
		String coordinate = "(" + x + "," + y + ")";
		return coordinate.hashCode();
	}

	/**
	 * Determines if we are arriving at a dead end at the given position (etc all 8
	 * surrounding squares are a) obstacles, b) visited grass square, c) mowers)
	 * 
	 * @param startingX
	 *            The starting x coordinate
	 * @param startingY
	 *            The starting y coordinate
	 * @return True or false
	 */
	private boolean knowInAdvanceArrivingAtDeadEnd(int startingX, int startingY) {
		boolean knowAllSurrounding = knowAllEightSurrounding(startingX, startingY);

		if (knowAllSurrounding) {
			List<Direction> surroundingDirections = getEightDirections();

			boolean deadEnd = true;
			boolean puppyGrass = false;
			for (Direction dir : surroundingDirections) {
				int x = getRelativeX(startingX, 1, dir);
				int y = getRelativeY(startingY, 1, dir);

				if (isGrass(x, y) && !visited(x, y)) {
					deadEnd = false;
					break;
				} else if (isPuppyGrass(x, y) && !visited(x, y)) {
					puppyGrass = true;
				}
			}

			if (deadEnd) {
				deadEnd = (puppyGrass == false);
			}
			return deadEnd;
		}
		return false;
	}

	/**
	 * Determines if we know we cannot go any further in the front direction from
	 * given starting position
	 * 
	 * @param startingX
	 *            The starting x coordinate
	 * @param startingY
	 *            The starting y coordinate
	 * @return True or false
	 */
	private boolean knowInAdvanceCannotGoStraightFurther(int startingX, int startingY) {
		int x = getRelativeX(startingX, 1, currentDirection);
		int y = getRelativeY(startingY, 1, currentDirection);

		if (!scanInfo.hasKey(calculateHashCode(x, y))) {
			return false;
		}
		return (isGrass(x, y) && !visited(x, y)) == false;
	}

	/**
	 * Determines if scan repository information about all 8 surrounding lawn
	 * squares
	 * 
	 * @return True or false
	 */
	private boolean knowAllEightSurrounding() {
		return knowAllEightSurrounding(currentPosition.getX(), currentPosition.getY());
	}

	private boolean knowAllEightSurrounding(int startingX, int startingY) {
		List<Direction> surroundingDirections = getEightDirections();

		boolean knowAll = true;
		for (Direction dir : surroundingDirections) {
			int x = getRelativeX(startingX, 1, dir);
			int y = getRelativeY(startingY, 1, dir);

			if (!scanInfo.hasKey(calculateHashCode(x, y))) {
				knowAll = false;
				break;
			}
		}
		return knowAll;
	}
}
