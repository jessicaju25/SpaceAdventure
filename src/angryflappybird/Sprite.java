package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
/** This class contains for the applications of the game objects ( bird, pipes, pig, eggs and floor */
public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    //private String IMAGE_DIR = "../resources/images/"; Delete already defined in the defines class 

    public Sprite(Image image ,double positionX, double positionY, double velocityX, double velocityY, double width, double height) {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    void setImage(Image image) {
       this.image = image;
       this.width = image.getWidth();
       this.height = image.getHeight();
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

    void setVelocity(double velocityX, double velocityY) {
       this.velocityX = velocityX;
       this.velocityY = velocityY;
    }

    void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
}


    double getVelocityX() {
        return velocityX;
    }

    double getVelocityY() {
     return velocityY;
    }

    double getWidth() {
      return width;
    } 

    void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    Rectangle2D getBoundary() {
      return new Rectangle2D(positionX, positionY, width, height);
    }

    boolean intersectsSprite(Sprite s) {
      return s.getBoundary().intersects(this.getBoundary());
    }

    void update(double time) {
        positionX += velocityX * time;
       positionY += velocityY * time;
    }
}
