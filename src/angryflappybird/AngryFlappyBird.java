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
    private MediaPlayer backgroundMusic;
    private Text scoreText = new Text ();
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
        String backgroundMusicFile = "backgroundMusic.mp3";
        Media music = new Media (new File(backgroundMusicFile).toURI().toString());
        backgroundMusic = new MediaPlayer(music);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();
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

    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
   
        DEF.easyButton.setOnMouseClicked(e -> handleDifficultyButton("Easy"));
        DEF.mediumButton.setOnMouseClicked(e -> handleDifficultyButton("Medium"));
        DEF.hardButton.setOnMouseClicked(e -> handleDifficultyButton("Hard"));
        
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton);
//        scoreText = new Text("Score: 0" );
//        scoreText.setFont(Font.font( "Times New Roman", FontWeight.BOLD, 30));
//        scoreText.setFill(Color.BLACK);
//        
//        scoreText.setLayoutX(DEF.SCENE_WIDTH - 390);
//        scoreText.setLayoutY(35);
//        gameControl.getChildren().add(scoreText);
        
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
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        pipes2 = new ArrayList<>();
        pipeHeight = new  ArrayList<>();
        eggs = new ArrayList<>();
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

//initialize pig
for(int i=0; i<DEF.pig_COUNT; i++) {
	
	
	Pig pig = new Pig(-3000, 0 ,DEF.IMAGE.get("pig"));
	pig.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
	pig.render(gc);
	pigs.add(pig);

}


//
//pig = new Pig(-3000, 0 ,DEF.IMAGE.get("pig"));
////pig.setVelocity(sceneVelocity, pigDropVelocity);z
////pig.render(gc);
//	//Y postion randomized 
//	//array lit 
//        
//>>>>>>> 05d061acccdecd521e0eb214f527caeb4260e70b
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
    	    	
    	    	 movePipes2();
    	    	
    	    	 moveBlob();
    	    
    	    	 checkCollision();
    	    
    	    	 pigUpdate();
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
    	 	private void updateEggs(int i) {
    	 	
        		eggs.get(i).render(gc);
        		eggs.get(i).update(DEF.SCENE_SHIFT_TIME);
        		
        	}
    	 private void movePipes(int i) {

     			if (pipes.get(i).getPositionX() <= -DEF.pipe_WIDTH) {
     				passed = false;
     		
     				double nextX = pipes.get((i+1)%DEF.pipe_COUNT).getPositionX() + DEF.pipe_WIDTH + 200;

     	        	double nextY = DEF.SCENE_HEIGHT -DEF.FLOOR_HEIGHT- DEF.pipe_HEIGHT;
     	        	pipes.get(i).setPositionXY(nextX, nextY);
     	        	whiteEggAppear(i);
		 	   		Random generator = new Random();
					int randomIndex = generator.nextInt(pipeHeight.size());
		            DEF.pipe_HEIGHT = pipeHeight.get(randomIndex);
		           
     			}
     			
     			pipes.get(i).render(gc);
     			pipes.get(i).update(DEF.SCENE_SHIFT_TIME);
     			score(i);
//<<<<<<< HEAD
//
//=======
//     	          
//>>>>>>> 05d061acccdecd521e0eb214f527caeb4260e70b
     		}
     		
     	 
    	 private void whiteEggAppear(int i) {
//<<<<<<< HEAD
    		 
    		if (eggcount %2 ==0) {
	    	    eggs.get(i).setPositionXY(pipes.get(i).getPositionX() ,pipes.get(i).getPositionY() - DEF.egg_HEIGHT);
	    	  
    		}
	    	
//=======
    		 Random random = new Random();
    		 int randomNumber = random.nextInt(15); // Generate a random number between 0 and 15
    		 //show egg when the random number is less than 5 (33%)
    		 if(randomNumber < 5) {
	    	    eggs.get(i).setPositionXY(pipes.get(i).getPositionX() ,pipes.get(i).getPositionY() - DEF.egg_HEIGHT);
	    	    eggs.get(i).render(gc);
    		 }
//>>>>>>> 05d061acccdecd521e0eb214f527caeb4260e70b
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
     	 
     	 public void pigappear(int i) {
//     		
     		 
     		if (pigs.get(i).getPositionX() < -20) {
     	        Random generator = new Random();
     	        int randomInt = lastPipe;
     	        while (randomInt == lastPipe) {
     	           randomInt = generator.nextInt(pipes.size()); // Use pipes.size() instead of DEF.pipe_COUNT
     	           System.out.println("Hi");
     	        }
     	        lastPipe = randomInt;
     	        

     	        Pipe randomPipe = pipes.get(randomInt);
     	        pigs.get(i).setPositionXY(randomPipe.getPositionX(), 0);
     	        
     	    }
     	    
     	    pigs.get(i).setVelocity(DEF.SCENE_SHIFT_INCR, DEF.pig_DROP_VEL);
     	    pigs.get(i).update(DEF.SCENE_SHIFT_TIME);
     	    pigs.get(i).render(gc);
//     	    
     	 } 


 public void pigUpdate() {
	 
	 for ( int i =0; i< DEF.pig_COUNT; i++) {
	 pigappear(i);
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
    	 
    	 

    	 
    	 
    	 
    	 
    	 // possibly condense this code and also look into cropping the pipe image 
    	 public void checkCollision() {
    		 
    		 checkegg();
			for (Bird floor: floors) {
			    if ( blob.intersectsSprite(floor)) {
                    lives--;
                    if (lives<=0);
                    GAME_OVER = true;
                }
//				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			 for (Pipe pipe : pipes) {
			     if ( blob.intersectsSprite(pipe)) {
	                    lives--;
	                    if (lives<=0);
	                    GAME_OVER = true;
	                }
			 }
			 for (Pipe pipe2 : pipes2) {
			     if ( blob.intersectsSprite(pipe2)) {
	                    lives--;
	                    if (lives<=0);
	                    GAME_OVER = true;
	                }			    }
			 for (Pig pig : pigs) {
			     if ( blob.intersectsSprite(pig)) {
	                    lives--;
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
		            pipe.setVelocity(0, 0);
		        }
		        for (Pipe pipes2 : pipes2) {
		            pipes2.setVelocity(0, 0);
		        }
//		        
		        timer.stop();
		        if (lives <= 0) {
		            lives = 3;
		        }
		    }
			
			
//			gameoverScreen = new Group();
//            Text gameoverText = new Text ("Oh no. You lost all your lives! ");
//            gameoverText.setFont(Font.font ("Times New Roman", FontWeight.EXTRA_BOLD,50));
//            gameoverText.setFill(Color.RED);
////          gameoverText.size
//            gameoverText.setLayoutX(200);
//            
//            gameoverText.setLayoutY(100);
//            gameoverScreen.getChildren().add(gameoverText);
////            Button playagain = new Button("Play Again?");
//            DEF.playagain.setOnAction( e -> resetGameScene(true));
//            gameoverScreen.getChildren().addAll(gameoverText, DEF.playagain);
//            DEF.playagain.setLayoutX(100);
//            DEF.playagain.setLayoutX(200);
//            
//            ((HBox)gameScene.getParent()).getChildren().add(gameoverScreen);
//			
    	 }
    	 
    	 public void checkegg() {
    		 
    		 for (Bird egg: eggs) {
                 if(blob.intersectsSprite(egg)) {
                     // taking egg out of scene
                     egg.setPositionXY(-3000, -3000);
                     score =score +5;
                    egg.render(gc);
                 score = score +5;
                    eggcheck = true;
            
                 }
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

