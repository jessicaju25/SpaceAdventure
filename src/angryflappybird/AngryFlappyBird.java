package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Bird blob;
    private ArrayList<Bird> floors;
    private ArrayList<Pipe> pipes;
    private ArrayList<Pipe> pipes2;
    private Bird egg;
    private int count;
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    
	// the mandatory main method 
    public static void main(String[] args) {
        launch(args);
    }
       
    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	
        HBox root = new HBox();
		HBox.setMargin(gameScene, new Insets(0,0,0,15));
		root.getChildren().add(gameScene);
		root.getChildren().add(gameControl);
		
		// add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        
        DEF.easyButton.setOnMouseClicked(e -> handleDifficultyButton("Easy"));
        DEF.mediumButton.setOnMouseClicked(e -> handleDifficultyButton("Medium"));
        DEF.hardButton.setOnMouseClicked(e -> handleDifficultyButton("Hard"));
        
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton,DEF.easyButton,DEF.mediumButton,DEF.hardButton);
    }
    
    
    private void handleDifficultyButton(String difficulty ) {
    
    	
    }
    
    private void mouseClickHandler(MouseEvent e) {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
    }
    
    private void resetGameScene(boolean firstEntry) {
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        pipes2 = new ArrayList<>();
        count= 0;
        
    	if(firstEntry) {
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("background");
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas);
    	}
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH ;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		
    		Bird floor = new Bird(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    		
    	}
    	//initialize pipe 
	for(int i=0; i<DEF.pipe_COUNT; i++) {
	
    		int posX = i * DEF.pipe_WIDTH + 200;
    		int posY = DEF.SCENE_HEIGHT- DEF.FLOOR_HEIGHT - DEF.pipe_HEIGHT;
    		
    		Pipe pipe = new Pipe(posX, posY, DEF.IMAGE.get("unitytut-pipe"));
    		pipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		

    		pipe.render(gc);
    		
    		pipes.add(pipe);
    
    	}
    	
	for(int i=0; i<DEF.pipe_COUNT; i++) {
		
		int posX = i * DEF.pipe_WIDTH + 200;

		int posY = 0;
		
		Pipe pipe2 = new Pipe(posX, posY, DEF.IMAGE.get("unitytut-pipe2"));
		pipe2.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		pipe2.render(gc);
		
		pipes2.add(pipe2);
	}
    
        
        // initialize blob
        blob = new Bird(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("blob0"));
        blob.render(gc);
        
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
    }

    //timer stuff
    class MyTimer extends AnimationTimer {
    	
    	int counter = 0;
    	long backgroundCounter = 0;
    	
    	 @Override
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     //double  tenSeconds = startTime * DEF.NANOSEC_TO_SEC;
    	     ImageView background = (ImageView) gameScene.getChildren().get(0);
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	     //background.setImage(DEF.IMAGE.get("background"));
    	     if  ( now - backgroundCounter >= 10_000_000_000L ) {
    	         //change the background image every 10 seconds 
    	         
    	         Image current = background.getImage(); //get current background
    	         
    	         if (current.equals(DEF.IMAGE.get("background"))) {
    	         background.setImage(DEF.IMAGE.get("background0"));
    	         }
    	         else {
    	             background.setImage(DEF.IMAGE.get("background"));
    	         }
    	         
    	         backgroundCounter = now; //update counter
    	     
    	     }
    	     
    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 moveFloor();
    	    	 movePipes();
    	    	 movePipes2();
    	    	
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 checkegg();
    	    	 checkCollision();
    	    	 
    	     }
    	 }
    	 
    	 // step1: update floor
    	 private void moveFloor() {
    		
    		for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    			if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
    				double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH ;
    	        	double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    	        	floors.get(i).setPositionXY(nextX, nextY);
    			}
    			floors.get(i).render(gc);
    			floors.get(i).update(DEF.SCENE_SHIFT_TIME);
    		}
    	 }
    	 
    	 
    	 
    	 private void movePipes() {
     		
     		for(int i=0; i<DEF.pipe_COUNT; i++) {
     			if (pipes.get(i).getPositionX() <= -DEF.pipe_WIDTH) {

     				double nextX = pipes.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

     	        	double nextY = DEF.SCENE_HEIGHT -DEF.FLOOR_HEIGHT- DEF.pipe_HEIGHT;
     	        	pipes.get(i).setPositionXY(nextX, nextY);
     			}
     			pipes.get(i).render(gc);
     			pipes.get(i).update(DEF.SCENE_SHIFT_TIME);
     			double pipeVelocityX = pipes.get(i).getVelocityX();
     			double pipeVelocityY = pipes.get(i).getVelocityY();
     			
//     			  if (elapsedTime > 0 && (elapsedTime * DEF.NANOSEC_TO_SEC) % 5 == 0) {
     		            whiteEggAppear(pipeVelocityX, pipeVelocityY);
//     		        }
     		}
     		
     	 }
    	 
    	 private void movePipes2() {
      		
      		for(int i=0; i<DEF.pipe_COUNT; i++) {
      			if (pipes2.get(i).getPositionX() <= -DEF.pipe_WIDTH) {

      				double nextX = pipes2.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

      	        	double nextY = 0;
      	        	pipes2.get(i).setPositionXY(nextX, nextY);
      			}
      			pipes2.get(i).render(gc);
      			pipes2.get(i).update(DEF.SCENE_SHIFT_TIME);
      		}
      	 }
     	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
				
				int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
				imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
				blob.setImage(DEF.IMAGE.get("blob"+String.valueOf(imageIndex)));
				blob.setVelocity(0, DEF.BLOB_FLY_VEL);
			}
			// blob drops after a period of time without button click
			else {
			    blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
			    CLICKED = false;
			}

			// render blob on GUI
			blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			blob.render(gc);
    	 }
    	 
    	 
    	 private void whiteEggAppear(double x, double y) {

    		 		
    		   		int posX = DEF.pipe_WIDTH;
    	    		int posY = DEF.SCENE_HEIGHT- DEF.FLOOR_HEIGHT - DEF.pipe_HEIGHT- DEF.egg_HEIGHT;
    	    	    egg = new Bird(posX, posY,DEF.IMAGE.get("whiteegg"));
    	    	    egg.setVelocity(x, y);
    	    	    egg.render(gc);

    	 }
    	 
    	 // possibly condense this code and also look into cropping the pipe image 
    	 public void checkCollision() {
    		 
    		 
    		if (blob.intersectsSprite(egg)) {
    			egg.setPositionXY(-100,-100);
    		}
    		// check collision  
			for (Bird floor: floors) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			 for (Pipe pipe : pipes) {
				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
			    }
			 
			 for (Pipe pipe2 : pipes2) {
				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe2);
			    }
	
			if (GAME_OVER) {
		        showHitEffect();
		        for (Bird floor : floors) {
		            floor.setVelocity(0, 0);
		        }
		        for (Pipe pipe : pipes) {
		            pipe.setVelocity(0, 0);
		        }
		        for (Pipe pipes2 : pipes2) {
		            pipes2.setVelocity(0, 0);
		        }
		        timer.stop();
		    }
			
			
			
			
    	 }
    	 
    	 public void checkegg() {
    		 
    		 if (blob.intersectsSprite(egg)) {
    		egg.setImage(null); 
     		}
    	 }
	     private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);
	        parallelTransition.getChildren().add(fadeTransition);
	        parallelTransition.play();
	     }
    	 
    } // End of MyTimer class

} // End of AngryFlappyBird Class

