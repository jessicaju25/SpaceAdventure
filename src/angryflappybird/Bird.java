package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
//Moves when user clicks button
	// moves through pipe collects point
	// touches the floor, pipe, pig loses a life
	// touches the yellow egg- five second free fall for 6 seconds 
	//touches regular eggs gets extra points (5 points)
	//question touch a pig game over or loss a life 
	
	
	
	
	
	///what other application would the bird class have 
public class Bird implements Sprite  {

	  private Image image;
	    private double positionX;
	    private double positionY;
	    private double velocityX;
	    private double velocityY;
	    private double width;
	    private double height;
	    //private String IMAGE_DIR = "../resources/images/";

	    public Bird() {
	        this.positionX = 0;
	        this.positionY = 0;
	        this.velocityX = 0;
	        this.velocityY = 0;
	    }
	   
	    public Bird(double pX, double pY, Image image) {
	    setPositionXY(pX, pY);
	        setImage(image);
	        this.velocityX = 0;
	        this.velocityY = 0;
	    }

	    public void setImage(Image image) {
	        this.image = image;
	        this.width = image.getWidth();
	        this.height = image.getHeight();
	    }
	    
	    public void removeImge() {
	    	this.image = null;
	    }
	    

	    public void setPositionXY(double positionX, double positionY) {
	        this.positionX = positionX;
	        this.positionY = positionY;
	    }

	    public double getPositionX() {
	        return positionX;
	    }

	    public double getPositionY() {
	        return positionY;
	    }

	    public void setVelocity(double velocityX, double velocityY) {
	        this.velocityX = velocityX;
	        this.velocityY = velocityY;
	    }

	    public void addVelocity(double x, double y) {
	        this.velocityX += x;
	        this.velocityY += y;
	    }

	    public double getVelocityX() {
	        return velocityX;
	    }

	    public double getVelocityY() {
	        return velocityY;
	    }

	    public double getWidth() {
	        return width;
	    }

	    public void render(GraphicsContext gc) {
	        gc.drawImage(image, positionX, positionY);
	    }

	    public Rectangle2D getBoundary() {
	        return new Rectangle2D(positionX, positionY, width, height);
	    }

	    public boolean intersectsSprite(Sprite s) {
	        return s.getBoundary().intersects(this.getBoundary());
	    }

	    public void update(double time) {
	        positionX += velocityX * time;
	        positionY += velocityY * time;
	    }
	    
	}
	


