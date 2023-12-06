package angryflappybird;

import java.util.HashMap;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 50;    		// the blob drop velocity    //change to 50 from 300
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
 
    final int pipe_WIDTH = 100;
     int pipe_HEIGHT = 100;
    final int pipe_COUNT = 2;
    
    
    
//    final int pipe_HEIGHT = 400;
//    final int pipe_POS_X = 300;  // Updated position to appear on the right side of the screen
//    final int pipe_POS_Y = SCENE_HEIGHT - FLOOR_HEIGHT - pipe_HEIGHT; // Adjusted to fit above the floor
//    final int pipe_WIDTH = 70;  // Reduced width for the pipe
//     // Increased height for the pipe
//    final int pipe_COUNT = 1;   // Number of pipes
  
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";
    final String[] IMAGE_FILES = {"background","background0" , "blob0", "blob1", "blob2", "blob3", "floor","unitytut-pipe","unitytut-pipe2"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    //nodes on the scene graph
    Button startButton;
    
    //nodes for level of difficulty 
    Button easyButton;
    Button mediumButton;
    Button hardButton;
    
    // constructor
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 6) {
				img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT,  false, false);
			}
			else if (i == 2 || i == 3 || i == 4 || i == 5){
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
			}
			
			else if (i == 7  || i == 8 ) {
				img = new Image(pathImage(IMAGE_FILES[i]), pipe_WIDTH, pipe_HEIGHT, false, false);
			}

			else {
				img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
			}
    		IMAGE.put(IMAGE_FILES[i],img);
    	}
		
		// initialize image views
		for(int i=0; i<IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		// initialize scene nodes
		startButton = new Button("Go!");
		//initialize difficulty nodes
		
		easyButton = new Button("Easy");
		mediumButton = new Button("Medium");
		hardButton = new Button("Hard");
	}
	
	public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }
	
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }
}
