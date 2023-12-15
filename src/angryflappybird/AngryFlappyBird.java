package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import java.util.Random;
import java.io.File;
//import java.awt.Color;
//import java.awt.Font;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
//import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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
    private ArrayList<Bird> eggs;
    private ArrayList<Bird> goldeggs;
    //private Pig pig;
    private int pipecount, eggcount;
    private ArrayList<Pig > pigs;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private GraphicsContext gc;		
    private long lastEggAppearanceTime = 0;
    private long  pigTime  =0;
    private  ArrayList<Integer> pipeHeight;
    private int pipeCounter = 0;
    private int score;
    private boolean passed;
    private boolean  eggcheck;

    private boolean goldeggcheck;
    private boolean pigcheck;
    private MediaPlayer backgroundMusic;
    Text scoreText;
    
    private boolean isBounce;

   
   
    private Text livesText = new Text ();
    private int lives = 3;
    private Group gameoverScreen;
    private int lastPipe = -1;
 
//    private long p

    
    
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

        scoreText = new Text("Score: " + score);
        
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
   

//        
    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
   
        DEF.easyButton.setOnMouseClicked(e -> handleDifficultyButton("Easy"));
        DEF.mediumButton.setOnMouseClicked(e -> handleDifficultyButton("Medium"));
        DEF.hardButton.setOnMouseClicked(e -> handleDifficultyButton("Hard"));
        DEF.playagain.setOnMouseClicked(this::mouseClickHandler);
        
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
        pigAndTextContainer.getChildren().addAll(pigIconImageView, new Text("  Avoid Pigs"));

        // Add the container to the gameControl
        gameControl.getChildren().add(pigAndTextContainer);

        // Add some spacing
        gameControl.getChildren().add(new Text(""));  // Empty text for spacing
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
        isBounce = false;
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        pipes2 = new ArrayList<>();
        pipeHeight = new  ArrayList<>();
        eggs = new ArrayList<>();
        goldeggs = new ArrayList<>();
        pigs = new ArrayList<>();
        pipeHeight.add(25);
        pipeHeight.add(50); 
        pipeHeight.add(75);
        pipeHeight.add(100); 
        pipeHeight.add(125); 
        pipeHeight.add(150);
        pipeHeight.add(175);
        pipeHeight.add(200);
        //pipeHeight.add(225);
        pipeCounter = 0;
        score = 0;
        pipecount = 1;
        eggcount =0;
        
        
//        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + lives);
     
        
        
        
        
        
    	if(firstEntry) {
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("background");
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas);
            
            if (gameoverScreen ==  null ) { 
               
                gameoverScreen  = new  Group(); 
            }
    	}
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH ;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		
    	
    		Bird floor = new Bird(posX, posY, DEF.IMAGE.get("floor"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    		passed = false;
    	}
   
    	//initialize pipe 
	for(int i=0; i<DEF.pipe_COUNT; i++) {
	
    		int posX = i * (DEF.pipe_WIDTH + 200) +DEF.SCENE_WIDTH;
    		int posY = DEF.SCENE_HEIGHT- DEF.FLOOR_HEIGHT - DEF.pipe_HEIGHT;
    		
    		Pipe pipe = new Pipe(posX, posY, DEF.IMAGE.get("pipe"));
    		pipe.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		
    		

    		pipe.render(gc);
    		
    		pipes.add(pipe);
    	
    	}
    	
	for(int i=0; i<DEF.pipe_COUNT; i++) {
		
		int posX = i * (DEF.pipe_WIDTH + 200) +DEF.SCENE_WIDTH;

		int posY = 0;
		
		Pipe pipe2 = new Pipe(posX, posY, DEF.IMAGE.get("pipe2"));
		pipe2.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		pipe2.render(gc);
		
		pipes2.add(pipe2);
	}
	
	
	//initialize egg
for(int i=0; i<DEF.egg_COUNT; i++) {
		
	
		Bird egg = new Bird(-3000, -3000, DEF.IMAGE.get("whiteegg"));
		egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		egg.render(gc);
		eggs.add(egg);
		eggcheck = false;
	}


for(int i=0; i<DEF.egg_COUNT; i++) {
	
	
	Bird egg = new Bird(-3000, -3000, DEF.IMAGE.get("goldegg"));
	egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
	egg.render(gc);
	goldeggs.add(egg);
	goldeggcheck = false;
}
//initialize pig
for(int i=0; i<DEF.pig_COUNT; i++) {
	
	
	Pig pig = new Pig(-3000, 0 ,DEF.IMAGE.get("pig"));
	pig.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
	pig.render(gc);
	pigs.add(pig);
	pigcheck = false;

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
    	    	
    	    	 moveFloor();
    	    	 updatePipes();
    	    	
    	    	 updatePipes2();
    	    	
    	    	 moveBlob();
    	    
    	    	 checkCollision();
    	    
    	    	
//    	    	 System.out.println(score);
    	    	 
    	     }
    	 }
    	 
    	 private void score(int i) {
    		 if (pipes.get(i).getPositionX() <= blob.getPositionX() && passed== false) {
    		 score = score +1;
    		 passed = true;
    		 eggcount = eggcount +1;
    		 scoreText.setText("Score: " + score); 
    		 }
    		 
    	 }
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
    	 
    	 private void updatePipes() {
    		for(int i=0; i<DEF.pipe_COUNT; i++) {
    			movePipes(i);
    			updateEggs(i);
    		
    		}
    	 }
    	 
    	 
    	 private void updatePipes2() {
     		for(int i=0; i<DEF.pipe_COUNT; i++) {
     			movePipes2(i);
     			pigUpdate(i);
     		
     		}
     	 }
    	 	private void updateEggs(int i) {
    	 	
        		eggs.get(i).render(gc);
        		eggs.get(i).update(DEF.SCENE_SHIFT_TIME);
        		goldeggs.get(i).render(gc);
        		goldeggs.get(i).update(DEF.SCENE_SHIFT_TIME);
        		goldeggcheck = true;
        		eggcheck = true;
        		
        		
        	}
    	 private void movePipes(int i) {

     			if (pipes.get(i).getPositionX() <= -DEF.pipe_WIDTH) {
     				passed = false;
     		
     				double nextX = pipes.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

     	        	double nextY = DEF.SCENE_HEIGHT -DEF.FLOOR_HEIGHT- DEF.pipe_HEIGHT;
     	        	pipes.get(i).setPositionXY(nextX, nextY);
     	        	whiteEggAppear(i);
     	        	goldEggAppear( i); 
     	   		Random generator = new Random();
 				int randomIndex = generator.nextInt(pipeHeight.size());
                DEF.pipe_HEIGHT = pipeHeight.get(randomIndex);
               

     			}
     			
     			pipes.get(i).render(gc);
     			pipes.get(i).update(DEF.SCENE_SHIFT_TIME);
     			score(i);

     		}
     		
     	 
    	 private void whiteEggAppear(int i) {
<<<<<<< HEAD
<<<<<<< HEAD

    		  Random r = new Random();
    		    int result = r.nextInt(20); 

    		    if (result < whiteEggfreq && goldeggcheck == false && pigcheck == false) {
    		        eggs.get(i).setPositionXY(pipes.get(i).getPositionX(), pipes.get(i).getPositionY() - DEF.egg_HEIGHT);
    		        eggcheck = true;
    		    }
    	 }
=======
=======
>>>>>>> parent of e60b1d6 (code)
    		 Random r = new Random();
    		 int low = 1;
    		 int high = 20;
    		 int result = r.nextInt(high-low) + low;
    		if (result %2 ==0 && goldeggcheck == false && pigcheck == false) {
	    	    eggs.get(i).setPositionXY(pipes.get(i).getPositionX() ,pipes.get(i).getPositionY() - DEF.egg_HEIGHT);
	    	  eggcheck = true;
    		}
	    	
	    	}
>>>>>>> parent of e60b1d6 (code)
    	 private void goldEggAppear(int i ) {
    		 
    		 Random r = new Random();
    		 int low = 1;
    		 int high = 50;
    		 int result = r.nextInt(high-low) + low;
    		    if (result% 5 == 0 && eggcheck == false && pigcheck == false ) { // Egg count is a multiple of 5
    		      

    		 
    		
    		            goldeggs.get(i).setPositionXY(pipes.get(i).getPositionX(), pipes.get(i).getPositionY() - DEF.egg_HEIGHT);
    		            goldeggcheck = true;
    		        }
    		    }
    		

    	 
    	 private void movePipes2(int i) {
      		
      		
      			if (pipes2.get(i).getPositionX() <= -DEF.pipe_WIDTH) {

      				double nextX = pipes2.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

      	        	double nextY = 0;
      	        	pipes2.get(i).setPositionXY(nextX, nextY);
      	        	pigappear(i);
      	        	
      	      
      			}
      			pipes2.get(i).render(gc);
      			pipes2.get(i).update(DEF.SCENE_SHIFT_TIME);
      		
      		}
      	
     	 public void pigappear(int i) {
<<<<<<< HEAD

     		

=======
     		

>>>>>>> parent of e60b1d6 (code)
     		 Random r = new Random();
    		 int low = 1;
    		 int high = 50;
    		 int result = r.nextInt(high-low) + low;
         
     		if (result %4 ==0 && goldeggcheck == false && eggcheck ==false) {
	    	   pigs.get(i).setPositionXY(pipes.get(i).getPositionX() ,0);
	    	  pigcheck = true;
    		}
    

     
	
     	

   
     	 } 

 public void pigUpdate(int i) {
	 
	pigs.get(i).setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
	pigs.get(i).update(DEF.SCENE_SHIFT_TIME);
	pigs.get(i).render(gc);
	pigcheck = false;
    	 }
    	 
    	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 
			long diffTime = System.nanoTime() - clickTime;
			
			// blob flies upward with animation
<<<<<<< HEAD
<<<<<<< HEAD
			//if (isBounce == false ) {
			if (snoozecheck == false ) {
=======
>>>>>>> parent of e60b1d6 (code)
=======
>>>>>>> parent of e60b1d6 (code)
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

<<<<<<< HEAD
<<<<<<< HEAD
			}
			
			else {
				snooze();
			}

			for (Pipe pipe:pipes) {
			    if (blob.intersectsSprite(pipe)) {
			        blob.setVelocity(-500, 1000);
			        CLICKED = false;
			    }
			 
			}
			for (Pipe pipe: pipes2) {
			    if (blob.intersectsSprite(pipe)) {
                    blob.setVelocity(-500, 1000);
                    CLICKED = false;
                }
			}


=======
>>>>>>> parent of e60b1d6 (code)
=======
>>>>>>> parent of e60b1d6 (code)
			// render blob on GUI
			blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
			blob.render(gc);
    	 }
<<<<<<< HEAD
<<<<<<< HEAD
    	// }
=======
=======
>>>>>>> parent of e60b1d6 (code)
    	 
    	 

>>>>>>> parent of e60b1d6 (code)
    	 
    	 
    	 
    	 
    	 // possibly condense this code and also look into cropping the pipe image 
    	 public void checkCollision() {
    		 
    		 checkegg();
			for (Bird floor: floors) {
			    if ( blob.intersectsSprite(floor)) {
                    lives--;
                    if (lives<=0);
                    GAME_OVER = true;
                }
			}
			 for (Pipe pipe : pipes) {
<<<<<<< HEAD
<<<<<<< HEAD
//<<<<<<< HEAD
				 if(snoozecheck==false ) {
					    if ( blob.intersectsSprite(pipe)) {
		                    lives--;
		                    if (lives<=0);
		                    GAME_OVER = true;
		                }
				 }
//				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
				 }
			    
			 
			 for (Pipe pipe : pipes2) {
//<<<<<<< HEAD
				 if(snoozecheck==false ) {
					    if ( blob.intersectsSprite(pipe)) {
		                    lives--;
		                    if (lives<=0);
		                    GAME_OVER = true;
		                }
				 }
//				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
				 }
			    
			

=======
=======
>>>>>>> parent of e60b1d6 (code)
				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe);
			    }
			 
			 for (Pipe pipe2 : pipes2) {
				 GAME_OVER = GAME_OVER || blob.intersectsSprite(pipe2);
			    }
<<<<<<< HEAD
>>>>>>> parent of e60b1d6 (code)
=======
>>>>>>> parent of e60b1d6 (code)
			 for (Pig pig : pigs) {
			     if ( blob.intersectsSprite(pig)) {
//			         bounceBack();
			         lives--;
//	                    bounceBack();
	                    if (lives<=0);
	                    GAME_OVER = true;
	                }
			    }
			if (GAME_OVER) {
		        showHitEffect();
		        for (Bird floor : floors) {
		            floor.setVelocity(0, 0);
		        }
		        for (Pipe pipe : pipes) {
<<<<<<< HEAD
<<<<<<< HEAD
		        	
		            pipe.setVelocity(0, 0);
		        }
		        for (Pipe pipes2 : pipes2) {
		        	
=======
		            pipe.setVelocity(0, 0);
		        }
		        for (Pipe pipes2 : pipes2) {
>>>>>>> parent of e60b1d6 (code)
=======
		            pipe.setVelocity(0, 0);
		        }
		        for (Pipe pipes2 : pipes2) {
>>>>>>> parent of e60b1d6 (code)
		            pipes2.setVelocity(0, 0);
		        }
//		        
		        timer.stop();
		        if (lives <= 0) {
		            lives = 3;
		            gameOver();
		        }
		    }
			
			

//			
    	 }
<<<<<<< HEAD
    	 public void gameOver () {
    	     Text gameoverText = new Text ("You lost all your lives! ");
    	     
    	     gameoverText.setFont(Font.font("Times New Roman", FontWeight.SEMI_BOLD, 20));
    	     gameoverText.setFill(Color.RED);
    	     
    	     gameoverText.setLayoutX(200);
    	     gameoverText.setLayoutY(100);
    	     
//  
    	     VBox gameoverScreen = new VBox(20);
    	     gameoverScreen.setAlignment(Pos.CENTER);
//    	     gameoverScreen.setFill(Color.GREY);
    	     
    	     gameoverScreen.getChildren().addAll(gameoverText, DEF.playagain);
//    	     gameScene.getChildren()
    	     Scene gameoverScene = new Scene (gameoverScreen, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	     Stage gamestage = (Stage) gameScene.getScene().getWindow();
    	     gamestage.setScene(gameoverScene);
    	     GAME_OVER = true;
    	     
    	
    	     
    	     
    
    	 }
    	
    	

=======
    	 
>>>>>>> parent of e60b1d6 (code)
    	 public void checkegg() {
    		 
    		 for (Bird egg: eggs) {
                 if(blob.intersectsSprite(egg)) {
                     // taking egg out of scene
                     egg.setPositionXY(-3000, -3000);
                  
                    egg.render(gc);
<<<<<<< HEAD
<<<<<<< HEAD

                 score = score +3;
                 

               
                 

=======
                 score = score +5;
                    //eggcheck = true;
>>>>>>> parent of e60b1d6 (code)
=======
                 score = score +5;
                    //eggcheck = true;
>>>>>>> parent of e60b1d6 (code)
            
                 }
    	 }
    	 }
<<<<<<< HEAD
<<<<<<< HEAD
    	 

   
 public void checkgoldegg() {
    		 
	 for (Bird egg : goldeggs) {
	        if (blob.intersectsSprite(egg)) {
	    
	            snoozecheck = true;
	            snoozeStartTime = System.nanoTime();
	            egg.setPositionXY(-3000, -3000);
	            egg.render(gc);
	         
	            }
 }
	 }
 
 public void pigCollectsEgg() {
	
		 for(Pig pig : pigs) {
			 for (Bird egg : goldeggs) {
	        if (pig.intersectsSprite(egg)) {
	        	egg.setPositionXY(-3000, -3000);
                
                egg.render(gc);
	        	score= score -3;
	        }
	        }
	        }
	 
	
		 for(Pig pig : pigs) {
			 for (Bird egg : eggs) {
	        if (pig.intersectsSprite(egg)) {
	        	egg.setPositionXY(-3000, -3000);
                
                egg.render(gc);
	        	score= score -3;
	        }
	        }
	        }
	 
 }

    
    public void snooze() {
    	
    	
    	
    	
            double birdTopY = blob.getPositionY();
            double topPipeBottomY = pipes2.get(0).getPositionY() + DEF.pipe_HEIGHT;

           
            if (birdTopY <= topPipeBottomY) {
                blob.setPositionXY(DEF.pipe_WIDTH,topPipeBottomY );
            }



            blob.setVelocity(0, DEF.BLOB_FLY_VEL); 

            blob.render(gc); 
            
            long currentTime = System.nanoTime();
            long snoozeDuration = (currentTime - snoozeStartTime) / 1_000_000_000; 
            long remainingTime = Math.max(0, 6 - snoozeDuration); 

          
            String snoozeText = "Snooze: " + remainingTime + "s";
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            gc.fillText(snoozeText, 20, 40);

            if (snoozeDuration >= 6) {
                 
                snoozecheck = false; 
              
            }
      
    }
    
  
=======
   
>>>>>>> parent of e60b1d6 (code)
=======
   
>>>>>>> parent of e60b1d6 (code)
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

