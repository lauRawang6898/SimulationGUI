package osmowsis.misc;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 * The shared repository for visit information from all lawn mowers
 * 
 * @author Laura  Wang
 *
 */
public class SharedVisitInfoRepository {
	
	private Map<Integer, Boolean> visitInfo = new HashMap<Integer, Boolean>();
	
	private static SharedVisitInfoRepository repo;
	
	/**
	 * Returns the singleton repository instance
	 * @return The repository
	 */
	public static SharedVisitInfoRepository getInstance() {
		if(repo == null) {
			repo = new SharedVisitInfoRepository();
		}
		return repo;
	}

	/**
	 * Updates the repository
	 * @param hashCode The hashcode of (x,y) coordinate
	 * @param visit True or false
	 */
	public void put(Integer hashCode, Boolean visit) {
		visitInfo.put(hashCode, visit);
	}

	/**
	 * If the (x,y) coordinate represented by the given hashcode key has been visited by a lawn mower
	 * @param hashCode The coordinate hash
	 * @return True or false
	 */
	public boolean hasVisited(int hashCode) {
		return visitInfo.containsKey(hashCode) && visitInfo.get(hashCode);
	}
}
