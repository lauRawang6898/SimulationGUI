package osmowsis.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * The shared repository for scan information from all lawn mowers
 * 
 * @author Laura  Wang
 *
 */
public class SharedScanInfoRepository {
	
	private Map<Integer, SquareType> scanInfo = new HashMap<Integer, SquareType>();
	
	private static SharedScanInfoRepository repo;
	
	/**
	 * Number of squares yet to be explored
	 */
	private int squaresToExplore = 0;
	
	/**
	 * Returns the static singleton instance
	 * @return The singleton repository
	 */
	public static SharedScanInfoRepository getInstance() {
		if(repo == null) {
			repo = new SharedScanInfoRepository();
		}
		return repo;
	}
	
	/**
	 * Increment number of squares to explore
	 */
	public synchronized void incrementSquareToExplore() {
		squaresToExplore +=1;
	}
	
	/**
	 * Return the number of squares yet to be explored
	 * @return The number of squares
	 */
	public synchronized int getSquareToExplore() {
		return squaresToExplore;
	}
	
	/**
	 * Decreases the number of squares to explore
	 * @param n The decrement amount
	 */
	public synchronized void decrementSquaresToExplore(int n) {
		squaresToExplore -= n;
	}
	
	/**
	 * Returns the number of square yet to be explored
	 * @return The number
	 */
	public synchronized int getSquaresToExplore() {
		return squaresToExplore;
	}

	/**
	 * Updates the repository
	 * @param hashCode The hashcode of the (x,y) coordinate
	 * @param squareType The {@link SquareType}
	 */
	public synchronized void put(Integer hashCode, SquareType squareType) {
		scanInfo.put(hashCode, squareType);
	}

	/**
	 * If the repository has the given key
	 * @param key THe given hashcode key of (x,y) coordinate
	 * @return True or false
	 */
	public synchronized boolean hasKey(int key) {
		return scanInfo.containsKey(key);
	}

	/**
	 * If the given (x,y) coordinate hashcode corresponds to a {@link SquareType#Grass} lawn square
	 * @param key The given (x,y) coordinate hash
	 * @return True or false
	 */
	public synchronized boolean isGrass(int key) {
		return scanInfo.containsKey(key) && scanInfo.get(key)== SquareType.Grass;
	}
	
	/**
	 * If the given (x,y) coordinate hashcode corresponds to a {@link SquareType#Empty} lawn square
	 * @param key The given (x,y) coordinate hash
	 * @return True or false
	 */
	public synchronized boolean isEmpty(int key) {
		return scanInfo.containsKey(key) && scanInfo.get(key)==SquareType.Empty;
	}
	
	/**
	 * If the given (x,y) coordinate hashcode corresponds to a {@link SquareType#Mower} lawn square
	 * @param key The given (x,y) coordinate hash
	 * @return True or false
	 */
	public synchronized boolean isMower(int key) {
		return scanInfo.containsKey(key) && scanInfo.get(key)==SquareType.Mower;
	}
	
	/**
	 * 
	 */
	public synchronized boolean isPuppyGrass(int key) {
		return scanInfo.containsKey(key) && scanInfo.get(key)==SquareType.Puppy_grass;
	}
	
	public synchronized boolean occupiedByPuppy(int key) {
		return scanInfo.containsKey(key) && (scanInfo.get(key) == SquareType.Puppy_empty || scanInfo.get(key) == SquareType.Puppy_mower);
	}
}
