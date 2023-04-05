/*
 * Class: GameMode
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     GameFX
 *     Tile
 *     Timer
 * 
 * Called by classes:
 *     EasyGameMode
 *     HardGameMode
 * 
 * Description:
 *     This class acts as the parent class for EasyGameMode and HardGameMode. In it are the generic
 *     attributes and methods needed to complete the basic functions of each game mode. Almost 
 *     everything here will be overriden or not used in the game mode classes.
 * 
 * Attrtibutes:
 *     protected Tile[][] arrTiles - An array of tile objects that the user interacts with
 *     protected int iNumberOfWhiteTiles - The number of all the white tiles in a given grid
 *     protected int iNumberOfWhiteTilesClicked - The number of white tiles clicked by the user
 *                                                Used to check for completion
 *     protected int iNumberOfBlackTiles - Used to keep track of the number of black tiles
 *     protected int iRounds - Used to keep track of the number of rounds
 *     protected Timeline timelineGame - The game's timeline
 *     protected GridPane gpGame - Used for assigning the GridPane via the constructor used in GameFX
 *     protected Timer timerClock - Used for assigning the start time to a Timer object from the 
 *                                  constructor used in GameFX       
 *     protected GameFX gameFX - Object used for assigning the GameFX object via the constructor
 *                               used in GameFX                             
 * 
 * Interesting Methods:
 *     gameLoop() - Uses a JavaFX timeline to loop indefinitely, called at a rate ~= 60fps
 *     randomlyPopulateGrid() - Randomly populates a Tile array with tiles
 *     registerTileClick(Tile) - Called when a tile is clicked and handles each color as needed
 *     blackTileClick(Tile) - Method run when a black tile is clicked
 *     checkTimerEnd() - Called continuously as the end condition for the timeline in
 *                       startGameLoop(); checks for running out of time
 *                       
 */


// Imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class GameMode {
	
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Initialize a generic array of tiles
	protected Tile[][] arrTiles = new Tile[1][1];
	
	// Counter variables needed for any game mode
	protected int iNumberOfWhiteTiles;
	protected int iNumberOfWhiteTilesClicked;
	protected int iNumberOfBlackTiles;
	protected int iRounds;
	
	// Attribute used for a game loop timeline
	protected Timeline timelineGame = new Timeline();
	
	// Constructor reference variables
	protected GridPane gpGame = null;
	protected Timer timerClock = null;
	protected GameFX gameFX = null;
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// Set the constructor for use, taking in an instance of GameFX, its game gridpane, and a timer object
	public GameMode(GameFX gameFX, GridPane gpGame, Timer timerClock)
	{
		this.gameFX = gameFX;
		this.gpGame = gpGame;
		this.timerClock = timerClock;
	}
	
	
	// Generic method to instantiate a game's looping timeline and begin the game
	public void gameLoop()
	{
		// Set the game's timeline to be indefinite
		timelineGame.setCycleCount(Timeline.INDEFINITE);
		// Repeat the timeline cycle every 16ms, which equates to 1000ms / 16ms =~ 60fps
		// Every time it's enabled, call checkTimerEnd(), which checks for the game mode running out of time
		timelineGame.getKeyFrames().add(new KeyFrame(Duration.millis(16), e -> checkTimerEnd()));
		timelineGame.play(); 
	}
	
	
	// Generic method to randomly populate an array with colored tile objects
	public void randomlyPopulateGrid() throws ArrayIndexOutOfBoundsException, OutOfMemoryError
	{
		// Begin try catch block
		try
		{
			for(int i = 0; i < arrTiles.length; i++) 
			{
				for(int j = 0; j < arrTiles.length; j++) 
				{
					// RNG for tile selection
					double dRand = Math.random();
					
					// Create and adjust ImageView object to be used by created tiles
					ImageView view = new ImageView();
	
					if (dRand <= .25) 
					{
						// // Assign the white tile image to the ImageView
						view.setImage(Tile.IMG_WHITE_TILE);
						
						// Assign a white tile to the array index	
						arrTiles[i][j] = new Tile(TileColor.White, new Button(null, view));
					}
					
					else if (dRand <= .5) 
					{
						// Assign the black tile image to the ImageView and adjust its color
						view.setImage(Tile.IMG_BLACK_TILE);
						// Assign a black tile to the array index	
						arrTiles[i][j] = new Tile(TileColor.Black, new Button(null, view));
					}
					
					else if (dRand <= .75) 
					{
						// // Assign the green tile image to the ImageView
						view.setImage(Tile.IMG_GREEN_TILE);
						// Assign a green tile to the array index	
						arrTiles[i][j] = new Tile(TileColor.Green, new Button(null, view));
					}
					
					else 
					{
						// // Assign the red tile image to the ImageView
						view.setImage(Tile.IMG_RED_TILE);
						// Assign a red tile to the array index	
						arrTiles[i][j] = new Tile(TileColor.Red, new Button(null, view));
					}
				
					// Run through the array and add the created tile to the gridpane
					// Talks to GameFX through the constructor
					gpGame.add(arrTiles[i][j].getTileButton(), i, j);
					
					// Register tile clicks
					registerTileClick(arrTiles[i][j]);
				}
			}
		}
		
		// Catch exception if necessary- although it will never occur
		// Unless someone manually changes either for-loop's arguments to go out of bounds, this try-catch is irrelevant
		catch(ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Array has been referenced out-of-bounds. Check this method's encompassing loop.");
		}
		
		// Catch out of memory error- which should not occur unless the user is on the brink of their computer 
		// memory running out... but, as a 5x5 grid is being populated with images, it can take up a lot of memory
		// When testing, the game used on average 3.5% of my personal computer's memory, of which I have 16GB
		catch(OutOfMemoryError e)
		{
			System.out.println("You're out of memory! Clean up your poor computer!");
		}
	}
	
	
	// Generic method called through randomlyPopulateGrid that handles a click on whatever tile color is clicked
	public void registerTileClick(Tile tile) 
	{
		// If the tile clicked is white, handle accordingly
		if (tile.getTileColor() == TileColor.White) 
		{	
			tile.getTileButton().setOnAction(e -> 
			{
				// Add 1 to the number of white tiles clicked
				iNumberOfWhiteTilesClicked++;
				
				// Disable the button both visually and functionally (extremely important)
				// This prevents an already clicked tile being able to be clicked again
				tile.getTileButton().setDisable(true);
				
				// Console check for white tiles clicked
				System.out.println(iNumberOfWhiteTilesClicked);

			});	
		}
		
		// If the tile clicked is black, handle accordingly
		if (tile.getTileColor() == TileColor.Black) 
		{
			tile.getTileButton().setOnAction(e -> 
			{
				blackTileClick(tile);
			});
		}
		
		// If the tile clicked is green, disable the tile and update the timer clock
		if (tile.getTileColor() == TileColor.Green) 
		{
			// If the tile clicked is green, disable the tile and update the timer clock
			tile.getTileButton().setOnAction(e -> 
			{
				tile.getTileButton().setDisable(true);
				timerClock.setTimer(timerClock.getCurrentTime() + .5);
			});			
		}

		// If the tile clicked is red, disable the tile and update the timer clock
		if (tile.getTileColor() == TileColor.Red) 
		{
			tile.getTileButton().setOnAction(e -> 
			{
				// Disable tile and update the timer
				tile.getTileButton().setDisable(true);
				timerClock.setTimer(timerClock.getCurrentTime() - .5);
			});	
		}
	}

	
	// Generic method run on a black tile click to execute the steps needed because of user failure
	public void blackTileClick(Tile tile)
	{
		// Disable the button
		tile.getTileButton().setDisable(true);
		
		// Stop all timelines; the game loop and the timer clock loop
		timelineGame.stop();
		timerClock.getTimeline().stop();
		
		// Display failure
		System.out.println("Black tile clicked!");
	}
	
	
	// Generic method run continuously in any game mode to check for round failure via running out of time
	public void checkTimerEnd()
	{	
		// If a round's timer ever hits 0.0, inform the user through an alert and return to the main menu
		if (timerClock.getCurrentTime() <= 0.0)
		{
			// Set the visible timer to .04, which displays 0.0 in the game (because of rounding)
			// Otherwise, the game does not respond very well
			// Then, pause the timeline
			timerClock.setTimer(.04);
			timerClock.getTimeline().stop();
		
			// Display failure
			System.out.println("Out of time!");
		}
	}
	
	
}