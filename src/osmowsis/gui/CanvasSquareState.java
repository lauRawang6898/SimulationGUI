package osmowsis.gui;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 
 * 
 * The CanvasSquareState, state of a canvas square on UI
 * 
 * @author Laura  Wang
 *
 */
public enum CanvasSquareState {
	Crater(false, false, false),
	Empty(false, false, false),
	Grass(false, false, false),
	MowerEast(false, true, false),
	MowerNorth(false, true, false),
	MowerNortheast(false, true, false),
	MowerNorthwest(false, true, false),
	MowerSouth(false, true, false),
	MowerSoutheast(false, true,false),
	MowerSouthwest(false, true, false),
	MowerWest(false, true, false),
	Puppy(true, false, false),
	PuppyGrass(true, false, false),
	PuppyMowerNorth(false, false, true),
	PuppyMowerSouth(false, false, true),
	PuppyMowerNortheast(false, false, true),
	PuppyMowerNorthwest(false, false,true),
	PuppyMowerSoutheast(false, false, true),
	PuppyMowerSouthwest(false, false,true),
	PuppyMowerEast(false, false, true),
	PuppyMowerWest(false, false, true);

	private boolean isPuppyOnly;
	private boolean isMowerOnly;
	private boolean isPuppyMower;

	CanvasSquareState(boolean isPuppyOnly, boolean isMowerOnly, boolean both) {
		this.isPuppyOnly = isPuppyOnly;
		this.isMowerOnly = isMowerOnly;
		this.isPuppyMower = both;
	}

	Image getImage() throws IOException {
		return ImageIO.read(getClass().getResourceAsStream("/images/"+name() + ".png"));
	}

	boolean isPuppyOnly() {
		return isPuppyOnly;
	}

	boolean isMowerOnly() {
		return isMowerOnly;
	}

	boolean isPuppyMower() {
		return isPuppyMower;
	}
}
