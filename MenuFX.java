/*
 * Class: MenuFX
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 * 	   MenuFX
 *     GameFX
 * 
 * Called by classes:
 *     EasyGameMode
 *     HardGameMode 
 *
 * Description:
 *     This class is the beginning of the entire game; from here, users can start a new
 *     game, receive help or read the credits, toggle the music on/off and exit
 *     the application. Most of the programming in this class is JavaFX-related,
 *     so nothing too interesting goes on, besides some ugly syntax and properties
 *     used to let the main menu be returned to from other classes (refer to the
 *     Attributes section).
 * 
 * Attrtibutes:
 * 	   public static final int WINDOW_WIDTH - Used to set the window width
 * 	   public static final int WINDOW_HEIGHT - Used to set the window height
 *     public static MenuFX menuFXinstance - Used for reference by other classes
 *     private Stage stage - Used for reference by other classes
 *     private VBox menuVBox menuVBox - Again, used for reference by other classes
 *                                      Acts as a rootPane  
 * 
 * Interesting Methods:
 *     helpButtonClick() - Handles the help button being clicked
 *     musicButtonClick(ToggleButton) - Handles the music button being clicked
 *     creditsButtonClick() - Handles the credits button being clicked
 *     
 * Notes:
 *     This class, and all of the classes in this project, follow a form of Hungarian 
 *     notation, in which all attribute/variable names are prefixed with their data
 *     type. For instance: int iNum, double dAvg, Button btOk, etc.
 * 
 */


// Imports
import java.io.File;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;


public class MenuFX extends Application 
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Constants for the window size of the game
	public static final int WINDOW_WIDTH = 650; 
	public static final int WINDOW_HEIGHT = 800;
	
	// Reference variables used to return to the main menu from other classes
	// If a MenuFX constructor was used, it would be done similarly
	public static MenuFX menuFXinstance = null;
	private Stage stage = null;
	private VBox vbMenu = null;
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// It is bad practice to define variables outside of the commonly-accepted declaration area at the top of a method,
	// but this start method is better understood with declarations and modifications done in chunks
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		// Set for use  
		MenuFX.menuFXinstance = this;
		stage = primaryStage;
		
		// Create media player and update its settings
		// Rather than inputting the entire absolute URI, which would confine the program to the creator's device, 
		// we create a new file, grab the URI on whatever machine the program is running on and convert it to a string
		// Want to see the absolute URI? Uncomment the next line
		//System.out.println(new File("music/menu.mp3").toURI().toString());
		Media mediaMenu = new Media(new File("music/menu.mp3").toURI().toString());
		MediaPlayer mediaPlayerMenu = new MediaPlayer(mediaMenu);
		// Adjust the cycles and volume then start playing menu music
		mediaPlayerMenu.setCycleCount(Integer.MAX_VALUE);
		mediaPlayerMenu.setVolume(0.1);
		mediaPlayerMenu.setAutoPlay(true);
		
		// Create menu VBox and set the background image
		vbMenu = new VBox(45);
		vbMenu.setBackground(new Background(new BackgroundImage(new Image("image/bambooBG.jpg"), null, null, null, new BackgroundSize(45, 45, true, true, true, true))));
		
		// Create ImageView from logo image
		ImageView imgLogo = new ImageView("image/tiles_logo.png");
		// Set a margin to the logoImage so it's away from the buttons
		VBox.setMargin(imgLogo, new Insets(10, 0, 140, 0));
		
		// Create buttons with titles
		// START - HELP - MUSIC TOGGLE - CREDITS - EXIT
		Button btStart = new Button("Start Game");
		Button btHelp = new Button("Help");
		ToggleButton btMusic = new ToggleButton("Music On/Off");
		Button btCredits = new Button("Credits");
		Button btEnd = new Button("Exit Game");
		
		// Create DropShadow effect for the buttons
		// Radius, offset X, offset Y, color
		DropShadow dropShadowButton = new DropShadow(5.0, 3.0, 3.0, Color.BROWN);
		
		// Modify buttons' text and apply drop shadow
		btStart.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btStart.setEffect(dropShadowButton);
		btHelp.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btHelp.setEffect(dropShadowButton);
		btMusic.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btMusic.setEffect(dropShadowButton);
		btCredits.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btCredits.setEffect(dropShadowButton);
		btEnd.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btEnd.setEffect(dropShadowButton);
		
		// Start GameFX on startButton click
		// Get the scene of our primaryStage in MenuFx, then set the root of it as GameFX's rootPane, which happens
		// to be the regular pane that the timer and game grid are both set in
		btStart.setOnAction(e -> 
		{
			GameFX gameFXObject = new GameFX();
			primaryStage.getScene().setRoot(gameFXObject.getRootPane());
		});
		
		// Display help window through method on helpButton click
		btHelp.setOnAction(e -> helpButtonClick());
		
		// Toggle play/pause through method on musicButton click
		btMusic.setOnAction(e -> musicButtonClick(btMusic, mediaPlayerMenu));
		
		// Display credits window through method on creditsButton click
		btCredits.setOnAction(e -> creditsButtonClick());
		
		// End program on endButton click
		btEnd.setOnAction(e -> Platform.exit());
		
		// Add all nodes to the vbox pane and center it (left -> right  =  top -> bottom) and center it to the stage
		vbMenu.getChildren().addAll(imgLogo, btStart, btHelp, btMusic, btCredits, btEnd);
		vbMenu.setAlignment(Pos.CENTER);

		// Place pane into scene, then scene into stage and show it
		// Set the title and make the window non-resizable, otherwise the proportions look terrible
		Scene sceneMenu = new Scene(vbMenu, WINDOW_WIDTH, WINDOW_HEIGHT);
	    primaryStage.setTitle("-tiles-"); 
	 	primaryStage.setResizable(false);
	    primaryStage.setScene(sceneMenu); 
	    primaryStage.show(); 
	}
	
	
	// Displays help window on helpButton click
	private void helpButtonClick()
	{
		// Create alert popup for the help button if player needs help
		Alert helpAlert = new Alert(AlertType.INFORMATION, 
				"This game, tiles, will test your visual and fine-muscle acuity. When you begin a round, a 5x5 grid of tiles, with its contents randomly placed, will appear. "
				+ "A timer will always be displayed at the top of the gamescreen. During this short, intense time, you must click all of the white tiles, while avoiding the black tiles. "
				+ "Once a round is complete, a new round begins with a new grid of tiles. If you manage to complete five rounds, the game will shift to a 6x6 grid of tiles, "
				+ "with a little more time given at the start. But, this 6x6 grid will be a survival round of disappearing and randomly regenerating tiles. You must go "
				+ "until you fail, or win after 30 seconds. Once you do either, you will be prompted to enter your name, and your score will be saved to a file. "
				+ "\n\nWhite tiles - Click all of these! \nBlack tiles - Do not click these! \nGreen tiles - These add to your timer. \nRed tiles - These subtract from your timer.");
		helpAlert.setTitle("Help");
		helpAlert.setHeaderText(null);
		helpAlert.setGraphic(null);
		helpAlert.showAndWait();
	}
	
	
	// Toggles music play/pause on musicButton click
	private void musicButtonClick(ToggleButton musicButton, MediaPlayer menuPlayer)
	{
		// isSelected() returns a boolean
		// If musicButton is selected == false, and the button is clicked, pause/keep it paused
		// Otherwise, resume playing
		if (musicButton.isSelected()) 
	    {
	        menuPlayer.pause();
	    }
	    else 
	    {
	        menuPlayer.play();
	    }
	}
	
	
	// Displays credits window on creditsButton click
	private void creditsButtonClick()
	{
		// Create alert popup for the credits button so player can see credits
		Alert creditsAlert = new Alert(AlertType.INFORMATION, 
				"A Java application by Tyler T. Procko, S.E. major- ERAU Daytona.\n-Under the instruction of Dr. Keith Garfield-\n\n\n"
				+ "Menu music:\n\"Chinese moods\" by Klaus Schønning\n\nMenu Background: http://es.forwallpaper.com/wallpaper/simple-bamboo-iii-342743.html"
				+ "\n\nHelp with project:\n    Thomas Bassa, a fellow student and teaching assistant at Embry-Riddle Aeronautical University, was a massive help with this project. "
				+ "He guided me through many of the more complex JavaFX principles.\n     Mohammad Alali, another student and good friend of mine, who was"
				+ " already well-versed in programming and game design, was a great help with many of the more complex logical pieces of the game.");
		creditsAlert.setTitle("Credits");
		creditsAlert.setHeaderText(null);
		creditsAlert.setGraphic(null);
		creditsAlert.showAndWait();
	}
	
	
	// Getter for the menu's Stage
	public Stage getStage()
	{
		return stage;
	}
	
	
	// Getter for the menu's VBox
	public VBox getMenuVBox()
	{
		return vbMenu;
	}
	
	
	// Needed to run JavaFX w/o the use of the command line
	public static void main(String[] args) 
	{
	    MenuFX.launch(args);
	    // Or just launch(args);
	}
	
	
}