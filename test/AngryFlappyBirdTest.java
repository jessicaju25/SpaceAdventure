import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import angryflappybird.AngryFlappyBird;
import angryflappybird.Defines;
import angryflappybird.Sprite;

class AngryFlappyBirdTest {

    @BeforeAll
    static void initJfxRuntime() {
        Platform.startup(() -> {
        });
    }

    @BeforeEach
    void setUp() throws Exception {
    }

    @Test
    void testMain() {

    }

    @Test
    void testStartStage() {
        //Testing Game Set up
        
        //In this test game, we are testing if our resetGameScene is functioning correctly
        AngryFlappyBird testGame = new AngryFlappyBird();
        //Test if we start with 3 lives
        assertEquals(3, testGame.getLives());
        testGame.resetGameScene(true);
        //Test if the score resets to 0 with our reset game scene 
        assertEquals(0,testGame.getScore());
        
        
        //Check if BIRD was inilizaed in the right place when resetGameScene
        assertEquals(Defines.BLOB_POS_X, testGame.blob.getPositionX());
        assertEquals(Defines.BLOB_POS_Y, testGame.blob.getPositionY());
        
        //Check if EGGS were initilized correctly 
        assertNotNull(testGame.goldeggs); 
        assertEquals(2, testGame.goldeggs.size() ); 
        assertFalse (testGame.isGameOver());
        
        
    
        //Testing our start button
        testGame.mouseClickHandler(null); 
        assertTrue(testGame.GAME_START);
        assertTrue(testGame.CLICKED);
        assertEquals(0,testGame.clickTime);
        
   
        
        //Testing out the level of different difficulties buttons  
        
      
                //Easy Mode
        testGame.handleDifficultyButton("Easy");
        assertEquals (5 , Defines.SCENE_SHIFT_TIME);
        assertEquals (20  * 1_000_000_000L, testGame.pigAppearanceInterval);
       
                //Medium mode
        testGame.handleDifficultyButton("Medium");
        assertEquals ( 13, Defines.SCENE_SHIFT_TIME);
        assertEquals (15 * 1_000_000_000L, testGame.pigAppearanceInterval);
                //Hard mode
        testGame.handleDifficultyButton("Hard");
        assertEquals ( 20, Defines.SCENE_SHIFT_TIME);
        assertEquals ( 10 * 1_000_000_000L, testGame.pigAppearanceInterval);
     
        
        //Testing if Snooze component is working correctly
   
        testGame.resetGameScene(true);
                //Make bird get the gold egg to start snooze 
        testGame.goldeggs.get(0).setPositionXY(testGame.blob.getPositionX(), testGame.blob.getPositionY() );
        testGame.snooze();
        assertFalse(testGame.snoozecheck);
        testGame.resetGameScene(true);
        
        //Test if white eggs give 3 more points 
        
        testGame.eggs.get(0).setPositionXY(testGame.blob.getPositionX() , testGame.blob.getPositionY() );
        testGame.checkegg();
        assertEquals(3, testGame.getScore()); //Should have gained 3 points
        
        //Test if pigs take points away when it gets white eggs before bird
        
        testGame.pigs.get(0).setPositionXY(testGame.blob.getPositionX(), 0); 
        testGame.eggs.get(0).setPositionXY(testGame.blob.getPositionX(), testGame.blob.getPositionY() );
        testGame.pigCollectsEgg();
        
        assertEquals(3, testGame.getScore() );
 
        
        

    }



    }


