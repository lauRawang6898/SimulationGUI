package osmowsis.misc;

/**
 * 
 * 
 * Encapsulates a square on the lawn
 * 
 * @author Laura  Wang
 *
 */
public class Square {

	private int positionX;
	private int positionY;
	
	public Square(int x, int y){
		positionX=x;
		positionY=y;
	}
	
	public int getX() {
		return positionX;
	}
	
	public int getY() {
		return positionY;
	}
	
	@Override
	public String toString() {
		return "(" + positionX + "," + positionY + ")";
	}
}
