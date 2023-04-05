/*
 * Some good reference for file I/O and JavaFX:
 * http://stackoverflow.com/questions/24565539/in-need-of-javafx-help-writing-to-a-txt-file
 * http://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
 * 
 * Class: SaveUserInfo
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     None
 * 
 * Called by classes:
 *     HardGameMode
 * 
 * Description:
 *     This class writes the user's input name and final score to a file upon finishing, either
 *     winning or failing, HardGameMode.
 * 
 * Attrtibutes:
 *     private String strUserFinishType - Used to save the relevant user finish type
 *     private String strFinishInfo - Used to save relevant information on the user's finish type
 *     private UserFinishType finishType - Enumeration instance used to save the user's finish
 *     									   type passed in from HardGameMode
 * 
 * Interesting Methods:
 *     appendUserScore() - Appends user information to the scores.txt file
 *     displayTextFieldInput() - Returns a string of the user's input name
 * 
 */


// Imports
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class SaveUserInfo
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// String used to store the user's finish type, of which there are only three:
	// Fail by running out of time, fail by clicking a black tile, and a win
	private String strUserFinishType = new String();
	
	// String used to store the information related to the user's finish type
	private String strFinishInfo = new String();
	
	// Finish type reference attribute used to locally store the user's finish type
	private UserFinishType finishType;

	
	/* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
	
	// Method to begin writing user info to the file
	public void appendUserScore(UserFinishType finishType, int iWhitesClicked, double dTimeSurvived) 
	{
		// Depending on what finishType is input as the parameter (during the method call in HardGameMode),
		// change the value of the UserFinishType string to make sense to the user 
		// The string chosen will be passed into the file write, and added as part of a user-instance header
		// In each if block, set the local attribute for the user's finish type and update the string appropriately
		if (finishType == UserFinishType.Fail_BlackTile)
		{
			this.finishType = finishType;
			strUserFinishType = "LOSER - BLACK TILE";
		}
		
		if (finishType == UserFinishType.Fail_OutOfTime)
		{
			this.finishType = finishType;
			strUserFinishType = "LOSER - OUT OF TIME";
		}
		
		if (finishType == UserFinishType.Win)
		{
			this.finishType = finishType;
			strUserFinishType = "WINNER";
		}
		
		// Create a BufferedWriter (much faster than FileWriter... line by line vs. character by character)
		// Open (or create, if it doesn't already exist) the file, scores.txt, to save the user scores to
		// This is known as a try-with-resources file write, which implements the exception handling needed
		// for ANY file I/O, as well as opening and closing our file in the try's arguments
		// Remove keyword 'true' in FileWriter's constructor to turn off append mode
		try (BufferedWriter buffwrWriter = new BufferedWriter(new FileWriter("scores.txt", true))) 
		{
			// Skip two lines between each user-instance block
			// Makes pretty blocks of text for each appending of user information
			buffwrWriter.newLine();
			buffwrWriter.newLine();
			// Add in the user's finish type from updated string inside of a user-instance block header
			buffwrWriter.write("<-> ----- " + strUserFinishType + " ----- <->");
			buffwrWriter.newLine();
			// The user's name is passed in from the string the user inputs into a text field, through
			// the method displayTextFieldInput()
			buffwrWriter.write("NAME: ");
			buffwrWriter.write(displayTextFieldInput());
			buffwrWriter.newLine();
			// The number of white tiles clicked is passed in from HardGameMode
			buffwrWriter.write("WHITE TILES CLICKED: ");
			buffwrWriter.write("" + iWhitesClicked);
			buffwrWriter.newLine();
			// The time survived is passed in from HardGameMode
			buffwrWriter.write("TIME SURVIVED: ");
			buffwrWriter.write("" + String.format("%.2f", dTimeSurvived) + " seconds");
			buffwrWriter.newLine();
			// Begin writing a user-instance block footer
			buffwrWriter.write("<-> ------");
			
			// For the length of the user finish type string passed to the user-instance block header,
			// add in an equal amount of dashes to the user-instance block footer (so pretty!)
			for (int i = 0; i < strUserFinishType.length(); i++)
			{
				buffwrWriter.write("-");
			}
			
			// Finish writing a user-instance block footer
			buffwrWriter.write("------ <->");
			
			// For example, a user-instance block in the file scores.txt may look like this:
			// <-> ----- LOSER - OUT OF TIME ----- <->
			// NAME: Tyler Procko
			// WHITE TILES CLICKED: 15
			// TIME SURVIVED: 15.051 seconds
			// <-> ------------------------------- <->
			// Where the finish type in the header varies and changes the footer's length
		} 
		
		// Catch exception if necessary and print an error message to the console
		// There are no extensive measures to take if an IOException occurs- it never should
		// If the file is deleted/moved when the BufferedWriter is open, then the exception WILL occur
		// But, any good OS will prevent the user from doing so; an in-use alert will appear, blocking
		// any moving/deletion while the file and its writer are in use
		catch (IOException e) 
		{
			System.out.println("An error occured during the file write. What on earth did you do?");
		}
	}
	
	
	// Method to take in the user's name through a text field
	// Does a little bit more than just returning a string, but the end goal of this method is to get the user's name
	// Sometimes, because of the timelines' rates, finishing the 6x6 round, which calls this class that implements the 
	// showAndWait() method below, can throw an IllegalStateException, which is ENTIRELY non-lethal, and the program will still run perfectly fine
	// The error is thrown very, very, VERY rarely and has no effect whatsoever besides an ugly stack trace, so in the
	// off-chance that it does occur, catch it with a try catch block
	private String displayTextFieldInput() throws IllegalStateException
	{
		// Depending on what finishType is input as the parameter, change the info dialogue to make sense
		// The finish type comes right from HardGameMode
		if (finishType == UserFinishType.Fail_BlackTile)
		{
			strFinishInfo = "A black tile was clicked... better luck next time.\nNow, save your name to a file.";
		}
		
		if (finishType == UserFinishType.Fail_OutOfTime)
		{
			strFinishInfo = "Out of time... better luck next time.\nNow, save your name to a file.";
		}
	
		if (finishType == UserFinishType.Win)
		{
			strFinishInfo = "You have won! Great job!\nNow, save your name to a file.";
		}
		
		// Create a whole new stage (with a scene and regular pane) and display it to the user, instead of using a simple alert
		// This allows the impromptu alert (the created stage)'s properties to be adjusted and custom-fit 
		Pane paneAlert = new Pane();
		Scene sceneAlert = new Scene(paneAlert, 320, 120);
		Stage stageAlert = new Stage();
		stageAlert.setTitle("-Game Over-");
		stageAlert.setResizable(false);
		
		// Create a text field and confirmation button to save the user's name
		// Also, create a label to display to the user some helpful dialogue
		Button btOk = new Button("Okay!");
		TextField tfName = new TextField();
		tfName.setPromptText("Enter your name");
		Label labelInfo = new Label(strFinishInfo);
		
		// Create dropshadow effect for all of the nodes
		DropShadow dropShadowButton = new DropShadow(5.0, 3.0, 3.0, Color.GRAY);
		
		// Adjust the properties of each of the three nodes (location, text properties and effects)
		btOk.relocate(240, 20);
		btOk.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		btOk.setEffect(dropShadowButton);
		tfName.relocate(40, 20);
		tfName.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		tfName.setEffect(dropShadowButton);
		labelInfo.relocate(10, 60);
		labelInfo.setStyle("-fx-font-family: \"Palatino Linotype\"; -fx-font-size: 1.15em");
		
		// Set the stage to close on btOk click
		btOk.setOnAction(e -> stageAlert.close());
		 
		// Add all nodes to the pane and display the stage
		paneAlert.getChildren().addAll(btOk, tfName, labelInfo);
		stageAlert.setScene(sceneAlert);
		// Make it so the shown stage cannot be clicked off of (otherwise the user can have more than one
		// fail/win stage show up to them)... the evil users' tricks have been thwarted!
		// Also, show the stage and wait for user input
		stageAlert.initModality(Modality.APPLICATION_MODAL);
		
		// Begin try catch block to show and wait on the alert stage
		try 
		{
			stageAlert.showAndWait();
		}
		
		// If the useless error occurs, print out a confirmatory message
		// Again, it will have no effect on the program even if it is thrown and not caught
		catch (IllegalStateException e) 
		{	
			System.out.println("Illegal State Exception has been caught. The game will continue running normally.");
			
			// Return to main menu
			MenuFX.menuFXinstance.getStage().getScene().setRoot(MenuFX.menuFXinstance.getMenuVBox());
		}
		
		// Whatever is in the text field upon the stage closing is returned as a string
		// Used in the method appendUserScore()
		return tfName.getText();
	}
}


// Enumeration for user finish types
// Used in HardGameMode to pass in the finish types, so the proper informational strings are displayed
enum UserFinishType
{
	Fail_BlackTile,
	Fail_OutOfTime,
	Win
}