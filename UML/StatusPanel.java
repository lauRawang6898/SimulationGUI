package osmowsis.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

import osmowsis.model.lawnmower.LawnMower;
import osmowsis.model.puppy.Puppy;

/**
 * 
 * 
 * The StatusPanel for the simulation (etc simulation stats)
 * 
 * @author Laura  Wang
 *
 */
public class StatusPanel extends JPanel {

	private static final long serialVersionUID = 5374526368938674660L;

	private static StatusPanel statusPanel;
	
	private ObjectsPanel objectsPanel;
	
	private SummaryPanel summaryPanel;
	
	/**
	 * Returns the StatusPanel instance
	 * @return The StatusPanel
	 */
	public static StatusPanel getInstance() {
		if(statusPanel == null) {
			statusPanel = new StatusPanel();
		}
		return statusPanel;
	}
	
	/**
	 * Initialize the StatusPanel
	 * @param mowers List of mowers
	 * @param puppies List of puppies
	 */
	public void initStatusPanel(List<LawnMower> mowers, List<Puppy> puppies, int grassRemaining, int turnsRemaining) {
		setLayout(new BorderLayout());
		objectsPanel = new ObjectsPanel(mowers, puppies);
		add(objectsPanel, BorderLayout.NORTH);
		summaryPanel = new SummaryPanel(0, grassRemaining, turnsRemaining);
		add(summaryPanel, BorderLayout.CENTER);
	}
	
	/**
	 * Updates the StatuPanel
	 * @param crashedMowerIds List of crashed mower ids
	 * @param stalledMowerIds List of stalled mower ids (mower-mower & mower-puppy)
	 * @param stalledTurnsByMowerIds Remaining stalled turns by mower id (mower-mower collision)
	 * @param pollMower Whether or not simulator is polling a mower
	 * @param mowerPollingIndex The current polling index for mower
	 * @param puppyPollingIndex The current polling index for puppy
	 */
	public void updateObjectsPanel(List<Integer> crashedMowerIds, List<Integer> stalledMowerIds, Map<Integer, Integer> stalledTurnsByMowerIds, boolean pollMower, int mowerPollingIndex, int puppyPollingIndex) {
		objectsPanel.updateObjectsPanel(crashedMowerIds, stalledMowerIds, stalledTurnsByMowerIds, pollMower, mowerPollingIndex, puppyPollingIndex);
	}
	
	/**
	 * Updates teh StatusPanel
	 * @param grassCutSoFar Number of grass square cut so far
	 * @param grassRemaining Number of grass square remaining
	 * @param turnsRemaining Turns remaing
	 */
	public void updateSummaryPanel(int grassCutSoFar, int grassRemaining, int turnsRemaining) {
		summaryPanel.updateInfo(grassCutSoFar, grassRemaining, turnsRemaining);
	}
	
	/**
	 * Private ObjectsPanel for information display
	 * @author Laura  Wang
	 *
	 */
	private class ObjectsPanel extends JPanel{
		
		private static final long serialVersionUID = 2163209701559060751L;
		private JLabel[][]objectsGrid;
		private int mowerCount;
		
		private ObjectsPanel(List<LawnMower> mowers, List<Puppy> puppies) {
			GridLayout layout = new GridLayout(1+mowers.size() + puppies.size(), 4);
			layout.setHgap(5);
			setLayout(layout);
			mowerCount = mowers.size();
			
			objectsGrid = new JLabel[1+mowers.size() + puppies.size()][4];
			
			objectsGrid[0][0]= new JLabel("Object");
			objectsGrid[0][1] = new JLabel("Poll Next");
			objectsGrid[0][2] = new JLabel("Stalled Turns");
			objectsGrid[0][3] = new JLabel("Crashed");
			
			int y = 1;
			for(LawnMower mower : mowers) {
				int x = 0;
				while(x < 4) {
					JLabel label = new JLabel();
					label.setHorizontalTextPosition(JLabel.CENTER);
					if(x == 0) {
						label.setText("mower" + (mower.getId() + 1));
					}else if(x == 1) {
						boolean polledNext = mower.getId() == 0;
						label.setText(polledNext + "");
					} else if(x == 2) {
						label.setText("N/A");
					} else {
						label.setText("false");
					}
					objectsGrid[y][x] = label;
					x +=1;
				}
				
				y +=1;
			}
			
			y = 1 + mowers.size();
			for(Puppy puppy: puppies) {
				int x = 0;
				while(x <4 ) {
					JLabel label = new JLabel();
					label.setHorizontalTextPosition(JLabel.CENTER);
					if(x == 0) {
						label.setText("puppy" + (puppy.getId() + 1));
					}else if(x == 1) {
						label.setText("false");
					} else if(x == 2) {
						label.setText("N/A");
					} else {
						label.setText("N/A");
					}
					objectsGrid[y][x] = label;
					x +=1;
				}
				y +=1;
			}
			
			for(int i = 0; i< objectsGrid.length; i ++) {
				for(int j = 0; j< objectsGrid[i].length; j++) {
					add(objectsGrid[i][j]);
				}
			}
			
		}
		
		private void updateObjectsPanel(List<Integer> crashedMowerIds, List<Integer> stalledMowerIds, Map<Integer, Integer> stalledTurnsByMowerIds, boolean pollMower, int mowerPollingIndex, int puppyPollingIndex) {
			for(Integer crashed: crashedMowerIds) {
				int i = 1 + crashed;
				objectsGrid[i][2].setText("true");
			}
			for(Integer key : stalledTurnsByMowerIds.keySet()) {
				int i = 1 + key;
				int turns = stalledTurnsByMowerIds.get(key);
				objectsGrid[i][2].setText(turns + "");
			}
			
			for(Integer key : stalledMowerIds) {
				int i = 1 + key;
				if(!stalledTurnsByMowerIds.containsKey(i)) {
					objectsGrid[i][2].setText("Until puppy moves");
				}
			}
			
			for(int i = 1; i<=mowerCount; i++) {
				if(!stalledMowerIds.contains(i - 1)) {
					objectsGrid[i][2].setText("N/A");
				}
				
				if(pollMower) {
					boolean next = mowerPollingIndex == (i-1);
					objectsGrid[i][1].setText("" + next);
				}else {
					objectsGrid[i][1].setText("false");
				}
			}
			
			for(int i = 1 + mowerCount; i < objectsGrid.length; i++) {
				if(pollMower) {
					objectsGrid[i][1].setText("false");
				}else {
					boolean next = puppyPollingIndex == (i-1 - mowerCount);
					objectsGrid[i][1].setText("" + next);
				}
			}
			
			GUIFrame.getFrame().pack();
			
		}
	}
	
	private class SummaryPanel extends JPanel{

		private static final long serialVersionUID = 7909124228174157521L;
		private JLabel[][] summaryGrid;
		
		SummaryPanel(int grassCutSoFar, int grassRemaining, int turnsRemaining){
			
			GridLayout grid = new GridLayout(3,2);
			setLayout(grid);
			summaryGrid = new JLabel[3][2];
			
			for(int i = 0; i < 3; i++) {
				for(int j = 0; j < 2; j++) {
					JLabel label = new JLabel();
					label.setHorizontalTextPosition(JLabel.CENTER);
					
					if(i == 0) {
						if(j == 0) {
							label.setText("Grass Cut So Far: ");
						}else {
							label.setText("" + grassCutSoFar);
						}
					}else if(i==1) {
						if(j == 0) {
							label.setText("Grass Remaining: ");
						}else {
							label.setText("" + grassRemaining);
						}
					}else {
						if(j == 0) {
							label.setText("Turns Remaining: ");
						}else {
							label.setText("" + turnsRemaining);
						}
					}
					
					summaryGrid[i][j] = label;
					add(label);
				}
			}
		}
		
		private void updateInfo(int grassCutSoFar, int grassRemaining, int turnsRemaining) {			
			summaryGrid[0][1].setText("" + grassCutSoFar);
			summaryGrid[1][1].setText("" + grassRemaining);
			summaryGrid[2][1].setText("" + turnsRemaining);
		}
	}
}
