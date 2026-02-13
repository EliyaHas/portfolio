package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Block;
import pepse.world.Sky;

import pepse.world.Terrain;

import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;

import java.time.Clock;

public class PepseGameManager extends GameManager {
    private Terrain terrain;
    private Vector2 windowDimensions;
    public static int WINDOW_BLOCK_SIZE;
    /*the window block size is used both by the Tree and the Terrain classes
     * It is used to surmise the y coordinates of blocks rendered on screen*/
    private Tree trees;

    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        // constants
        this.windowDimensions = windowController.getWindowDimensions();
        WINDOW_BLOCK_SIZE = (int)windowDimensions.y() / Block.SIZE;

        // create sky
        Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);

        //create sun, sun halo and night
        GameObject sun = Sun.create(gameObjects(),Layer.BACKGROUND, windowDimensions,30);
        SunHalo.create(gameObjects(),Layer.BACKGROUND+10,sun, new Color(255,255,0,20));
        Night.create(gameObjects(),Layer.FOREGROUND, windowDimensions,30);

        // get random seed based on time
        int seed = (int)Clock.systemDefaultZone().instant().getEpochSecond();

        // generate terrain
        this.terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS, windowDimensions, seed);
        setCamera(new Camera(Vector2.ZERO, windowDimensions, windowDimensions));
        getCamera().setVelocity(new Vector2(-100, 0)); // TODO: this is just to show off

        // create avatar
        float avatarXLocation = windowController.getWindowDimensions().x() / 2;
        Avatar avatar = Avatar.create(gameObjects(), Layer.DEFAULT, windowDimensions.add
                        (new Vector2(avatarXLocation,
                                -((terrain.groundHeightAt(avatarXLocation)) + Block.SIZE * 2 + Avatar.AVATAR_SIZE))),
                inputListener, imageReader, terrain);

        // sets camera to follow avatar
        setCamera(new Camera(avatar, Vector2.ZERO.add(new Vector2(0,-300)),
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        // generate trees
        this.trees = new Tree(this.terrain, gameObjects(), Layer.DEFAULT, seed);
    }

    /**
     * In this method all objects are updated
     * Terrain: the new space in the frame is terrained(except for the last column)
     *
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime){
        super.update(deltaTime);

        final int TREE_CALC_OFFSET = 300;
         // update Terrain
        int rangeStart = (int)camera().getCenter().x() - (int)windowDimensions.x() / 2;
        int rangeEnd = (int)camera().getCenter().x() + (int)windowDimensions.x() / 2;
        terrain.createInRange(rangeStart, rangeEnd);

        // update trees
        trees.createInRange(rangeStart - TREE_CALC_OFFSET, rangeEnd + TREE_CALC_OFFSET);
    }

    public static void main(String[] args){
        new PepseGameManager().run();
    }
}
