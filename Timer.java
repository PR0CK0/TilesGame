/*
 * Class: Timer
 * Author: Tyler T. Procko
 * Date(s): March - April 2017
 * 
 * Classes called:
 *     None
 * 
 * Called by classes:
 *     GameFX
 *     GameMode
 *     EasyGameMode
 *     HardGameMode
 * 
 * Description:
 *     This class can be used to create a timer object that takes in a start time. Implements 
 *     quite a bit of JavaFX programming in the form of timelines and animation.
 * 
 * Attrtibutes:
 *     private double dTime - Variable used to take in the start time specified from the 
 *                            constructor used in GameFX
 *     private Timeline timelineTimer - Timeline used for the timer
 *     private Label labelTimer - Label used to place the updating time into
 *     private final VBox rootPane - Used in order for GameFX to access the timer clock's pane
 * 
 * Interesting Methods:
 *     timerLoop() - Method called every 16ms to manually subtract time from the visual clock;
 *     				 also has an important logical block to manually set the timer clock's
 *     				 current time to 0 if it ever hits or goes under 0, due to the frame rate         
 * 
 */


// Imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class Timer 
{
	/* ---------------------- */
	/* ----- ATTRIBUTES ----- */
	/* ---------------------- */
	
	// Starting time variable, also used to decrement/increment the timer clock as needed
	private double dTime;
	
	// Attributes used to visualize the round timer
    private Timeline timelineTimer;
    private Label labelTimer = new Label();
    private final VBox rootPane;
    
    
    /* -------------------------------- */
	/* ----- METHODS/CONSTRUCTORS ----- */
	/* -------------------------------- */
    
    // Timer's constructor; note that it takes in a startTime, specified for each game mode
    public Timer(double dStartTime) 
    {
    	// Initialize the timer's start time
    	dTime = dStartTime;
    	
    	// Set the rootPane
    	VBox vbTimer = new VBox();
    	rootPane = vbTimer;
		
    	// Adjust the visual properties of the timer
	    labelTimer.setTextFill(Color.DARKSLATEGRAY);
	    labelTimer.setStyle("-fx-font-size: 6em;");
	     
	    // Create a new timeline that is called indefinitely at 60fps (16 ms)
        timelineTimer = new Timeline();
		timelineTimer.setCycleCount(Timeline.INDEFINITE);
		timelineTimer.getKeyFrames().add(new KeyFrame(Duration.millis(16), e -> timerLoop()));
		timelineTimer.play();
        
		// Add the updating timer clock to the VBox
		vbTimer.getChildren().add(labelTimer);
    }
    
    
    // Method to continually subtract time from the timer clock
    private void timerLoop()
    {
    	// Every time it is called (every 16 ms), subtract 16 ms from the clock
		dTime -= 0.016;
    	
		// Sometimes the clock's timeline reads an incorrect number if dTime isn't explicitly 
		// set to 0 when the timer clock hits or even skips over 0.0 from the 16ms rate
		// This is to ensure a concrete ZERO when the timer clock hits or goes below 0
		// This is NOT to be confused with setting the timer clock ITSELF to 0.4 when it hits zero,
		// which visually rounds to and displays 0.0 (which is done in the game mode classes)
    	if(dTime <= 0)
    	{
    		dTime = 0;
    	}
    	
    	// Round the clock's displayed number to one decimal place
    	labelTimer.setText(String.format("%.1f", dTime));
    }
    
    
    // Method to set (technically, to update) the Timer's time
    // Used with green and red tiles
    public void setTimer(double dSetTime)
    {
    	dTime = dSetTime;
    }
    
    // Getter for the time, whatever it may be
    public double getCurrentTime()
    {
    	return dTime;
    }
    
    
    // Getter for the timeline
    public Timeline getTimeline()
	{
		return timelineTimer;
	}
    
    
    // Getter for the rootPane
    public Pane getRootPane() 
    {	
		return rootPane;
	}
    
    
}