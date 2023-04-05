/*
 * Class: Tile
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     None
 * 
 * Called by classes:
 *     GameMode
 *     EasyGameMode
 *     HardGameMode
 * 
 * Description:
 *     This class contains all of the tile objects' functionality. The classes EasyGameMode
 *     and HardGameMode interact heavily with this class.
 * 
 * Attrtibutes:
 *     public static final Image imgWhiteTile - White tile image constant
 *	   public static final Image imgBlackTile - Black tile image constant
 *	   public static final Image imgGreenTile - Green tile image constant
 *	   public static final Image imgRedTile - Red tile image constant
 *     private TileColor color - Used for assigning a tile Color through the constructor
 *                               used in the game mode classes
 *     private Button btTile - Used for assigning a button through the constructor used 
 *                             in the game mode classes
 *                                                      
 * Interesting Methods:
 *     NONE
 * 
 */


// Imports
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Tile 
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Image constants for the tiles
	public static final Image IMG_WHITE_TILE = new Image("image/white_tile.jpg");
	public static final Image IMG_BLACK_TILE = new Image("image/black_tile.jpg");
	public static final Image IMG_GREEN_TILE = new Image("image/green_tile.jpg");
	public static final Image IMG_RED_TILE = new Image("image/red_tile.jpg");
	
	// Constructor reference variables that are used in each game mode to create proper tile 
	// objects, which contain a color and a button- all that is needed to update upon clicks
	// TileColor from enumeration
	private TileColor color;
	// Button for each tile
	private Button btTile = new Button(null, new ImageView());
	
	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// Constructor for the Tile objects
	public Tile(TileColor color, Button btTile) 
	{
		// Set the local attributes of color and tile button to equal those passed in from a randomlyPopulate() method
		this.color = color;
		this.btTile = btTile;
		
		// Remove the button border from the tile images
		// If this step is not performed, then the tiles that users see in the game grids will have actual 
		// JavaFX button borders, which skews their shape and causes them to look horrible
		this.btTile.setPadding(Insets.EMPTY);
		
		// If the new tile generated is black, adjust its color, as JavaFX did not take well to its .jpg
		if (color == TileColor.Black)
		{
			ColorAdjust adjustTileColor = new ColorAdjust();
			adjustTileColor.setBrightness(.14);
			btTile.getGraphic().setEffect(adjustTileColor);
		}
		
		// If the new tile generated is green, adjust its color properties, as JavaFX did not take well to its .jpg
		if (color == TileColor.Green)
		{
			ColorAdjust adjustTileColor = new ColorAdjust();
			adjustTileColor.setBrightness(-.35);
			adjustTileColor.setSaturation(-.4);
			btTile.getGraphic().setEffect(adjustTileColor);
		}
	}
	
	
	// Getter for tileButton
	public Button getTileButton() 
	{
		return btTile;
	}
	
	
	// Getter for TileColor
	public TileColor getTileColor() 
	{
		return color;
	}
	
	
	// Setter for TileColor
	public void setTileColor(TileColor color)
	{
		this.color = color;
	}
	
	
}


// The enumeration used to determine which color tile is being used
// Much easier for debugging to see the color name in English
enum TileColor 
{
	White,
	Black,
	Green,
	Red
}