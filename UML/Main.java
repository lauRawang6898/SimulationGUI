package osmowsis.driver;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import osmowsis.gui.GUIFrame;

/**
 * CS6310 O01 Assignment6
 * 
 * Main class
 * 
 * @author Laura  Wang
 *
 */
public class Main {

	public static void main(String[] args) throws NumberFormatException, IOException {
		
		Simulator sim = Simulator.getInstance();
		sim.initSimulation(args[0]);

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				JFrame frame = GUIFrame.getFrame();
				frame.pack();
				frame.setVisible(true);
				
			}
			
		});
		
	}
}
