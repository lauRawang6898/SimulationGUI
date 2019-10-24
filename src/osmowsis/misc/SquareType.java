package osmowsis.misc;

/**
 * 
 * 
 * The enum for lawn square type
 * 
 * @author Laura  Wang
 *
 */
public enum SquareType {
	
	Grass("grass"),
	Crater("crater"),
	Fence("fence"),
	Empty("empty"),
	Puppy_grass("puppy_grass"),
	Puppy_mower("puppy_mower"),
	Puppy_empty("puppy_empty"),
	Mower("mower");
	
	private String name;
	
	private SquareType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static SquareType fromName(String name) {
		for(SquareType type : SquareType.values()) {
			if(type.getName().equals(name)) {
				return type;
			}
		}
		throw new RuntimeException("Invalid squareType name!");
	}
}
