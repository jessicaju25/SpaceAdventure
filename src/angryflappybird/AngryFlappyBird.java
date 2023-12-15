package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.media.AudioClip;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Random;
import java.io.File;
import java.util.ArrayList;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;


//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;
    private ArrayList<Sprite> bottomPipes;
    private ArrayList<Sprite> topPipes;
    private ArrayList<Sprite> eggs;
    //private Pig pig;
    private int eggcount;
    private ArrayList<Sprite > pigs;
    private ArrayList<Sprite>  goldeggs;
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    private  ArrayList<Integer> pipeHeight;
    private int score =0;
    private boolean passed;
    private boolean  eggcheck;
    private boolean goldeggcheck;
    private boolean pigcheck;
    private boolean snoozecheck;
    private MediaPlayer backgroundMusic;
	private int randomIndex;
	private long snoozeStartTime;
	private int goldEggfreq = 3; 
	private int whiteEggfreq = 5;
	private long lastPigAppearanceTime = 0;
	private final long pigAppearanceInterval = 20 * 1_000_000_000L;
    private Text scoreText = new Text ();
    private Text livesText = new Text ();
    private int lives = 3;

    
    
    
    
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
        String backgroundMusicFile = "background.mp3";
        Media music = new Media (new File(backgroundMusicFile).toURI().toString());
        backgroundMusic = new MediaPlayer(music);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();
        
        scoreText = new Text("Score: " + 0);
        
        scoreText.setFont(Font.font( "Times New Roman", FontWeight.BOLD, 30));
        scoreText.setFill(Color.WHITE);
        
        scoreText.setLayoutX(DEF.SCENE_WIDTH - 390);
        scoreText.setLayoutY(35);
        gameScene.getChildren().add(scoreText);
        livesText = new Text ("Lives: " + lives);

//        gameScene.getChildren().add(scoreText);
        livesText.setFont(Font.font("Times New Roman", FontWeight.BOLD, 30));
        livesText.setFill(Color.WHITE);
        livesText.setLayoutX(DEF.SCENE_WIDTH - 150);
        livesText.setLayoutY(530);
        gameScene.getChildren().add(livesText);
    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
   
        DEF.easyButton.setOnMouseClicked(e -> handleDifficultyButton("Easy"));
        DEF.mediumButton.setOnMouseClicked(e -> handleDifficultyButton("Medium"));
        DEF.hardButton.setOnMouseClicked(e -> handleDifficultyButton("Hard"));
        DEF.playagain.setOnMouseClicked(e -> {
        	resetGameScene(true);
        });
        
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton);
       
        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing

        gameControl.getChildren().addAll(DEF.easyButton, DEF.mediumButton, DEF.hardButton);
        
        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
     // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
        
        // Display the white egg image
        Image weggIconImage = new Image(DEF.pathImage("whiteegg"), DEF.weggicon_WIDTH, DEF.weggicon_HEIGHT, false, false);
        ImageView weggIconImageView = new ImageView(weggIconImage);
     // Create an HBox to hold the white egg image and text
        HBox eggAndTextContainer = new HBox();
        eggAndTextContainer.getChildren().addAll(weggIconImageView, new Text("  Bonus Points"));
        
        // Add the container to the gameControl
        gameControl.getChildren().add(eggAndTextContainer);

        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
       
        // Display the gold egg image 
        Image geggIconImage = new Image(DEF.pathImage("goldegg"), DEF.geggicon_WIDTH, DEF.geggicon_HEIGHT, false, false);
        ImageView geggIconImageView = new ImageView(geggIconImage);
        // Create an HBox to hold the gold egg image and text
        HBox geggAndTextContainer = new HBox();
        geggAndTextContainer.getChildren().addAll(geggIconImageView, new Text("  Lets you snooze"));

        // Add the container to the gameControl
        gameControl.getChildren().add(geggAndTextContainer);

        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
                
        // Display the pig image 
        Image pigIconImage = new Image(DEF.pathImage("pig"), DEF.pigicon_WIDTH, DEF.pigicon_HEIGHT, false, false);
        ImageView pigIconImageView = new ImageView(pigIconImage);
        // Create an HBox to hold the pig image and text
        HBox pigAndTextContainer = new HBox();
        pigAndTextContainer.getChildren().addAll(pigIconImageView, new Text("  Avoid Aliens"));

        // Add the container to the gameControl
        gameControl.getChildren().add(pigAndTextContainer);

        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
    }
    
    
    private void handleDifficultyButton(String difficulty ) {

        if(difficulty.equalsIgnoreCase("Medium")) {

            DEF.SCENE_SHIFT_TIME = 15;

        }

        else if(difficulty.equalsIgnoreCase("Hard")) {

            DEF.SCENE_SHIFT_TIME = 25;

        }

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
        bottomPipes = new ArrayList<>();
        topPipes = new ArrayList<>();
        pipeHeight = new  ArrayList<>();
        eggs = new ArrayList<>();
        pigs = new ArrayList<>();
        goldeggs = new ArrayList<>();
        pipeHeight.add(25);
        pipeHeight.add(50); 
        pipeHeight.add(75);
        pipeHeight.add(100); 
        pipeHeight.add(125); 
        pipeHeight.add(150);
        pipeHeight.add(175);
        pipeHeight.add(200);
        
       

        eggcount =0;
     
        snoozecheck = false;
  if(GAME_OVER == true) {
	  score = 0;
  }
        
        livesText.setText("Lives:" + lives);
        
        
        
        
        
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
    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		floors.add(floor);
    		passed = false;
    	}
   
    	//initialize bottom pipe 
	for(int i=0; i<DEF.pipe_COUNT; i++) {
	
    		int posX = i * (DEF.pipe_WIDTH + 200) +DEF.SCENE_WIDTH;
    		int posY = DEF.SCENE_HEIGHT- DEF.FLOOR_HEIGHT - DEF.pipe_HEIGHT;
    		Sprite pipe = new Sprite(posX, posY, DEF.IMAGE.get("pipe"));
    		pipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		pipe.render(gc);
    		bottomPipes.add(pipe);
    		
    	}
	//initialize top pipe 
	for(int i=0; i<DEF.pipe_COUNT; i++) {
		int posX = i * (DEF.pipe_WIDTH + 200) +DEF.SCENE_WIDTH;
		int posY = 0;
		Sprite pipe2 = new Sprite(posX, posY, DEF.IMAGE.get("pipe2"));
		pipe2.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		pipe2.render(gc);
		
		topPipes.add(pipe2);
	}
	
	
	//initialize egg
for(int i=0; i<DEF.egg_COUNT; i++) {
		
	
		Sprite egg = new Sprite(-3000, -3000, DEF.IMAGE.get("whiteegg"));
		egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		egg.render(gc);
		eggs.add(egg);
		eggcheck = false;
	}
// initialize gold egg
for(int i=0; i<DEF.egg_COUNT; i++) {

	
	Sprite goldegg = new Sprite(-3000, -3000, DEF.IMAGE.get("goldegg"));
	goldegg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
	goldegg.render(gc);
	goldeggs.add(goldegg);
	goldeggcheck = false;
}



//initialize pig
for(int i=0; i<DEF.pig_COUNT; i++) {
	Sprite pig = new Sprite(-3000, 0 ,DEF.IMAGE.get("pig"));
	pig.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
	pig.render(gc);
	pigs.add(pig);
	pigcheck = false;

}


        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("blob0"));
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
    	        
    	         
    	         Image current = background.getImage(); //get current background
    	         
    	         if (current.equals(DEF.IMAGE.get("background"))) {
    	         background.setImage(DEF.IMAGE.get("background0"));
    	         }
    	         else {
    	             background.setImage(DEF.IMAGE.get("background"));
    	         }
    	         
    	         backgroundCounter = now; 
    	     
    	     }
    	     
    	     if (GAME_START) {
    	    	 moveFloor();
    	    	 updatebottomPipes();
    	    	 moveBlob();
    	    	 checkCollision();
    	    	 updatetopPipes();
    	     }
    	 }
    	 /*Method to update the score when the blob has passed the pipe 
    	  *  @param i, the current pipe 
    	  * */
    	 private void score(int i) {
    		 if (bottomPipes.get(i).getPositionX() <= blob.getPositionX() && passed== false) {
    		 score = score +1;
    		 passed = true;
    		 eggcount = eggcount +1;
    		 scoreText.setText("Score: " + score); 
    		 }
    		 
    	 }
    	 
    	 /*Method to move the floor
    	  * */
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
    	 /*Method to update the eggs and move the pipes
    	  * */
    	 private void updatebottomPipes() {
    		for(int i=0; i<DEF.pipe_COUNT; i++) {
    			movebottomPipes(i);
    			updateEggs(i);
    		
    		}
    	 }
    	 
    	 /* Method to update the eggs
    	
    	  * */
    	 	private void updateEggs(int i) {
    	 	
        	eggs.get(i).render(gc);
        	eggs.get(i).update(DEF.SCENE_SHIFT_TIME);
        	goldeggs.get(i).render(gc);
       		goldeggs.get(i).update(DEF.SCENE_SHIFT_TIME);
       		eggcheck = false;
       		goldeggcheck = false;
        	}
    	 	
    	 	
    	 	 /* Method to change the X and Y position of the bottom pipes and call the gold and white eggs to appear 
        	@param int i, the current pipe 
       	  * */
    	 private void movebottomPipes(int i) {

     			if (bottomPipes.get(i).getPositionX() <= -DEF.pipe_WIDTH) {
     				passed = false;
     				double nextX = bottomPipes.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;
     	        	double nextY = DEF.SCENE_HEIGHT -DEF.FLOOR_HEIGHT- DEF.pipe_HEIGHT;
     	        	bottomPipes.get(i).setPositionXY(nextX, nextY);
     	        	whiteEggAppear(i);
     	        	goldEggAppear( i); 
     	        	Random generator = new Random();
     	        	randomIndex = generator.nextInt(pipeHeight.size());
     	        	DEF.pipe_HEIGHT = pipeHeight.get(randomIndex);
               
     			}
     			
     			bottomPipes.get(i).render(gc);
     			bottomPipes.get(i).update(DEF.SCENE_SHIFT_TIME);
     			score(i);

     		}
     		
    	 /* Method to make the white eggs appear randomly on the pipes
    	   @param int i, for the position of the current pipe
     	
    	  * */
    	 private void whiteEggAppear(int i) {
    		  Random r = new Random();
    		    int result = r.nextInt(20); 

    		    if (result < whiteEggfreq && goldeggcheck == false && pigcheck == false) {
    		        eggs.get(i).setPositionXY(bottomPipes.get(i).getPositionX(), bottomPipes.get(i).getPositionY() - DEF.egg_HEIGHT);
    		        eggcheck = true;
    		    }
	    	
	    	}
    	 /* Method to make the gold eggs appear randomly on the pipes
  	   @param int i, for the position of the current pipe
   	
  	  * */
    	 private void goldEggAppear(int i ) {
    		 
    		  Random r = new Random();
    		    int result = r.nextInt(20); 

    		    if (result < goldEggfreq && eggcheck == false && pigcheck == false) {
    		        goldeggs.get(i).setPositionXY(bottomPipes.get(i).getPositionX(),bottomPipes.get(i).getPositionY() - DEF.egg_HEIGHT);
    		        goldeggcheck = true;
    		    }
    		    }
    		
    	 /* Method to change the X and Y position of the top pipes and call the gold and white eggs to appear 
     	@param int i, the current pipe 
    	  * */
    	 private void movetopPipes(int i) {
      		
      		
      			if (topPipes.get(i).getPositionX() <= -DEF.pipe_WIDTH) {

      				double nextX = topPipes.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

      	        	double nextY = 0;
      	        	topPipes.get(i).setPositionXY(nextX, nextY);
      	        	pigappear( i);
      	      
      			}
      			topPipes.get(i).render(gc);
      			topPipes.get(i).update(DEF.SCENE_SHIFT_TIME);
      		
      	
      	 }
    	 /* Method to make the pig drop every 20secs on the pipes
    	   @param int i, for the position of the current pipe
     	
    	  * */
     	 public void pigappear(int i) {
     		
     		long currentTime = System.nanoTime();

     	    if (currentTime - lastPigAppearanceTime >= pigAppearanceInterval && goldeggcheck == false && eggcheck == false) {
	    	   pigs.get(i).setPositionXY(bottomPipes.get(i).getPositionX() ,0);
	    	  pigcheck = true;
	    	  lastPigAppearanceTime = currentTime;
    		}
    

     
   
 

    	 } 
     	 
    	 /* Method to update the pigs 
     	
    	  * */	 
 public void pigUpdate(int i) {
	 
		pigs.get(i).setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
	    pigs.get(i).update(DEF.SCENE_SHIFT_TIME);
	    pigs.get(i).render(gc);
	    pigcheck = false;
	     		 
    	 }
 /*Method to update the pig and move the pipes
  * */
 private void updatetopPipes() {
		for(int i=0; i<DEF.pipe_COUNT; i++) {
			movetopPipes(i);
			pigUpdate(i);
		
		}
	 }
 /*Method to update the blob
  * */
    	 private void moveBlob() {
    		 
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
			
			if (snoozecheck == false ) {
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
			
			bounceBack();
			
			}
			
			else {
				snooze();
			}
			// render blob on GUI
			blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			blob.render(gc);
    	 
    	 
    	 }

    	 
    	 /*Method to bounce the blob backward and make it fall to the ground when it hits a pipe or pig
    	  * */ 
    	 public void bounceBack() {
    		 for (Sprite pipe:bottomPipes) {
 			    if (blob.intersectsSprite(pipe)) {
 			        blob.setVelocity(-500, 1000);
 			        CLICKED = false;
 			    }
 			 
 			}
 			for (Sprite pipe: topPipes) {
 			    if (blob.intersectsSprite(pipe)) {
                     blob.setVelocity(-500, 1000);
                     CLICKED = false;
 			    }
 			}
 			
 			for (Sprite pig:pigs) {
 			    if (blob.intersectsSprite(pig)) {
 			        blob.setVelocity(-500, 1000);
 			        CLICKED = false;
 			    }
 			 
 			}
    	 }
    	 public void dyingSound() {
    	  String audioPath = "dying.mp3";
          AudioClip sound = new AudioClip(getClass().getResource(audioPath).toString());
	      sound.play();
    	 }
    	 /*Method to check all the collisions of with the Sprites and the pipe
    	  * Note: the game is over when the blob collied with the pig and floor
    	  * If the blob intersects with the pipe it loses lives, once three lives a lost the game is over
    	  * */
    	 public void checkCollision() {
    		 checkgoldegg();
    		 checkegg();
    		 pigCollectsEgg();
 			for (Sprite floor: floors) {
 				 GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
                }
			 for (Sprite pipe : bottomPipes) {
				 if(snoozecheck==false ) {
					    if ( blob.intersectsSprite(pipe)) {
					    	 dyingSound();
		                    lives--;
		                    if (lives<=0);
		                    	GAME_OVER = true;
		                    
		                    }
				 }
			    }
			 
			 for (Sprite pipe2 : topPipes) {
				 if(snoozecheck==false ) {
				 
					    if ( blob.intersectsSprite(pipe2)) {
					    	 dyingSound();
		                    lives--;
		                    if (lives<=0);
		                    	GAME_OVER = true;
		                    
		                    }
			    }
			 }
			 for (Sprite pig : pigs) {
				 if(snoozecheck==false ) {
					
						    if (!GAME_OVER && blob.intersectsSprite(pig)) {
						        dyingSound(); 
						        GAME_OVER = true; 
						    }
				 
				 }
			    }
			if (GAME_OVER) {
				
		        showHitEffect();
		        for (Sprite floor : floors) {
		            floor.setVelocity(0, 0);
		        }
		        for (Sprite pipe : bottomPipes) {

		            pipe.setVelocity(0, 0);
		        }
		        for (Sprite pipes2 : topPipes) {

		            pipes2.setVelocity(0, 0);
		        }

		        timer.stop();
		        if (lives <= 0 ) {
		        	lives = 3;
		        	gameOver();
		        }
		    }
			
			
			
			
    	 }
    	 /*Method to check collision with the white egg and add 3 points to the score
    	  * */
    	 public void checkegg() {
    
    		 for (Sprite egg: eggs) {
                 if(blob.intersectsSprite(egg)) {
                	 
                     
                 egg.setPositionXY(-3000, -3000); 
                 egg.render(gc);
                 score = score +3;
                 String audioPath = "bonus.mp3";
                 AudioClip sound = new AudioClip(getClass().getResource(audioPath).toString());
 		         sound.play();
                 
            
                 }
    	 }
    		 
    		 
    	 }
    	 
   
    	 /*Method to check collision with the gold egg 
    	  * */  	 
 public void checkgoldegg() {
    		 
	 for (Sprite egg : goldeggs) {
	        if (blob.intersectsSprite(egg)) {
	    
	            snoozecheck = true;
	            snoozeStartTime = System.nanoTime();
	            egg.setPositionXY(-3000, -3000);
	            egg.render(gc);
	            String audioPath = "bonus.mp3";
                AudioClip sound = new AudioClip(getClass().getResource(audioPath).toString());
		         sound.play();
                
	         
	            }
 }
	 }
 
 //*Method to check collision with the pig and the eggs and removing points if collision happens 

 public void pigCollectsEgg() {
	
		 for(Sprite pig : pigs) {
			 for (Sprite egg : goldeggs) {
	        if (pig.intersectsSprite(egg)) {
	        	egg.setPositionXY(-3000, -3000);
                
                egg.render(gc);
	        	score= score -3;
	        }
	        }
	        }
	 
	
		 for(Sprite pig : pigs) {
			 for (Sprite egg : eggs) {
	        if (pig.intersectsSprite(egg)) {
	        	egg.setPositionXY(-3000, -3000);
                
                egg.render(gc);
	        	score= score -3;
	        }
	        }
	        }
	 
 }

    
 /*Method to make the bird free flow for 6secs when the bird in
  * */
    public void snooze() {
    	
    	
            double birdTopY = blob.getPositionY();
            double topPipeBottomY = topPipes.get(0).getPositionY() + DEF.pipe_HEIGHT;

           
            if (birdTopY <= topPipeBottomY) {
                blob.setPositionXY(DEF.pipe_WIDTH,topPipeBottomY );
            }

            blob.setVelocity(0, DEF.BLOB_FLY_VEL); 
            blob.setImage(DEF.IMAGE.get("blobf"));
            blob.render(gc); 
            long currentTime = System.nanoTime();
            long snoozeDuration = (currentTime - snoozeStartTime) / 1_000_000_000; 
            long remainingTime = Math.max(0, 6 - snoozeDuration); 
            String snoozeText = "Snooze: " + remainingTime + "s";
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Time New Roman", FontWeight.BOLD, 20));
            gc.fillText(snoozeText, 20, 60);
            if (snoozeDuration >= 6) {
                snoozecheck = false; 
                blob.setImage(DEF.IMAGE.get("blob1"));
                
            }
      
    }
    
	 public void gameOver () {
	     Text gameoverText = new Text ("You lost all your lives! ");
	     
	     gameoverText.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
	     gameoverText.setFill(Color.RED);
	     
	     gameoverText.setLayoutX(200);
	     gameoverText.setLayoutY(100);
	     
//
	     VBox gameoverScreen = new VBox(20);
	     gameoverScreen.setAlignment(Pos.CENTER);
//	     gameoverScreen.setFill(Color.GREY);
	     
	     gameoverScreen.getChildren().addAll(gameoverText, DEF.playagain);
//	     gameScene.getChildren()
	     Scene gameoverScene = new Scene (gameoverScreen, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
	     Stage gamestage = (Stage) gameScene.getScene().getWindow();
	     gamestage.setScene(gameoverScene);
	     GAME_OVER = true;
	     
	
	     
	     

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

