package osmowsis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 * 
 * The CanvasSquare, representing a canvas square on UI
 * 
 * @author Laura  Wang
 *
 */
public class CanvasSquare extends JPanel {

	private static final long serialVersionUID = 1660624289378283520L;

	/**
	 * The state of the square
	 */
	private CanvasSquareState state;

	/**
	 * If the canvas square is "active" (etc to be highlighted with a red border or
	 * blue border)
	 */
	private boolean active;

	/**
	 * Active square border color
	 */
	private Color color;

	/**
	 * Showing mower canvas square text
	 */
	private String mowerSquareText;

	/**
	 * Showing puppy canvas square text
	 */
	private String puppySquareText;

	/**
	 * Constructor
	 * 
	 * @param state
	 *            The square state
	 */
	public CanvasSquare(CanvasSquareState state) {
		super();
		setPreferredSize(new Dimension(100, 100));
		this.state = state;
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}

	/**
	 * Sets the state {@link CanvasSquareState}
	 * 
	 * @param state
	 *            The state
	 */
	public void setState(CanvasSquareState state) {
		this.state = state;
	}

	/**
	 * Sets the mower square text
	 * 
	 * @param mowerSquareText
	 *            The text
	 */
	public void setMowerSquareText(String mowerSquareText) {
		this.mowerSquareText = mowerSquareText;
	}

	/**
	 * Sets the puppy square text
	 * 
	 * @param puppySquareText
	 *            The text
	 */
	public void setPuppySquareText(String puppySquareText) {
		this.puppySquareText = puppySquareText;
	}

	/**
	 * Sets active or not
	 * 
	 * @param active
	 *            If the square is active or not
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Sets the active color
	 */
	public void setActiveColor(Color color) {
		this.color = color;
	}

	/**
	 * Paint the canvas square
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			Image image = state.getImage();

			Graphics imageGraphics = image.getGraphics();
			imageGraphics.setFont(imageGraphics.getFont().deriveFont(20F));

			if (state.isPuppyMower()) {
				imageGraphics.setColor(Color.RED);
				imageGraphics.drawString(mowerSquareText, 10, 60);
				imageGraphics.setColor(Color.BLUE);
				imageGraphics.drawString(puppySquareText, 10, 60 + imageGraphics.getFontMetrics().getHeight());
			} else if (state.isPuppyOnly()) {
				imageGraphics.setColor(Color.BLUE);
				imageGraphics.drawString(puppySquareText, 10, 60);
				imageGraphics.dispose();
			} else if (state.isMowerOnly()) {
				imageGraphics.setColor(Color.RED);
				imageGraphics.drawString(mowerSquareText, 10, 50);
				imageGraphics.dispose();
			}

			g.drawImage(image, 0, 0, null);

			if (state.name().startsWith("Mower")) {
				if (active) {
					setBorder(BorderFactory.createLineBorder(color, 4));
				} else {
					setBorder(BorderFactory.createLineBorder(Color.BLACK));
				}
			} else if (state.name().startsWith("Puppy")) {
				if (active) {
					setBorder(BorderFactory.createLineBorder(color, 4));
				} else {
					setBorder(BorderFactory.createLineBorder(Color.BLACK));
				}
			} else {
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
