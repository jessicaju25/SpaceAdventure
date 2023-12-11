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
    
    //coefficients related to pipes
    final int pipe_WIDTH = 100;
     int pipe_HEIGHT = 100;
     int pipe_HEIGHT1 = 500;
    final int pipe_COUNT = 2;

    //coefficients related to white egg
    final int wegg_WIDTH = 100;
    int wegg_HEIGHT = 100;
    final int wegg_COUNT = 1;

    final int egg_WIDTH = 70;
    final int egg_HEIGHT = 70;
    final int egg_COUNT = 2;

  
   //coefficients related to yellow egg
   final int gegg_WIDTH = 100;
   int gegg_HEIGHT = 100;
   final int gegg_COUNT = 1;
   
   // coefficients related to the pig
   final int pig_WIDTH = 70;
   final int pig_HEIGHT = 70;
   final int pig_POS_X = 70;
   final int pig_POS_Y = 200;
   final int pig_DROP_TIME = 300000000;  	
   final int pig_DROP_VEL = 30;    		
   final int pig_IMG_LEN = 1;
   final int pig_IMG_PERIOD = 5;
   
   
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";


    final String[] IMAGE_FILES = {"background","background0" , "blob0", "blob1", "blob2", "blob3", "floor","pipe","pipe2", "whiteegg","whiteegg","goldegg", "pig"};



    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    // Small icon dimensions for white egg (for on the console)
    final int weggicon_WIDTH = 30;
    final int weggicon_HEIGHT = 30;
    
    // Small icon dimensions for yellow egg (for on the console)
    final int geggicon_WIDTH = 30;
    final int geggicon_HEIGHT = 30;
    
 // Small icon dimensions for pig (for on the console)
    final int pigicon_WIDTH = 30;
    final int pigicon_HEIGHT = 30;
    
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
			
			else if (i == 8  ) {
				img = new Image(pathImage(IMAGE_FILES[i]), pipe_WIDTH, pipe_HEIGHT, false, false);
			}
			else if (i == 7  ) {
	                img = new Image(pathImage(IMAGE_FILES[i]), pipe_WIDTH, pipe_HEIGHT1, false, false);
	            }

			
			else if(i==10) {
				
				img = new Image(pathImage(IMAGE_FILES[i]), wegg_WIDTH, wegg_HEIGHT, false, false);
			}
			
			else if(i==11) {
				img = new Image(pathImage(IMAGE_FILES[i]), gegg_WIDTH, gegg_HEIGHT, false, false);
			}
			
			else if(i==12) {
				img = new Image(pathImage(IMAGE_FILES[i]), pig_WIDTH, pig_HEIGHT, false, false);
			}
			else if (i == 9) {
				img = new Image(pathImage(IMAGE_FILES[i]), egg_WIDTH, egg_HEIGHT, false, false);

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
