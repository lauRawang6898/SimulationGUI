package osmowsis.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;

import osmowsis.model.lawnmower.LawnMower;
import osmowsis.model.puppy.Puppy;

/**
 * 
 * 
 * The LawnCanvasJPanel, the UI grid panel for the lawn
 * 
 * @author Laura  Wang
 *
 */
public class LawnCanvasJPanel extends JPanel {

	private static final long serialVersionUID = 8381791044630209342L;

	private CanvasSquare[][] canvasSquares;

	private static LawnCanvasJPanel canvas;

	public static LawnCanvasJPanel getInstance() {
		if (canvas == null) {
			canvas = new LawnCanvasJPanel();
		}
		return canvas;
	}

	/**
	 * Inits the UI panel
	 * 
	 * @param width
	 *            The width of the lawn
	 * @param height
	 *            The height of the lawn
	 * @param craters
	 *            The list of crater positions
	 * @param mowers
	 *            THe list of mowers (each with initial position)
	 */
	public void initCanvas(int width, int height, List<String> craters, List<LawnMower> mowers, List<Puppy> puppies) {

		canvasSquares = new CanvasSquare[height][width];
		setLayout(new GridLayout(height, width));

		for (String crater : craters) {
			String[] numbers = crater.split(",");
			int x = Integer.parseInt(numbers[0]);
			int y = height - Integer.parseInt(numbers[1]) - 1;
			canvasSquares[y][x] = new CanvasSquare(CanvasSquareState.Crater);
		}
		for (LawnMower mower : mowers) {
			int mowerX = mower.currentPostion().getX();
			int mowerY = mower.currentPostion().getY();
			String state = "Mower" + mower.currentDirection().getName();
			CanvasSquare mowerSquare = new CanvasSquare(CanvasSquareState.valueOf(state));
			mowerSquare.setMowerSquareText((mower.getId() + 1) + "");
			canvasSquares[mowerY][mowerX] = mowerSquare;
		}

		for (Puppy pup : puppies) {
			int pupX = pup.currentPostion().getX();
			int pupY = pup.currentPostion().getY();
			String state = "PuppyGrass";
			CanvasSquare pupSquare = new CanvasSquare(CanvasSquareState.valueOf(state));
			pupSquare.setPuppySquareText((pup.getId() + 1) + "");
			canvasSquares[pupY][pupX] = pupSquare;
		}

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				CanvasSquare sq = canvasSquares[i][j];
				if (sq == null) {
					sq = new CanvasSquare(CanvasSquareState.Grass);
					canvasSquares[i][j] = sq;
				}
				add(sq);
			}
		}
	}

	/**
	 * Updates a {@link CanvasSquare}. This would repaint the square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param newState
	 *            The new {@link CanvasSquareState}
	 * @param active
	 *            If the square is to be active or not (etc highlighted with a red
	 *            border)
	 */
	public void updateCanvasSquare(int x, int y, CanvasSquareState newState, boolean active) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.setState(newState);
		canvasSquare.setActive(active);
		canvasSquare.repaint();
	}

	/**
	 * Sets the active color for a canvas square (red or blue border).
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param color
	 *            The active border color
	 */
	public void setActiveColor(int x, int y, Color color) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.setActiveColor(color);
	}

	/**
	 * Sets the mower square text for the canvas square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param puppySquareText
	 *            The text
	 */
	public void setMowerSquareText(int x, int y, String mowerSquareText) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.setMowerSquareText(mowerSquareText);
	}

	/**
	 * Sets the puppy square text for the canvas square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param puppySquareText
	 *            The text
	 */
	public void setPuppySquareText(int x, int y, String puppySquareText) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.setPuppySquareText(puppySquareText);
	}

	/**
	 * Forces a repaint of the given canvas square
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public void repaint(int x, int y) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.repaint();
	}

	/**
	 * Sets a {@link CanvasSquare} to be active. This would repaint the square.
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param active
	 *            If the square is to be active or not
	 */
	public void setActive(int x, int y, boolean active) {
		CanvasSquare canvasSquare = canvasSquares[y][x];
		canvasSquare.setActive(active);
		canvasSquare.repaint();
	}
}