/*
 * Class: EasyGameMode
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     GameMode
 *     MenuFX
 *     GameFX
 *     Tile
 *     Timer
 * 
 * Called by classes:
 *     GameFX
 * 
 * Description:
 *     This class performs the logic and realtime computation behind the easy, 5x5 grid rounds.
 *     It repeats its duties for five rounds before being cut off in GameFX. 
 * 
 * Attrtibutes:
 *     private int iEasyGridSize - Used to define the length and width of the 5x5 array (grid)
 *     public static final double EASY_START_TIME - Used to set the start time of the 5x5 rounds
 *     private int iEasyRoundsMax - Used to set the amount of rounds in the easy mode
 *     private Tile[][] arrTilesEasy- The array of tile objects that the user interacts with   
 *     
 *     All other attributes are inherited or modified from the parent class, i.e. iNumberOfWhiteTiles, 
 *     iNumberOfWhiteTilesClicked, iNumberOfBlackTiles, iRounds etc.
 * 
 * Interesting Methods:
 *     randomlyPopulateGrid() - Randomly populates the Tile array with tiles
 *     registerTileClick(Tile) - Called when a tile is clicked and handles each color as needed
 *     checkRoundCompletion() - Called when a white tile is clicked to check for round completion                     
 *     checkEasyModeCompletion() - Called when the rounds counter hits the fifth, and all white tiles 
 *     	                           are clicked... handles beating easy mode
 *     blackTileClick(Tile) - Method run when a black tile is clicked
 *     checkTimerEnd() - Called continuously as the end condition for the timeline in
 *                       gameLoop(); checks for running out of time
 *                       
 */


// Imports
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;


public class EasyGameMode extends GameMode
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Set the easy grid size, start time and the number of rounds before the hard game mode starts
	// Notice that the start time is a constant
	// I wanted 5 seconds initially, but my parents wanted 10 lol
	private int iEasyGridSize = 5;
	public static final double EASY_START_TIME = 10.0;
	private int iEasyRoundsMax = 5;
	
	// Initialize the array of tiles
	private Tile[][] arrTilesEasy = new Tile[iEasyGridSize][iEasyGridSize];
	
	// Set the counter variables needed to record and compare player progress
	// The two white tile counters are intentionally offset by 1, because if they are both 
	// the same value at the beginning, then the round is instantly won
	// The black tiles counter is to account for the case that no black tiles are created
	// Start counting the rounds at 1, to make the logic prettier
	private int iNumberOfWhiteTiles = 1;
	protected int iNumberOfWhiteTilesClicked = 0;
	private int iNumberOfBlackTiles = 0;
	private int iRounds = 1;

	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// Constructor used in GameFX
	// Takes in an instance of GameFX, its GridPane, as well as the created Timer object with the specified start time
	public EasyGameMode(GameFX gameFX, GridPane gpGame, Timer timerClock)
	{
		// Inheritance from the parent class, GameMode
		super(gameFX, gpGame, timerClock);
	}

	
	@Override
	// Method to randomly populate the array with colored tile objects
	// The if statements are intentionally specific, and not 1/4 for each
	// Approximates 11 white tiles, 9 black, 3 green and 2 red
	public void randomlyPopulateGrid() throws ArrayIndexOutOfBoundsException, OutOfMemoryError
	{		
		// Clear the game grid each time it is re-populated, so it is fresh
		// Otherwise, we get overlapping tiles
		gpGame.getChildren().clear();
		
		// Begin try catch block
		try
		{
			for(int i = 0; i < arrTilesEasy.length; i++) 
			{
				for(int j = 0; j < arrTilesEasy.length; j++) 
				{
					// RNG for tile selection
					double dRand = Math.random();
					
					// Create and adjust ImageView object to be used by created tiles
					ImageView imgvwTileImg = new ImageView();
					imgvwTileImg.setFitWidth(100);
					imgvwTileImg.setFitHeight(100);
		
					if (dRand <= .44) 
					{
						// Assign the white tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_WHITE_TILE);
						
						// Assign a white tile to the array index (uses image constant from Tile.java)	
						arrTilesEasy[i][j] = new Tile(TileColor.White, new Button(null, imgvwTileImg));
						
						// Add to number of white tiles counter
						iNumberOfWhiteTiles++;
					}
					
					else if (dRand <= .80) 
					{
						// Assign the black tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_BLACK_TILE);
						
						// Assign a black tile to the array index (uses image constant from Tile.java)	
						arrTilesEasy[i][j] = new Tile(TileColor.Black, new Button(null, imgvwTileImg));
					}
					
					else if (dRand <= .92) 
					{
						// Assign the green tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_GREEN_TILE);

						// Assign a green tile to the array index (uses image constant from Tile.java)
						arrTilesEasy[i][j] = new Tile(TileColor.Green, new Button(null, imgvwTileImg));
					}
					
					else
					{
						// Assign the red tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_RED_TILE);
						
						// Assign a red tile to the array index	(uses image constant from Tile.java)
						arrTilesEasy[i][j] = new Tile(TileColor.Red, new Button(null, imgvwTileImg));
					}
					
					// Run through the array and add the created tile to the gridpane
					// Talks to GameFX through the constructor
					gpGame.add(arrTilesEasy[i][j].getTileButton(), i, j);
					
					// Register tile clicks
					registerTileClick(arrTilesEasy[i][j]);
				}
			}	
			
			// The next two if statements are manual overrides in the very unlikely cases that either:
			// no white tiles spawn, no black tiles spawn, or both
			// Handles this case(s) by simply adding one tile type if needed
			if (iNumberOfWhiteTiles == 0) 
			{
				// Create a new imageview and assign it the white tile image constant from Tile.java
				ImageView imgvwNoWhiteTiles = new ImageView();
				imgvwNoWhiteTiles.setImage(Tile.IMG_WHITE_TILE);
				
				// Just give the grid one white tile, right in the center
				arrTilesEasy[2][2] = new Tile(TileColor.White, new Button(null, imgvwNoWhiteTiles));
			}
			
			if (iNumberOfBlackTiles == 0)
			{
				// Create a new imageview and assign it the black tile image constant from Tile.java
				ImageView imgvwNoBlackTiles = new ImageView();
				imgvwNoBlackTiles.setImage(Tile.IMG_BLACK_TILE);
				
				// Just give the grid one black tile, in the upper left corner
				arrTilesEasy[0][0] = new Tile(TileColor.Black, new Button(null, imgvwNoBlackTiles));
			}
		}
		
		// Catch array index exception if necessary- although it will never occur, unless someone changes the code
		catch(ArrayIndexOutOfBoundsException e)
		{
			// Print an error message
			System.out.println("Array has been referenced out-of-bounds. Check this method's encompassing loop.");
			
			// Return to main menu
			MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
		}
		
		// Catch out of memory error, which may occur on computer's with low memory, as tile population occurs
		catch(OutOfMemoryError e)
		{
			// Print an error message
			System.out.println("You're out of memory! Clean up your poor computer!");
			
			// Clear the game grid of all tile objects
			gpGame.getChildren().clear();
			
			// Return to main menu
			MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
		}
	}
	
	
	@Override
	// Method called through randomlyPopulateGrid that handles a click on whatever tile color is clicked
	public void registerTileClick(Tile tile) 
	{
		// If the tile clicked is white, handle accordingly
		if (tile.getTileColor() == TileColor.White) 
		{	
			tile.getTileButton().setOnAction(e -> 
			{
				// Add 1 to the number of white tiles clicked
				iNumberOfWhiteTilesClicked++;
				
				// Disable the button both visually and functionally 
				tile.getTileButton().setDisable(true);
				
				// TODO USED FOR TESTING
				// Console check for white tiles clicked
				//System.out.println(iNumberOfWhiteTilesClicked);
				
				// Method call to check for round completion
				// Refer to the method called for further detail
				checkRoundCompletion();
			});	
		}
		
		// If the tile clicked is black, handle accordingly (insta-fail)
		// Call to method blackTileClick(), which informs the user and returns to the main menu
		if (tile.getTileColor() == TileColor.Black) 
		{
			tile.getTileButton().setOnAction(e -> blackTileClick(tile));
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
	

	// Method to check for round completion by comparing the total number of white tiles in a grid to those clicked
	// Called every time a white tile is clicked
	public void checkRoundCompletion() 
	{
		// If the number of white tiles clicked = the number of white tiles in the grid on a given round, handle it accordingly
		if (iNumberOfWhiteTilesClicked == (iNumberOfWhiteTiles - 1)) 
		{
			// If five rounds have passed and all whites are clicked, call method to handle easy mode being completed
			// Always check this first, though- very important, otherwise an extra round will begin
			if (iRounds >= iEasyRoundsMax)
			{
				checkEasyModeCompletion();
			}
			
			// This block is only called if it isn't the fifth round
			else
			{
				// Stop the timer clock's timeline and alert the user of easy mode completion
				timerClock.getTimeline().stop();
				Alert allWhiteTilesClickedAlert = new Alert(AlertType.INFORMATION, "All white tiles clicked- round complete.");
				allWhiteTilesClickedAlert.setTitle("Next round incoming!");
				allWhiteTilesClickedAlert.setHeaderText(null);
				allWhiteTilesClickedAlert.setGraphic(null);
				allWhiteTilesClickedAlert.showAndWait();
				
				// Reset the number of white tiles and those clicked
				iNumberOfWhiteTiles = 1;
				iNumberOfWhiteTilesClicked = 0;
				// Populate again, reset start time and resume the timer clock
				randomlyPopulateGrid();
				timerClock.setTimer(EASY_START_TIME);
				timerClock.getTimeline().play();
				
				// Add 1 to rounds counter
				iRounds++;
			}
		}
	}
	
	
	// Method to handle easy game mode being completed...
	// Once five rounds are completed properly, easy game mode is over
	public void checkEasyModeCompletion()
	{
		// Pause the clock timer, then alert the user of easy game mode completion
		timerClock.getTimeline().pause();
		Alert easyDoneAlert = new Alert(AlertType.INFORMATION, "You have now completed easy mode... prepare for a survival round. Go for as long as you can, or fail. "
				+ "\n\nHint: Make sure to click your green tiles!");
		easyDoneAlert.setTitle("Easy game mode completed!");
		easyDoneAlert.setHeaderText(null);
		easyDoneAlert.setGraphic(null);
		easyDoneAlert.showAndWait();
		
		// Clear the easy game mode's clock and gridpane, in preparation for the hard game mode
		gpGame.getChildren().clear();
		timerClock.getRootPane().setVisible(false);
		
		// Stop the game's timeline
		timelineGame.stop();
		
		// Method call for GameFX to begin the HardGameMode
		gameFX.startHardGameMode(gpGame);
	}
	

	@Override
	// Method run when a black tile is clicked; displays an alert to the user and returns to the main menu
	public void blackTileClick(Tile tile) 
	{
		// Create alert
		Alert blackTileClickedAlert = new Alert(AlertType.WARNING, "A black tile was clicked... better luck next time.");
		blackTileClickedAlert.setTitle("You have failed!");
		blackTileClickedAlert.setHeaderText(null);
		blackTileClickedAlert.setGraphic(null);
		
		// Disable the button
		tile.getTileButton().setDisable(true);
		
		// Stop all timelines; the game loop and the timer clock loop
		timelineGame.stop();
		timerClock.getTimeline().stop();
		
		// Display failure alert
		blackTileClickedAlert.showAndWait();
		
		// Return to main menu
		MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
	}
	
	@Override
	// Method run continuously to check for round failure via running out of time
	public void checkTimerEnd()
	{	
		// If a round's timer ever hits 0.0, do things
		// It is VERY important that we include <= 0.0, not just == 0.0, because of the game loop's
		// 16 ms rate... it can jump under 0.0 and the condition never ring true
		if (timerClock.getCurrentTime() <= 0.0)
		{
			// Set the visible timer to .04, which displays 0.0 in the game, then stop all timelines
			timerClock.setTimer(.04);
			timelineGame.stop();
			timerClock.getTimeline().stop();
			
			// Crude and complex, but it works
			Platform.runLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					// Display failure alert
					Alert outOfTimeAlert = new Alert(AlertType.WARNING, "You ran out of time... better luck next time.");
					outOfTimeAlert.setTitle("You have failed!");
					outOfTimeAlert.setHeaderText(null);
					outOfTimeAlert.setGraphic(null);
					outOfTimeAlert.showAndWait();
					
					// Return to main menu
					MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
				}
			});
		}
	}
	
	
}