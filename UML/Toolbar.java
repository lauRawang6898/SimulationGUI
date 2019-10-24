package osmowsis.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;
import javax.swing.Timer;

import osmowsis.driver.Simulator;

/**
 * 
 * 
 * The tool bar for the UI
 * 
 * @author Laura  Wang
 *
 */
public class Toolbar extends JToolBar {

	private static final long serialVersionUID = -5261480899581379323L;

	private Timer timer;
	private JButton next;
	private JButton forward;
	private JButton stop;

	public Toolbar() {
		next = createNextButton();
		add(next);

		addSeparator();

		forward = createForwardButton();
		add(forward);

		addSeparator();

		stop = createStopButton(next, forward);
		add(stop);
	}

	private JButton createStopButton(JButton next, JButton forward) {
		stop = new JButton("Stop");
		stop.setToolTipText("Stop simulation");
		stop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JOptionPane.showMessageDialog(null,
						"Simulation stoped. To run again, close and reinitialize with another file.", "Stop",
						JOptionPane.INFORMATION_MESSAGE);
				next.setEnabled(false);
				forward.setEnabled(false);
				stop.setEnabled(false);
				Simulator.getInstance().printFinalReport();
			}

		});
		return stop;
	}

	private JButton createForwardButton() {
		forward = new JButton("Fast Forward");
		forward.setToolTipText(
				"Click to fast forward toward the end of the simulation. This would disable all other buttons.");
		forward.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				JOptionPane.showMessageDialog(null, "Please wait util simulation stops.", "Please wait",
						JOptionPane.INFORMATION_MESSAGE);

				forward.setEnabled(false);
				next.setEnabled(false);
				stop.setEnabled(false);

				timer = new Timer(5, new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {

						boolean end = Simulator.getInstance().run();

						if (end) {
							timer.stop();
							
							JOptionPane.showMessageDialog(null,
									"Simulation ended. To run again, close and reinitialize with another file.", "End",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				});

				timer.start();

			}

		});
		return forward;
	}

	private JButton createNextButton() {
		next = new JButton("Next Poll");
		next.setToolTipText("Cick to poll next action.");
		next.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				next.setEnabled(false);
				boolean end = Simulator.getInstance().run();
				next.setEnabled(!end);
			}

		});
		return next;
	}
}
