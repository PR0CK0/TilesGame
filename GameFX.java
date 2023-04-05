/*
 * Class: GameFX
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     EasyGameMode
 *     HardGameMode
 *     Timer
 * 
 * Called by classes:
 *     MenuFX
 *     GameMode
 *     EasyGameMode
 *     HardGameMode
 * 
 * Description:
 *     This class displays the game grids to the user. Ties in functionally with the 
 *     two game modes through the use of constructors.
 * 
 * Attrtibutes:
 *     private final Pane rootPane - This is used in order for the stage to be set by MenuFX;
 *                                   it also allows for the game's GridPane and the pane for 
 *                                   the timer to be fit into itself
 * 
 * Interesting Methods:
 *     startHardGameMode() - The method called from EasyGameMode, once over, to begin
 *                           HardGameMode... performs the same GameFX logic as the 
 *                           EasyGameMode section in GameFX does
 * 
 */


// Imports
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;


public class GameFX 
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */

	// The rootPane used to access GameFX's graphics from MenuFX
	private final Pane rootPane;
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// GameFX's constructor; talks with the game mode classes and also with MainFX
	public GameFX() 
	{
		// Set the rootPane
		rootPane = new Pane();
		
		// Form the GridPane that the game is based off of
		// Adjust the background, spacing between boxes, etc.
		GridPane gpGameGrid = new GridPane();
		gpGameGrid.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
		gpGameGrid.setHgap(5);
		gpGameGrid.setVgap(5);
		gpGameGrid.setPadding(new Insets(2, 2, 2, 2));
		gpGameGrid.setCursor(Cursor.HAND);
		
		// Create a Timer object, set its start time and place it properly in the rootPane
		// Pause it initially because it will start the round's time before the grid of tiles can be populated
		Timer timerClockEasy = new Timer(EasyGameMode.EASY_START_TIME);
		// Relocate the timer so it's roughly centered
		timerClockEasy.getRootPane().relocate(MenuFX.WINDOW_WIDTH/2 - 38, 10);
		// Pause the timer before the grid is displayed, otherwise the computation causes a small loss in the timer clock
		timerClockEasy.getTimeline().pause();
		
		// Create new EasyGameMode object and pass it THIS GameFX instance, the game's grid pane, as well as the timer object created above
		// This constructor is further defined in EasyGameMode, and its contents are heavily relied upon
		EasyGameMode easyGameObject = new EasyGameMode(this, gpGameGrid, timerClockEasy);
		
		// Relocate the gridpane in anticipation of the 5x5 grid game
		// Half the window W or H minus the gridpane's W or H (100px/tile, plus the 2px padding on each side = 500 + 4)
		// The Hgaps and Vgaps don't factor into the W or H
		// Still not exact, there is some minor hard-coding adjustment made for it to be centered
		gpGameGrid.relocate(MenuFX.WINDOW_WIDTH/2 - 510/2, MenuFX.WINDOW_HEIGHT/2 - 504/2);
		
		// Populate the 5x5 array, start the easy game mode timeline loop
		easyGameObject.randomlyPopulateGrid();
		easyGameObject.gameLoop();

		// Set the game's grid pane and the timer's VBox to the rootPane of GameFX
		// Acts the same functionally as .addAll, except it CLEARS what is already there, and sets the new stuff
		// This makes the game a little bit faster computationally
		rootPane.getChildren().setAll(gpGameGrid, timerClockEasy.getRootPane());

		// Now play the timeline for the timer clock after all of the objects have been populated and added to the grid
		// This minimizes the loss of time from the timer when a new round starts
		// It is noticeably worse on computers with older hardware, so this is a very important step to a fair game
		timerClockEasy.getTimeline().play();
	}
	
	
	// Method to begin hard game mode; called in EasyGameMode
	// Performs the same functions as with the easy game mode, but for the hard game mode
	public void startHardGameMode(GridPane gpGameGrid)
	{
		// Create a Timer object and set its start time, then center it
		Timer timerClockHard = new Timer(HardGameMode.HARD_START_TIME);
		timerClockHard.getRootPane().relocate(MenuFX.WINDOW_WIDTH/2 - 45, 0);
		// Pause to allow population and reduce timer clock lag
		timerClockHard.getTimeline().pause();
		
		// Create a new HardGameMode object and pass it THIS GameFX instance, the game's grid pane, as well as the timer object created above
		// This constructor's contents are heavily relied upon in HardGameMode
		HardGameMode hardGameObject = new HardGameMode(this, gpGameGrid, timerClockHard);
		
		// Properly position the 6x6 grid; same as the 5x5 grid, just 604 W and H (6 tiles, 100px each, plus 2px padding each side)
		// The Hgaps and Vgaps don't factor into the W or H
		// Not exact, minor adjustments made
		gpGameGrid.relocate(MenuFX.WINDOW_WIDTH/2 - 616/2, MenuFX.WINDOW_HEIGHT/2 - 604/2);
		
		// Populate the 6x6 array initially and start the hard game mode timeline loop
		hardGameObject.randomlyPopulateGrid();
		hardGameObject.gameLoop();

		// Set all elements of the 6x6 grid to the game's grid pane
		rootPane.getChildren().setAll(gpGameGrid, timerClockHard.getRootPane());
		
		// Now, play the timeline for the timer clock
		timerClockHard.getTimeline().play();
	}
	
	
	// Getter for rootPane
	public Pane getRootPane() 
	{
		return rootPane;
	}
	
	
}