package osmowsis.driver;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import osmowsis.gui.CanvasSquareState;
import osmowsis.gui.LawnCanvasJPanel;
import osmowsis.gui.StatusPanel;
import osmowsis.misc.Direction;
import osmowsis.misc.Square;
import osmowsis.misc.SquareType;
import osmowsis.model.lawnmower.LawnMower;
import osmowsis.model.lawnmower.MowerAction;
import osmowsis.model.lawnmower.MowerActionType;
import osmowsis.model.puppy.Puppy;
import osmowsis.model.puppy.PuppyAction;
import osmowsis.model.puppy.PuppyActionType;

/**
 * 
 * 
 * The Simulator model class
 * 
 * @author Laura  Wang
 *
 */
public class Simulator {

	private static Simulator sim;

	/**
	 * LawnLayout matrix
	 */
	private SquareType[][] lawnLayout;

	/**
	 * Height of the lawn
	 */
	private int lawnHeight;

	/**
	 * Width of the lawn
	 */
	private int lawnWidth;

	/**
	 * Number of craters
	 */
	private int craterCount;

	/**
	 * Number of grass cut so far
	 */
	private int grassCutCount;

	/**
	 * Number of turns
	 */
	private int turnCount;

	/**
	 * List of mowers
	 */
	private List<LawnMower> mowers = new ArrayList<LawnMower>();

	/**
	 * List of puppies
	 */
	private List<Puppy> puppies = new ArrayList<Puppy>();

	/**
	 * Locations of each mower, the index is the id of the mower (0,1,2...)
	 */
	private Square[] mowerLocations;

	/**
	 * Locations of each puppy, the index is the id of the puppy(0,1,2...)
	 */
	private Square[] puppyLocations;

	/**
	 * Ids of the crashed mowers
	 */
	private List<Integer> crashedMowerIds = new ArrayList<Integer>();

	/**
	 * Ids of the stalled mowers
	 */
	private List<Integer> stalledMowerIds = new ArrayList<Integer>();

	/**
	 * Ids of the turned-off mowers
	 */
	private List<Integer> poweredOffMowerIds = new ArrayList<Integer>();

	/**
	 * Current polling index for mower (etc 0, 1, 2)
	 */
	private int mowerPollingIndex = 0;

	/**
	 * The current polling index for puppy (etc 0, 1, 2)
	 */
	private int puppyPollingIndex = 0;

	/**
	 * Previous polling index for mower(etc which mower was polled previously)
	 */
	private int previousMowerPollingIndex = -1;

	/**
	 * Previous polling index for puppy (etc which puppy was polled previously)
	 */
	private int previousPuppyPollingIndex = -1;

	/**
	 * Number of stalled turns in mower-mower collision
	 */
	private int stallTurns;

	/**
	 * How many remaining stalled turns are there for any stalled map (in
	 * mower-mower case)
	 */
	private Map<Integer, Integer> stalledTurnsRemainingByMower = new HashMap<Integer, Integer>();

	private int maxTurns = 300;

	/**
	 * In any given poll, whether or not to poll the mower or puppy
	 */
	private boolean pollMower = true;

	/**
	 * Gets the singleton instance of the Simulator
	 * 
	 * @return
	 */
	public static Simulator getInstance() {
		if (sim == null) {
			sim = new Simulator();
		}
		return sim;
	}

	/**
	 * Increment the grass cut count
	 */
	public synchronized void incrementGrassCountCount() {
		grassCutCount += 1;
	}

	/**
	 * Increment the turn count
	 */
	public synchronized void incrementTurnCount() {
		turnCount += 1;
	}

	/**
	 * Init the simulation
	 * 
	 * @param file
	 *            The input file
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void initSimulation(String file) throws NumberFormatException, IOException {
		String filePath = System.getProperty("user.dir") + File.separator + file;
		FileInputStream stream = new FileInputStream(filePath);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

		// width & height
		int width = Integer.parseInt(reader.readLine().trim());
		int height = Integer.parseInt(reader.readLine().trim());

		// Simulator sets width & height of the lawn
		Simulator.getInstance().setWidthHeight(width, height);

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				lawnLayout[i][j] = SquareType.Grass;
			}
		}

		// init the mowers
		int lawnMowerCount = Integer.parseInt(reader.readLine().trim());
		grassCutCount = lawnMowerCount;
		mowerLocations = new Square[lawnMowerCount];

		stallTurns = Integer.parseInt(reader.readLine().trim());

		for (int i = 1; i <= lawnMowerCount; i++) {
			String mowerInfo = reader.readLine();
			String[] info = mowerInfo.split(",");
			int x = Integer.parseInt(info[0]);
			int y = convertY(Integer.parseInt(info[1]), height);
			String dir = info[2];

			// Initialize LawnMower with only the initial direction
			Square mowerPosition = new Square(x, y);
			LawnMower mower = new LawnMower(i - 1, Direction.getDirection(dir), mowerPosition);
			mowers.add(mower);
			mowerLocations[i - 1] = mowerPosition;
			lawnLayout[y][x] = SquareType.Mower;

		}

		// get crater positions
		int craterCount = Integer.parseInt(reader.readLine());

		List<String> craters = new ArrayList<String>();
		for (int i = 1; i <= craterCount; i++) {
			craters.add(reader.readLine());
		}

		initCraters(width, height, craterCount, craters);

		// init puppies
		int puppyNumber = Integer.parseInt(reader.readLine().trim());
		puppyLocations = new Square[puppyNumber];
		int stayPercentage = Integer.parseInt(reader.readLine().trim());
		for (int i = 1; i <= puppyNumber; i++) {
			String pupInfo = reader.readLine();
			String[] info = pupInfo.split(",");
			int x = Integer.parseInt(info[0]);
			int y = convertY(Integer.parseInt(info[1]), lawnHeight);
			Square square = new Square(x, y);
			Puppy pup = new Puppy(i - 1, stayPercentage, square);
			puppies.add(pup);
			puppyLocations[i - 1] = square;
			lawnLayout[y][x] = SquareType.Puppy_grass;
		}

		for (Puppy pup : puppies) {
			pup.setLawn(lawnLayout);
		}

		maxTurns = Integer.parseInt(reader.readLine().trim());

		LawnCanvasJPanel.getInstance().initCanvas(width, height, craters, mowers, puppies);

		int grassCount = lawnHeight * lawnWidth - craterCount;
		StatusPanel.getInstance().initStatusPanel(mowers, puppies, grassCount, maxTurns);
		reader.close();

		// timer to update the status panel
		Timer simpleTimer = new Timer(5, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				StatusPanel  statusPanel = StatusPanel.getInstance();
				statusPanel.updateObjectsPanel(crashedMowerIds, stalledMowerIds,
						stalledTurnsRemainingByMower, pollMower, mowerPollingIndex, puppyPollingIndex);
				
				int grassRemaining = lawnHeight * lawnWidth - craterCount - grassCutCount;
				statusPanel.updateSummaryPanel(grassCutCount, grassRemaining, maxTurns - turnCount);
			}

		});

		simpleTimer.start();
	}

	/**
	 * Run one simulation poll
	 * 
	 * @return
	 */
	public synchronized boolean run() {

		boolean endSimulation = false;

		if (pollMower && stalledMowerIds.size() == mowers.size()) {
			pollMower = false;
			pollPuppyAndPaint();
			endSimulation = shouldEndSimulation();
		}

		else if (pollMower) {
			LawnMower mower = mowers.get(mowerPollingIndex);

			// skip over stalled or crashed or poweredoff mowers
			while (stalledMowerIds.contains(mower.getId()) || crashedMowerIds.contains(mower.getId())
					|| poweredOffMowerIds.contains(mower.getId())) {
				mowerPollingIndex += 1;
				if (mowerPollingIndex < mowers.size()) {
					mower = mowers.get(mowerPollingIndex);
				} else {
					break;
				}
			}

			if (mowerPollingIndex == mowers.size()) {
				pollMower = false;
				mowerPollingIndex = 0;
				pollPuppyAndPaint();
			} else {
				pollMowerAndPaint();
			}

			endSimulation = shouldEndSimulation();

		} else if (!endSimulation) {
			pollPuppyAndPaint();
			endSimulation = shouldEndSimulation();
		}

		if (endSimulation) {
			printFinalReport();
		}

		for (Puppy pup : puppies) {
			pup.setLawn(lawnLayout);
		}

		return endSimulation;
	}

	/**
	 * Poll next puppy for action and paint
	 */
	private synchronized void pollPuppyAndPaint() {
		Puppy pup = puppies.get(puppyPollingIndex);
		PuppyAction act = pollForPuppyAction(pup);

		Square currentPupLocation = puppyLocations[pup.getId()];
		int currentPupX = currentPupLocation.getX();
		int currentPupY = currentPupLocation.getY();

		if (act.getType() == PuppyActionType.Stay) {
			sendOkayResponse(pup);
			LawnCanvasJPanel.getInstance().setPuppySquareText(currentPupX, currentPupY, (pup.getId() + 1) + "");
			LawnCanvasJPanel.getInstance().setActiveColor(currentPupX, currentPupY, Color.BLUE);
			LawnCanvasJPanel.getInstance().setActive(currentPupX, currentPupY, true);
		} else {
			Square newLocation = act.getDestination();
			int newPupX = newLocation.getX();
			int newPupY = newLocation.getY();

			CanvasSquareState withoutPuppyState = CanvasSquareState.Grass;
			SquareType sqrType = lawnLayout[currentPupY][currentPupX];

			if (sqrType == SquareType.Puppy_empty) {
				withoutPuppyState = CanvasSquareState.Empty;
				sqrType = SquareType.Empty;
			} else if (sqrType == SquareType.Puppy_mower) {
				sqrType = SquareType.Mower;
			} else if (sqrType == SquareType.Puppy_grass) {
				sqrType = SquareType.Grass;
			}

			// update lawn layout on the square puppy is currently on (puppy is about to
			// move)
			lawnLayout[currentPupY][currentPupX] = sqrType;

			for (LawnMower mower : mowers) {
				Square mowerPosition = mowerLocations[mower.getId()];

				int mowerX = mowerPosition.getX();
				int mowerY = mowerPosition.getY();

				if (mowerX == currentPupX && mowerY == currentPupY) {
					if (!poweredOffMowerIds.contains(mower.getId())) {
						int index = stalledMowerIds.indexOf(mower.getId());
						stalledMowerIds.remove(index);
					}
					withoutPuppyState = CanvasSquareState.valueOf("Mower" + mower.currentDirection().getName());
					break;
				}
			}

			// repaint the square the puppy is currently on
			LawnCanvasJPanel.getInstance().updateCanvasSquare(currentPupX, currentPupY, withoutPuppyState, false);

			sqrType = lawnLayout[newPupY][newPupX];

			CanvasSquareState newState = CanvasSquareState.Puppy;

			if (sqrType == SquareType.Grass) {
				sqrType = SquareType.Puppy_grass;
				newState = CanvasSquareState.PuppyGrass;
			} else if (sqrType == SquareType.Mower) {
				sqrType = SquareType.Puppy_mower;
			} else if (sqrType == SquareType.Empty) {
				sqrType = SquareType.Puppy_empty;
			}

			// update lawn layout at the destination square for the puppy move
			lawnLayout[newPupY][newPupX] = sqrType;

			for (LawnMower mower : mowers) {
				Square mowerPosition = mowerLocations[mower.getId()];

				int mowerX = mowerPosition.getX();
				int mowerY = mowerPosition.getY();

				if (mowerX == newPupX && mowerY == newPupY) {
					if (!poweredOffMowerIds.contains(mower.getId())) {
						stalledMowerIds.add(mower.getId());
					}
					LawnCanvasJPanel.getInstance().setPuppySquareText(newPupX, newPupY, (pup.getId() + 1) + "");
					newState = CanvasSquareState.valueOf("PuppyMower" + mower.currentDirection().getName());
					break;
				}
			}

			sendOkayResponse(pup);

			puppyLocations[pup.getId()] = newLocation;

			// repaint the destination square for puppy move
			LawnCanvasJPanel.getInstance().setPuppySquareText(newPupX, newPupY, (pup.getId() + 1) + "");
			LawnCanvasJPanel.getInstance().setActiveColor(newPupX, newPupY, Color.BLUE);
			LawnCanvasJPanel.getInstance().updateCanvasSquare(newPupX, newPupY, newState, true);
		}

		// un-highlight the square the previously polled puppy occupied
		if (previousPuppyPollingIndex != -1) {
			Puppy previousPup = puppies.get(previousPuppyPollingIndex);
			Square sqr = puppyLocations[previousPup.getId()];
			int x = sqr.getX();
			int y = sqr.getY();
			LawnCanvasJPanel.getInstance().setActive(x, y, false);
		}

		// if we are starting polling for puppy, un-highlight the square for the last
		// polled mower; if that mower occupies the same square, then don't un-highlight
		if (puppyPollingIndex == 0 && previousMowerPollingIndex != -1) {
			LawnMower previousMower = mowers.get(previousMowerPollingIndex);
			Square sqr = mowerLocations[previousMower.getId()];
			int x = sqr.getX();
			int y = sqr.getY();

			boolean active = false;

			Square pupLocation = puppyLocations[pup.getId()];
			int pupX = pupLocation.getX();
			int pupY = pupLocation.getY();

			if (x == pupX && y == pupY) {
				active = true;
				LawnCanvasJPanel.getInstance().setActiveColor(x, y, Color.BLUE);
			}

			LawnCanvasJPanel.getInstance().setActive(x, y, active);
			previousMowerPollingIndex = -1;
		}
		previousPuppyPollingIndex = puppyPollingIndex;

		puppyPollingIndex += 1;

		if (puppyPollingIndex == puppies.size()) {
			puppyPollingIndex = 0;
			pollMower = true;

			// increment turn count
			incrementTurnCount();

			// should the mower be "free" after stalling for # of turns (mower-mower case)
			for (LawnMower mower : mowers) {
				if (stalledTurnsRemainingByMower.containsKey(mower.getId())) {
					int turnsRemaining = stalledTurnsRemainingByMower.get(mower.getId());
					turnsRemaining -= 1;

					if (turnsRemaining == 0) {
						int index = stalledMowerIds.indexOf(mower.getId());
						stalledMowerIds.remove(index);
						stalledTurnsRemainingByMower.remove(mower.getId());
					} else {
						stalledTurnsRemainingByMower.put(mower.getId(), turnsRemaining);
					}
				}
			}

		}

	}

	/**
	 * Poll next mower for action and paint
	 */
	private synchronized void pollMowerAndPaint() {
		LawnMower mower = mowers.get(mowerPollingIndex);

		// highlight canvas UI square the currently polled mower is on
		Square sqr = mowerLocations[mower.getId()];
		int x = sqr.getX();
		int y = sqr.getY();
		LawnCanvasJPanel.getInstance().setMowerSquareText(x, y, (mower.getId() + 1) + "");
		LawnCanvasJPanel.getInstance().setActiveColor(x, y, Color.RED);
		LawnCanvasJPanel.getInstance().setActive(x, y, true);

		// poll for mower action
		MowerAction act = pollForMowerAction(mower);

		// process mower action
		if (act.getType() == MowerActionType.Turn_off) {
			sendOkayResponse(mower);
			poweredOffMowerIds.add(mower.getId());

			LawnCanvasJPanel.getInstance().setMowerSquareText(x, y, (mower.getId() + 1) + ": Off");
			LawnCanvasJPanel.getInstance().repaint(x, y);
		} else if (act.getType() == MowerActionType.Scan) {
			List<String> scanResult = respondToScan();
			sendScanResponse(mower, scanResult);
		} else {
			int stepsTaken = processMoveAction(act);

			if (crashedMowerIds.contains(mower.getId())) {
				sendCrashResponse(mower);
			} else if (stalledMowerIds.contains(mower.getId())) {
				sendStallResponse(mower, stepsTaken);
			} else {
				sendOkayResponse(mower);
			}
		}

		// un-highlight the canvas UI square the previously polled mower was on
		if (previousMowerPollingIndex != -1) {
			LawnMower previousMower = mowers.get(previousMowerPollingIndex);
			sqr = mowerLocations[previousMower.getId()];
			x = sqr.getX();
			y = sqr.getY();
			LawnCanvasJPanel.getInstance().setActive(x, y, false);
		}

		// when starting to poll for mower, un-highlight the square for previously
		// polled puppy; if that puppy occupies the same square, then don't un-highlight
		if (mowerPollingIndex >= 0 && previousPuppyPollingIndex != -1) {
			Puppy previousPup = puppies.get(previousPuppyPollingIndex);
			sqr = puppyLocations[previousPup.getId()];
			x = sqr.getX();
			y = sqr.getY();

			boolean active = false;

			Square mowerSquare = mowerLocations[mower.getId()];
			int mowerX = mowerSquare.getX();
			int mowerY = mowerSquare.getY();

			if (mowerX == x && mowerY == y) {
				active = true;
				LawnCanvasJPanel.getInstance().setActiveColor(x, y, Color.RED);
			}

			LawnCanvasJPanel.getInstance().setActive(x, y, active);
			previousPuppyPollingIndex = -1;
		}

		previousMowerPollingIndex = mowerPollingIndex;

		mowerPollingIndex += 1;
		if (mowerPollingIndex == mowers.size()) {
			mowerPollingIndex = 0;
			pollMower = false;
		}
	}

	/**
	 * Poll for mower action
	 * 
	 * @param mower
	 *            The mower {@link LawnMower} to poll
	 * @return The {@link MowerAction}
	 */
	public MowerAction pollForMowerAction(LawnMower mower) {
		return mower.decidesNextAction();
	}

	/**
	 * Poll for puppy action
	 * 
	 * @param puppy
	 *            The puppy {@link Puppy} to poll
	 * @return The {@link PuppyAction}
	 */
	public PuppyAction pollForPuppyAction(Puppy puppy) {
		return puppy.decidesNextAction();
	}

	/**
	 * Send over the okay response to the mower
	 * 
	 * @param mower
	 *            The currently polled mower {@link LawnMower}
	 */
	public void sendOkayResponse(LawnMower mower) {
		System.out.println("ok");
		mower.processOkResponse();
	}

	/**
	 * Sends the okay response to the puppy
	 * 
	 * @param pup
	 *            The {@link Puppy} that is currently being polled
	 */
	public void sendOkayResponse(Puppy pup) {
		System.out.println("ok");
		pup.processOkayResponse();
	}

	/**
	 * Send over scan response to the mower
	 * 
	 * @param mower
	 *            The currently polled mower {@link LawnMower}
	 * @param scanResult
	 *            The scan response
	 */
	public void sendScanResponse(LawnMower mower, List<String> scanResult) {
		printScanResponse(scanResult);
		mower.updateSharedScanInfo(scanResult);
	}

	/**
	 * Send over stall response to the mower
	 * 
	 * @param mower
	 *            The currently polled mower {@link LawnMower}
	 * @param steps
	 *            The number of steps
	 */
	public void sendStallResponse(LawnMower mower, int steps) {
		System.out.println("stall," + steps);
		mower.processStallResponse(steps);
	}

	/**
	 * Send over crash response to the mower; should never happen
	 * 
	 * @param mower
	 *            The currently polled mower {@link LawnMower}
	 */
	public void sendCrashResponse(LawnMower mower) {
		System.out.println("crash");
		mower.processCrashResponse();
	}

	/**
	 * Prints the final simulation result
	 */
	public void printFinalReport() {
		StringBuffer result = new StringBuffer();
		int totalSquares = lawnWidth * lawnHeight;
		result.append(totalSquares);
		result.append(",");
		result.append(totalSquares - craterCount);
		result.append(",");
		result.append(grassCutCount);
		result.append(",");
		result.append(turnCount);
		System.out.println(result.toString());
	}

	/**
	 * Sets the width and height
	 * 
	 * @param width
	 *            The width of the lawn
	 * @param height
	 *            The height of the lawn
	 */
	private void setWidthHeight(int width, int height) {
		lawnWidth = width;
		lawnHeight = height;
		lawnLayout = new SquareType[height][width];
	}

	/**
	 * Inits the lawn landscape
	 * 
	 * @param width
	 *            The width of the lawn
	 * @param height
	 *            The height of the lawn
	 * @param craterNumber
	 *            The number of craters
	 * @param craters
	 *            List of crater positions
	 */
	private void initCraters(int width, int height, int craterNumber, List<String> craters) {
		craterCount = craterNumber;

		for (String crater : craters) {
			String[] numbers = crater.split(",");
			int x = Integer.parseInt(numbers[0]);
			int y = convertY(Integer.parseInt(numbers[1]), height);

			// Java convention of array indexing (1st value vertical, 2nd value horizontal)
			lawnLayout[y][x] = SquareType.Crater;
		}
	}

	/**
	 * Process the move action by the lawn mower
	 * 
	 * @param act
	 *            The move mower {@link MowerAction}
	 * @return The steps that can be safely taken
	 */
	private int processMoveAction(MowerAction act) {

		int steps = act.getSteps();
		LawnMower mower = mowers.get(mowerPollingIndex);
		Direction dir = mower.currentDirection();

		if (steps > 0) {
			steps = updateMowerPosition(steps, dir);
			if (!stalledMowerIds.contains(mower.getId()) && !crashedMowerIds.contains(mower.getId())) {
				String state = "Mower" + act.getNewDirection().getName();
				Square sqr = mowerLocations[mower.getId()];
				int x = sqr.getX();
				int y = sqr.getY();
				LawnCanvasJPanel.getInstance().setActiveColor(x, y, Color.RED);
				LawnCanvasJPanel.getInstance().updateCanvasSquare(x, y, CanvasSquareState.valueOf(state), true);
			}
		} else {
			String state = "Mower" + act.getNewDirection().getName();
			Square sqr = mowerLocations[mower.getId()];
			int x = sqr.getX();
			int y = sqr.getY();
			LawnCanvasJPanel.getInstance().setActiveColor(x, y, Color.RED);
			LawnCanvasJPanel.getInstance().updateCanvasSquare(x, y, CanvasSquareState.valueOf(state), true);
		}

		return steps;
	}

	/**
	 * Do a conversion for y coordinate to 0 based Java convention
	 * 
	 * @param y
	 *            The y coordinate
	 * @param lawnHeight
	 *            The height of the lawn
	 * @return The converted y coordinate
	 */
	private int convertY(int y, int lawnHeight) {
		return lawnHeight - 1 - y;
	}

	/**
	 * Updates mower position and update UI
	 * 
	 * @param steps
	 *            The steps mower wants to take
	 * @param direction
	 *            The direction mower is moving
	 * @return The number of steps safely taken
	 */
	private int updateMowerPosition(int steps, Direction direction) {
		int stepsTaken = steps;
		boolean endMove = false;
		boolean stalledByPuppy = false;
		boolean stalledByMower = false;

		LawnMower mower = mowers.get(mowerPollingIndex);

		for (int i = 1; i <= steps; i++) {
			Square sqr = mowerLocations[mower.getId()];
			int x = sqr.getX();
			int y = sqr.getY();
			int newX = x;
			int newY = y;

			switch (direction) {
			case NORTH:
				newY -= 1;
				break;
			case SOUTH:
				newY += 1;
				break;
			case EAST:
				newX += 1;
				break;
			case WEST:
				newX -= 1;
				break;
			case NORTH_EAST:
				newY -= 1;
				newX += 1;
				break;
			case NORTH_WEST:
				newY -= 1;
				newX -= 1;
				break;
			case SOUTH_EAST:
				newY += 1;
				newX += 1;
				break;
			case SOUTH_WEST:
				newY += 1;
				newX -= 1;
				break;
			default:
				throw new RuntimeException("Cannot recognize direction");
			}

			// crash into fence
			if (newY < 0 || newX < 0 || newY > lawnHeight - 1 || newX > lawnWidth - 1) {
				// draw an empty lawn canvas square at where the mower was (since mower is
				// moving and crashed)
				LawnCanvasJPanel.getInstance().updateCanvasSquare(x, y, CanvasSquareState.Empty, false);

				// updates internal fields
				lawnLayout[y][x] = SquareType.Empty;
				crashedMowerIds.add(mower.getId());
				endMove = true;
			}
			// crash into crater
			else if (lawnLayout[newY][newX] == SquareType.Crater) {
				// draw an empty lawn canvas square at where the mower was (since mower is
				// moving and crashed)
				LawnCanvasJPanel.getInstance().updateCanvasSquare(x, y, CanvasSquareState.Empty, false);

				// update internal fields
				lawnLayout[y][x] = SquareType.Empty;
				crashedMowerIds.add(mower.getId());
				endMove = true;
			}
			// bump into another mower
			else if (lawnLayout[newY][newX] == SquareType.Mower) {
				endMove = true;
				stalledByMower = true;
				stalledMowerIds.add(mower.getId());
				stalledTurnsRemainingByMower.put(mower.getId(), stallTurns);
			} else {
				// draw an empty lawn canvas square at where the mower was(since mower is
				// moving)
				LawnCanvasJPanel.getInstance().updateCanvasSquare(x, y, CanvasSquareState.Empty, false);

				lawnLayout[y][x] = SquareType.Empty;
				mowerLocations[mower.getId()] = new Square(newX, newY);

				if (lawnLayout[newY][newX] == SquareType.Grass || lawnLayout[newY][newX] == SquareType.Empty) {

					if (lawnLayout[newY][newX] == SquareType.Grass) {
						incrementGrassCountCount();
					}

					lawnLayout[newY][newX] = SquareType.Mower;

					// draw a mower on the new canvas square the mower is now on after moving
					String state = "Mower" + direction.getName();
					LawnCanvasJPanel.getInstance().setMowerSquareText(newX, newY, (mower.getId() + 1) + "");
					LawnCanvasJPanel.getInstance().setActiveColor(newX, newY, Color.RED);
					LawnCanvasJPanel.getInstance().updateCanvasSquare(newX, newY, CanvasSquareState.valueOf(state),
							true);
				} else if (lawnLayout[newY][newX] == SquareType.Puppy_grass
						|| lawnLayout[newY][newX] == SquareType.Puppy_empty) {

					if (lawnLayout[newY][newX] == SquareType.Puppy_grass) {
						incrementGrassCountCount();
					}

					lawnLayout[newY][newX] = SquareType.Puppy_mower;

					// draw a mower on the new canvas square the mower is now on after moving
					String state = "PuppyMower" + direction.getName();
					LawnCanvasJPanel.getInstance().setMowerSquareText(newX, newY, (mower.getId() + 1) + "");
					LawnCanvasJPanel.getInstance().setActiveColor(newX, newY, Color.RED);
					LawnCanvasJPanel.getInstance().updateCanvasSquare(newX, newY, CanvasSquareState.valueOf(state),
							true);

					stalledMowerIds.add(mower.getId());

					stalledByPuppy = true;
					endMove = true;
				}
			}
			if (endMove) {
				if (stalledByMower) {
					stepsTaken = i - 1;
				} else if (stalledByPuppy) {
					stepsTaken = i;
				} else {
					stepsTaken = i;
				}
				break;
			}
		}

		return stepsTaken;

	}

	/**
	 * Responds to scan request
	 * 
	 * @return The scan results
	 */
	private List<String> respondToScan() {
		List<String> scanResponse = new ArrayList<String>();
		scanResponse.add(getScanResult(Direction.NORTH));
		scanResponse.add(getScanResult(Direction.NORTH_EAST));
		scanResponse.add(getScanResult(Direction.EAST));
		scanResponse.add(getScanResult(Direction.SOUTH_EAST));
		scanResponse.add(getScanResult(Direction.SOUTH));
		scanResponse.add(getScanResult(Direction.SOUTH_WEST));
		scanResponse.add(getScanResult(Direction.WEST));
		scanResponse.add(getScanResult(Direction.NORTH_WEST));

		return scanResponse;
	}

	/**
	 * Prints the scan response
	 * 
	 * @param scanResponse
	 *            The scan reponse to print
	 */
	private void printScanResponse(List<String> scanResponse) {
		StringBuffer scan = new StringBuffer();
		for (int i = 0; i < scanResponse.size(); i++) {
			scan.append(scanResponse.get(i));

			if (i < scanResponse.size() - 1) {
				scan.append(",");
			}
		}
		System.out.println(scan);
	}

	/**
	 * Get the individual scan result in the given direction based on the polled
	 * mower's current position
	 * 
	 * @param direction
	 *            The direction to scan
	 * @return The scanned result
	 */
	private String getScanResult(Direction direction) {
		// x,y in Java convention (x vertical, y horizontal)
		LawnMower mower = mowers.get(mowerPollingIndex);
		Square sqr = mowerLocations[mower.getId()];
		int x = sqr.getX();
		int y = sqr.getY();

		switch (direction) {
		case NORTH:
			y -= 1;
			break;
		case SOUTH:
			y += 1;
			break;
		case EAST:
			x += 1;
			break;
		case WEST:
			x -= 1;
			break;
		case NORTH_EAST:
			y -= 1;
			x += 1;
			break;
		case NORTH_WEST:
			y -= 1;
			x -= 1;
			break;
		case SOUTH_EAST:
			y += 1;
			x += 1;
			break;
		case SOUTH_WEST:
			y += 1;
			x -= 1;
			break;
		default:
			throw new RuntimeException("Cannot recognize direction");
		}
		if (y < 0 || x < 0 || y > lawnHeight - 1 || x > lawnWidth - 1) {
			return SquareType.Fence.getName();
		}
		return lawnLayout[y][x].getName();
	}

	/**
	 * Should the simulation be ended
	 * 
	 * @return True or false
	 */
	public synchronized boolean shouldEndSimulation() {
		if (mowers.size() == crashedMowerIds.size()) {
			return true;
		} else if (poweredOffMowerIds.size() == mowers.size()) {
			return true;
		} else if (turnCount == maxTurns) {
			return true;
		} else if (poweredOffMowerIds.size() + crashedMowerIds.size() == mowers.size()) {
			return true;
		}
		return false;
	}
}
