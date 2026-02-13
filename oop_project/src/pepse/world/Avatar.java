package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.Objects;

public class Avatar extends GameObject {
    private static final float MOVE_SPEED = 300;
    private static final double ANIMATION_INTERVAL = 0.1;

    private static final String [] MOVING_ANIMATION = {"assets/yoshirun0.gif", "assets/yoshirun1.gif",
            "assets/yoshirun2.gif","assets/yoshirun3.gif","assets/yoshirun4.gif"};

    private static final String [] STILL_ANIMATION = {"assets/yoshistill0.gif", "assets/yoshistill1.gif",
            "assets/yoshistill2.gif"};

    private static final String [] FLOAT_ANIMATION = {"assets/yoshifloat0.gif", "assets/yoshifloat1.gif"};
    private static final String [] DOWN_ANIMATION = {"assets/yoshidown.gif"};
    private static final int ENERGY_POINTS = 100;

    private static final float FALL_SPEED = 1;
    private static final long JUMP_TIME = 300;
    private static final float JUMP_POWER = 4;

    private final UserInputListener inputListener;
    private final ImageReader imageReader;

    private boolean isJumping = false;
    private boolean isFlying = false;
    private boolean still = true;
    private long jumpStartTime = 0;
    private Counter counter;
    public static final float AVATAR_SIZE = 100;


    /**
     * Construct a new Avatar instance
     * @param topLeftCorner -position of the object, in window coordinates (pixels).
     *                       note that (0,0) is the top-left corner of the window.
     * @param dimensions    -width and height in window coordinates.
     * @param renderable    -the renderable representing the object. Can be null, in which case
     *                       the GameObject will not be rendered.
     * @param inputListener -input listener by which we decide the velocity of the avatar
     * @param imageReader   -image reader used to read animation's image
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader, Counter counter) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.counter = counter;
    }

    /**
     * This method defines the objects that could be collided with - all but sun sky and halo.
     * @param other The other GameObject.
     * @return - true unless other object has tag "sun", "sky", or "halo".
     */
    @Override
    public boolean shouldCollideWith(GameObject other){
        super.shouldCollideWith(other);
        final String []backgroundStringTags = new String[]{"sun", "halo", "sky"};
        for(String backgroundString : backgroundStringTags){
            if(Objects.equals(other.getTag(), backgroundString)){
                return false;
            }
        }
        return true;
    }

    /**
     * This method prevents yoshi from moving while colliding with collidable objects.
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision){
        super.onCollisionEnter(other, collision);
        setVelocity(Vector2.ZERO);
    }

    /**
     * Static function to create the Avatar for the game
     * @param gameObjects     -reference to objects in game
     * @param layer           -the layer of the avatar
     * @param topLeftCorner   -position of the object, in window coordinates (pixels).
     *                          note that (0,0) is the top-left corner of the window.
     * @param inputListener   -input listener by which we decide the velocity of the avatar
     * @param imageReader     -image reader used to read animation's image
     * @return created avatar instance
     */
    public static  Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                 UserInputListener inputListener, ImageReader imageReader, Terrain terrain){

        // creates energy counter
        Counter counter = new Counter(ENERGY_POINTS*2);
        TextRenderable text = new TextRenderable("Energy : " + counter.value());
        EnergyCounter energyCounter = new EnergyCounter(Vector2.ONES.mult(20),
                new Vector2(100,40), text, counter);
        energyCounter.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        //creates avatar
        AnimationRenderable animationRenderable = new AnimationRenderable(STILL_ANIMATION,imageReader,
                false, ANIMATION_INTERVAL);
        Avatar avatar = new Avatar(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE),
                animationRenderable, inputListener, imageReader, counter);
        avatar.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        gameObjects.addGameObject(avatar, layer);
        gameObjects.addGameObject(energyCounter, Layer.UI);
        avatar.setTag("avatar");
        return avatar;

    }

    /** Called once per frame. Any logic is put here.
     * Used to update object's state.
     *
     * @param deltaTime -The time, in seconds, that passed since the last invocation
     *                   of this method (i.e., since the last frame). This is useful
     *                   for either accumulating the total time that passed since some
     *                   event, or for physics integration (i.e., multiply this by
     *                   the acceleration to get an estimate of the added velocity or
     *                   by the velocity to get an estimate of the difference in position).
     */
    public void update(float deltaTime) {
        super.update(deltaTime);


        boolean leftPressed = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean rightPressed = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);

        // starts with gravity
        Vector2 movementDir = Vector2.DOWN.mult(FALL_SPEED);

        if(leftPressed) {
            movementDir = setRightLeftMovements(imageReader, movementDir, true);


        }
        if(rightPressed) {
            movementDir = setRightLeftMovements(imageReader, movementDir, false);
        }

        if(!(leftPressed||rightPressed)){
            if(getVelocity().equals(Vector2.ZERO)){
                if(!still){
                    setUpAnimation(imageReader, STILL_ANIMATION);
                    still = true;
                }
                if(inputListener.isKeyPressed(KeyEvent.VK_DOWN)){
                    setUpAnimation(imageReader, DOWN_ANIMATION);
                    still = false;
                }

                if(counter.value()<=ENERGY_POINTS*2-1){counter.increment();}
            }
        }

        // checks if jump is legal and starts jumping
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) ) {
            if(getVelocity().y() == 0) {
                setUpAnimation(imageReader, FLOAT_ANIMATION);
                still = false;
                isJumping = true;
                jumpStartTime = System.currentTimeMillis();
            }
                if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT )&&counter.value()>0) {
                    counter.decrement();
                    isJumping = false;
                    isFlying = true;
                    movementDir = movementDir.add(Vector2.UP.mult(JUMP_POWER));
                }
                else {
                    if(isFlying){isFlying = false;}
                }
            }

        //Space is not pressed so sets flying to false
        else {
            if (isFlying) {isFlying = false;}
        }

        //checks for when to stop going up for jump
        if (isJumping) {
            movementDir =checkJumpTime(movementDir);

        }

        setVelocity(movementDir.mult(MOVE_SPEED));

    }

    /*
    private method that handles jump by time passed
    */
    private Vector2 checkJumpTime( Vector2 movementDir) {

        if (System.currentTimeMillis() - jumpStartTime > JUMP_TIME) {
            isJumping = false;
        }

        else {
            movementDir = movementDir.add(Vector2.UP.mult(JUMP_POWER));
        }
        return movementDir;
    }


    /*
    private method that sets the animation of the avatar
     */
    private void setUpAnimation(ImageReader imageReader, String [] animation_path){
        AnimationRenderable animationRenderable = new AnimationRenderable(animation_path,imageReader,
                false, ANIMATION_INTERVAL);
        renderer().setRenderable(animationRenderable);
    }

    /*
    private method that sets the right and left movements of the avatar
     */
    private Vector2 setRightLeftMovements(ImageReader imageReader, Vector2 movementDir, boolean direction){
        if(getVelocity().equals(Vector2.ZERO)){

            setUpAnimation(imageReader, MOVING_ANIMATION);
            still = false;
        }
        if (getVelocity().y()==0)
        {
            if(counter.value()<=ENERGY_POINTS*2-1){
                counter.increment();
            }
        }

        renderer().setIsFlippedHorizontally(direction);
        if(direction)
        {
            return movementDir.add(Vector2.LEFT);
        }

        return movementDir.add(Vector2.RIGHT);
    }

}


