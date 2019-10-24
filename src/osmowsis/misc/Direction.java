package osmowsis.misc;

/**
 * CS5310 O01 Assignment 6
 * 
 * Direction enum
 * 
 * @author Laura  Wang
 *
 */
public enum Direction {

	NORTH("North"),
	SOUTH("South"),
	EAST("East"),
	WEST("West"),
	NORTH_EAST("Northeast"),
	NORTH_WEST("Northwest"),
	SOUTH_EAST("Southeast"),
	SOUTH_WEST("Southwest");

	String directionName;

	Direction(String name) {
		directionName = name;
	}

	public String getName() {
		return directionName;
	}
	
	public static Direction getDirection(String name) {
		Direction result = null;
		for(Direction dir : values()) {
			if(dir.getName().equalsIgnoreCase(name)) {
				result = dir;
				break;
			}
		}
		return result;
	}
}
