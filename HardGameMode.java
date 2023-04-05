/*
 * Class: HardGameMode
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     GameMode
 *     MenuFX
 *     GameFX
 *     Tile
 *     Timer
 *     SaveUserInfo
 * 
 * Called by classes:
 *     GameFX
 * 
 * Description:
 *     This class performs the logic and realtime computation behind the hard, survival 6x6
 *     round. The user can fail in two ways, by running out of time or by clicking a black tile.
 *     The user can win in one way: by clicking green tiles, regaining clock time and surviving
 *     for a total of 30 seconds.
 * 
 * Attrtibutes:
 *     private int iHardGrid Size - Used to define the length and width of the 6x6 array (grid)
 *     public static final double HARD_START_TIME - Used to set the start time of the 6x6 round
 *     private Tile[][] arrTilesHard - The array of tile objects that the user interacts with
 *     private double dInitialTime - Used to store the current system time at the beginning
 *     private Timeline timelineUpdateTile - Used to update the grid randomly
 *     private FadeTransition fadeClicked - Animation to play upon clicking a tile
 *     
 *     All other attributes are inherited.

 * Interesting Methods:
 *     randomlyPopulateGrid() - Randomly populates the Tile array with tiles initially
 *     registerTileClick(Tile) - Called when a tile is clicked and handles each color as needed
 *     randomlyUpdateTile(Tile) - Updates any tile passed in, clicked or not
 *     randomlyUpdateGrid() - Updates the grid randomly, using timelineUpdateTile
 *     checkHardModeCompletion() - Run continuously to check for winning condition
 *     blackTileClick(Tile) - Method run when a black tile is clicked
 *     checkTimerEnd() - Called continuously as the end condition for the timeline in
 *                       gameLoop(); checks for running out of time
 * 
 */


// Imports
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;


public class HardGameMode extends GameMode
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Set the grid size and the start time
	private int iHardGridSize = 6;
	public static final double HARD_START_TIME = 12.0;
	
	// Initialize the array of tiles
	private Tile[][] arrTilesHard = new Tile[iHardGridSize][iHardGridSize];
	
	// Field to save the initial system time once the game mode begins
	// Using system time, this can be used to save the user's total time survived
	private double dInitialTime = 0;
	
	// The timeline used to randomly update the game grid, without user clicks
	private Timeline timelineUpdateTile = new Timeline();
	
	// The fade effect for a tile that has either been clicked or selected to disappear
	private FadeTransition fadeClicked = new FadeTransition(Duration.seconds(.115));
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	public HardGameMode(GameFX gameFX, GridPane gpGame, Timer timerClock) 
	{
		// Inheritance from the parent class, GameMode
		super(gameFX, gpGame, timerClock);
		
		// Set the start time to the current system time
		// This is to have a concrete base, to later subtract from, from which the difference is the time survived
		dInitialTime = System.currentTimeMillis() / 1000.0;
		
		// Set the updating tile timeline to loop indefinitely, calling the specified method every second
		// This is what updates the grid randomly
		timelineUpdateTile.setCycleCount(Timeline.INDEFINITE);
		timelineUpdateTile.getKeyFrames().add(new KeyFrame(Duration.seconds(1), e -> randomlyUpdateGrid()));
		timelineUpdateTile.play();

		// Define the fade transition for every tile update
		// Used to visually cue the user to a tile being clicked and changing color
		fadeClicked.setFromValue(1.0);
		fadeClicked.setToValue(.25);
		fadeClicked.setAutoReverse(true);
		fadeClicked.setCycleCount(2);
	}
	
	
	@Override 
	// Randomly populates the array, and the grid initially, just for the first instant
	// Approximates 15 white, 10 black, 6 green and 5 red tiles
	// Notice that, unlike easy game mode, the initial population of the game grid is non-important,
	// and entirely random, with no hard-coded white or black tiles 
	public void randomlyPopulateGrid() throws ArrayIndexOutOfBoundsException, OutOfMemoryError
	{
		// Clear the game grid before it is populated
		gpGame.getChildren().clear();
		
		// Begin try catch block
		try
		{
			for(int i = 0; i < arrTilesHard.length; i++) 
			{
				for(int j = 0; j < arrTilesHard.length; j++) 
				{
					// RNG for tile selection
					double dRand = Math.random();
					
					// Create and adjust ImageView object to be used by created tiles
					ImageView imgvwTileImg = new ImageView();
					imgvwTileImg.setFitWidth(100);
					imgvwTileImg.setFitHeight(100);
	
					if (dRand <= .417) 
					{
						// Assign the white tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_WHITE_TILE);
						
						// Assign a white tile to the array index	
						arrTilesHard[i][j] = new Tile(TileColor.White, new Button(null, imgvwTileImg));
					}
					
					else if (dRand <= .695) 
					{
						// Assign the black tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_BLACK_TILE);

						// Assign a black tile to the array index	
						arrTilesHard[i][j] = new Tile(TileColor.Black, new Button(null, imgvwTileImg));
					}
					
					else if (dRand <= .862) 
					{
						// Assign the green tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_GREEN_TILE);
						
						// Assign a green tile to the array index	
						arrTilesHard[i][j] = new Tile(TileColor.Green, new Button(null, imgvwTileImg));
					}
					
					else 
					{
						// Assign the red tile image to the ImageView
						imgvwTileImg.setImage(Tile.IMG_RED_TILE);
						
						// Assign a red tile to the array index	
						arrTilesHard[i][j] = new Tile(TileColor.Red, new Button(null, imgvwTileImg));
					}
				
					// Run through the array and add the created tile to the gridpane
					// Talks to GameFX through the constructor
					gpGame.add(arrTilesHard[i][j].getTileButton(), i, j);
					
					// Register tile clicks
					// This method call is more intuitive to in EasyGameMode, but there is deeper meaning behind calling
					// it here, in HardGameMode: the 6x6 grid starts with 36 tile objects, which change  color and
					// type randomly, but are NOT recreated as new Tile objects, so the initial objects must all have click 
					// functionality through this method call
					registerTileClick(arrTilesHard[i][j]);
				}
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
	// Method that handles a click on whatever tile color is clicked
	public void registerTileClick(Tile tile) 
	{		
		// If the tile clicked is white, handle accordingly
		if (tile.getTileColor() == TileColor.White) 
		{	
			tile.getTileButton().setOnAction(e -> 
			{	
				// Add 1 to the number of white tiles clicked
				iNumberOfWhiteTilesClicked++;

				// Start the fade transition on button click
				fadeClicked.setNode(tile.getTileButton());
				fadeClicked.play();
			
				// Method call, passing in the white tile, to randomly update the clicked tile
				randomlyUpdateTile(tile);
			});	
		}
		
		// If the tile clicked is black, handle accordingly (insta-fail)
		// Call to method blackTileClick(), which informs the user, saves their name to a file and returns to the main menu
		if (tile.getTileColor() == TileColor.Black) 
		{
			tile.getTileButton().setOnAction(e -> blackTileClick(tile));
		}
		
		// If the tile clicked is green, disable the tile and update the timer clock
		if (tile.getTileColor() == TileColor.Green) 
		{
			tile.getTileButton().setOnAction(e -> 
			{
				// Start the fade transition on button click
				fadeClicked.setNode(tile.getTileButton());
				fadeClicked.play();
				
				// Update the timer clock by adding .5 seconds
				timerClock.setTimer(timerClock.getCurrentTime() + .5);
				
				// Method call, passing in the green tile, to randomly update the clicked tile
				randomlyUpdateTile(tile);
			});			
		}

		// If the tile clicked is red, disable the tile and update the timer clock
		if (tile.getTileColor() == TileColor.Red) 
		{
			tile.getTileButton().setOnAction(e -> 
			{
				// Start the fade transition on button click
				fadeClicked.setNode(tile.getTileButton());
				fadeClicked.play();
				
				// Update the timer clock by subtracting .5 seconds
				timerClock.setTimer(timerClock.getCurrentTime() - .5);
				
				// Method call, passing in the red tile, to randomly update the clicked tile
				randomlyUpdateTile(tile);
			});	
		}
		checkHardModeCompletion();
	}
	

	// Method to randomly update any tile, either clicked or unclicked, depending on the input percentages
	// What follows are the percentages for selecting the new tile based on a random number
	// Notice that black tiles are not excluded, even if they result in instant failure
	// This method is written to be generically used for random grid updating and on-click updating
	// White tile: .35 W, .35 G, .13 R, .17 B
	// Black tile: .60 W, .20 G, .10 R, .10 B
	// Green tile: .40 W, .36 G, .10 R. .14 B
	// Red tile: .25 W, .45 G, .15 R, .15 B
	public void randomlyUpdateTile(Tile tile)
	{
		// Variables used to store the numeric percentage values for selecting a new tile color
		double dWhitePerc = 0.0, dGreenPerc = 0.0, dRedPerc = 0.0;
		
		// RNG for updating tiles, between 0.0 and 1.0
		double dRand = Math.random();
		
		// The ImageView used to update a selected tile's color
		ImageView newColor = new ImageView();
		newColor.setFitWidth(100);
		newColor.setFitHeight(100);
		
		// Set the numbers for selecting a new tile color- entirely preferential
		// The numbers picked are meant to make the hard game mode challenging yet fun
		if (tile.getTileColor() == TileColor.White)
		{
			dWhitePerc = .35;
			dGreenPerc = .35;
			dRedPerc = .13;
		}
		if (tile.getTileColor() == TileColor.Black)
		{
			dWhitePerc = .60;
			dGreenPerc = .20;
			dRedPerc = .10;
		}
		if (tile.getTileColor() == TileColor.Green)
		{
			dWhitePerc = .40;
			dGreenPerc = .36;
			dRedPerc = .10;
		}
		if (tile.getTileColor() == TileColor.Red)
		{
			dWhitePerc = .25;
			dGreenPerc = .45;
			dRedPerc = .15;
		}
		
		// Disable the tile to prevent misclicks
		tile.getTileButton().setDisable(true);
		
		// Some basic percentage-based math to pick a new tile color based on the above-defined percentages
		// Since dRand returns a value between 0.0 and 1.0, the double values in between can be used to
		// assign percentages to each tile's color selection
		
		// If dRand <= dWhitePerc, the new tile color is white
		if (dRand <= dWhitePerc)
		{
			tile.setTileColor(TileColor.White);
			newColor.setImage(Tile.IMG_WHITE_TILE);
			tile.getTileButton().setGraphic(newColor);
		}
		
		// If dRand > dWhitePerc && dRand <= dWhitePerc + dGreenPerc, the new tile color is green
		else if (dRand <= dWhitePerc + dGreenPerc)
		{
			// Adjust its color properties here, not in the Tile class, as a new tile object is not explicitly being created
			ColorAdjust adjustTileColor = new ColorAdjust();
			adjustTileColor.setBrightness(-.35);
			adjustTileColor.setSaturation(-.4);
			newColor.setEffect(adjustTileColor);

			tile.setTileColor(TileColor.Green);
			newColor.setImage(Tile.IMG_GREEN_TILE);
			tile.getTileButton().setGraphic(newColor);
		}
		
		// If dRand > dWhitePerc + dGreenPerc && dRand <= dWhitePerc + dGreenPerc + dRedPerc, the new tile color is red
		else if(dRand <= dWhitePerc + dGreenPerc + dRedPerc)
		{
			tile.setTileColor(TileColor.Red);
			newColor.setImage(Tile.IMG_RED_TILE);
			tile.getTileButton().setGraphic(newColor);
		}
		
		// Otherwise, if dRand >= the leftover percentage (dWhitePerc + dGreenPerc + dRedPerc), the new tile color is black
		else
		{
			// Adjust its color here, not in the Tile class, as a new tile object is not explicitly being created
			ColorAdjust adjustTileColor = new ColorAdjust();
			adjustTileColor.setBrightness(.14);
			newColor.setEffect(adjustTileColor);
			
			tile.setTileColor(TileColor.Black);
			newColor.setImage(Tile.IMG_BLACK_TILE);
			tile.getTileButton().setGraphic(newColor);
		}
		
		// Enable the tile so the game mode can continue
		tile.getTileButton().setDisable(false);
		
		// Register tile click to keep the cycle of clicking tiles going
		// This method call here is a little ambiguous- but it works well
		registerTileClick(tile);
	}
	
	
	// Method to randomly update the grid, called every second by timelineUpdatingTile
	// Notice the use of randomlyUpdateTile, which has the same percentages for a tile's
	// new color for clicked and un-clicked tiles
	public void randomlyUpdateGrid()
	{
		// As long as the timer clock is not 0, keep performing this method's logic
		if(timerClock.getCurrentTime() > 0.0)
		{
			// Every time this method is called, loop twice, literally updating 2 tiles at once
			for (int i = 0; i < 2; i++)
			{	
				// Since the array used is 6x6 in size, generate two numbers for the length and width of the array
				// Notice the expression used is NOT (int) Math.random() * 6;
				// This would result in the (int) type-casting the Math.random(), which would truncate and give 0 or 1 every time
				int iRandWidth = (int)(Math.random() * 6);
				int iRandHeight = (int)(Math.random() * 6);
				
				// Call to method, passing in the tile with the specified, random index, to update 
				randomlyUpdateTile(arrTilesHard[iRandWidth][iRandHeight]);
			}
		}
	}
	
	
	// Method to handle hard mode completion
	// If the user makes it for 30 seconds (by spamming green tiles and regaining time), then they have won the game
	public void checkHardModeCompletion()
	{
		if ((System.currentTimeMillis() / 1000) - dInitialTime >= 30.0)
		{
			// Stop all timelines; the updating tile loop, the game's loop, the timer clock's loop and any fade transition
			timelineUpdateTile.stop();
			timelineGame.stop();
			timerClock.getTimeline().stop();
			fadeClicked.stop();
			
			// Create SaveUserInfo object for the purpose of writing to a file
			SaveUserInfo userInfo = new SaveUserInfo();
			
			// Append to scores file using win 
			// Notice the use of dInitialTime, which is subtracted from the current system time, then divided by 1000 to get it to seconds
			// Important to divide by 1000.0, not 1000- this ensures proper decimal places
			// Also acts as an alert- refer to the SaveUserInfo class
			userInfo.appendUserScore(UserFinishType.Win, iNumberOfWhiteTilesClicked, (System.currentTimeMillis() / 1000.0) - dInitialTime);
			
			// Return to main menu
			MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
		}
	}
	
	
	@Override
	// Method run when a black tile is clicked; displays an alert to the user and returns to the main menu
	public void blackTileClick(Tile tile) 
	{
		// Disable the button
		tile.getTileButton().setDisable(true);

		// Stop all timelines; the updating tile loop, the game's loop, the timer clock's loop and any fade transition
		timelineUpdateTile.stop();
		timelineGame.stop();
		timerClock.getTimeline().stop();
		fadeClicked.stop();
		
		// Create SaveUserInfo object for the purpose of writing to a file
		SaveUserInfo userInfo = new SaveUserInfo();
		
		// Append to scores file using black tile failure
		// Also acts as an alert- refer to the SaveUserInfo class
		userInfo.appendUserScore(UserFinishType.Fail_BlackTile, iNumberOfWhiteTilesClicked, (System.currentTimeMillis() / 1000.0) - dInitialTime);
		
		// Return to main menu
		MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
	}
	
	@Override
	// Method run continuously to check for round failure via running out of time
	public void checkTimerEnd()
	{	
		// If a round's timer ever hits 0.0, inform the user through an alert and return to the main menu
		if (timerClock.getCurrentTime() <= 0.0)
		{
			// Stop the updating tile loop, the game's timeline loop and any fade transition
			timelineUpdateTile.stop();
			timelineGame.stop();
			fadeClicked.stop();
			
			// Set the visible timer to .04, which displays 0.0 in the game (because of rounding)
			// Then, pause the timer clock's timeline
			timerClock.setTimer(.04);
			timerClock.getTimeline().stop();
			
			// Create SaveUserInfo object for the purpose of writing to a file
			SaveUserInfo userInfo = new SaveUserInfo();
			
			// This block does what it sounds like... it runs this code later in the program flow
			// This makes the game display the failure alert properly before returning to the main menu
			Platform.runLater(new Runnable() 
			{
				@Override
				public void run() 
				{
					// Append to scores file using out of time failure
					// Also acts as an alert- refer to the SaveUserInfo class
					userInfo.appendUserScore(UserFinishType.Fail_OutOfTime, iNumberOfWhiteTilesClicked, (System.currentTimeMillis() / 1000.0) - dInitialTime);
					
					// Return to main menu
					MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
				}
			});
		}
	}
	
	
}