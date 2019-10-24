package osmowsis.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * 
 * 
 * The GUI frame
 * 
 * @author Laura  Wang
 *
 */
public class GUIFrame extends JFrame {

	private static final long serialVersionUID = 1260226537214861L;
	
	private static GUIFrame frame;
	
	public static GUIFrame getFrame() {
		if(frame == null) {
			frame = new GUIFrame();
		}
		return frame;
	}
	
	public GUIFrame() {
		JPanel container = new JPanel();
		container.setLayout(new BorderLayout());
		LawnCanvasJPanel canvas = LawnCanvasJPanel.getInstance();
		container.add(canvas);
		
		JPanel toolAndStatus = new JPanel();
		toolAndStatus.setLayout(new BorderLayout());
		Toolbar tool = new Toolbar();
		toolAndStatus.add(tool, BorderLayout.NORTH);
		StatusPanel status = StatusPanel.getInstance();
		toolAndStatus.add(status, BorderLayout.CENTER);
		
		container.add(toolAndStatus, BorderLayout.EAST);
		add(container);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
