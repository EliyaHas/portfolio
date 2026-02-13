package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.util.Random;

import static java.lang.Math.*;

public class LeafObject extends GameObject {
    private static final float MAX_TIME_TO_DIE = 5;
    private static final float MIN_ZIGZAG_RADIUS = 10;
    private static final float MAX_ZIGZAG_RADIUS = 30;
    private static final float MAX_LEAF_HEIGHT = 300;
    private static final float MAX_TIME_TO_FALL = 10;
    private static final float MAX_TIME_TO_FADE = 5;
    private static final float MIN_TIME_TO_FALL = 5;
    private final float LEAF_OPAQUENESS;
    private final float TIME_TO_FADE;
    private static final float SLOW_MO_FACTOR = 5;
    private final float ZIGZAG_RADIUS;
    private final Random random;
    private final Vector2 originalPosition;
    private Transition<Float> transition;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public LeafObject(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, int perlinInt) {
        super(topLeftCorner, dimensions, renderable);
        this.originalPosition = this.getCenter();
        this.random = new Random(perlinInt);

        // set angle
        float LEAF_ANGLE = (random.nextFloat() * 1000 % 10 - 10);//set angle at range [-+pi/4]
        this.renderer().setRenderableAngle(LEAF_ANGLE);
        LEAF_OPAQUENESS = 1 - random.nextFloat() * 1000 % 10 * 0.05f;
        this.renderer().setOpaqueness(LEAF_OPAQUENESS);

        /*falling parameters*/
        this.TIME_TO_FADE = random.nextFloat() * MAX_TIME_TO_FADE;
        this.ZIGZAG_RADIUS = MIN_ZIGZAG_RADIUS + random.nextFloat() * MAX_ZIGZAG_RADIUS;

        reincarnate(); // start life cycle
    }

    /**
     * Override collision matcher method to make leaves collide with terrain blocks.
     */
    @Override
    public boolean shouldCollideWith(GameObject other){
        return other instanceof Block;
    }

    /**
     * Override collision action method to make leaves stop falling once they hit the terrain.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        this.removeComponent(transition);
    }
/*----------Life Cycle in 3 Methods----------*/
/*
the naming system was partly inspired by the latest James Bond movie
*/
    /**
     * this 1st method resets position and opaqueness of leaf and then schedules fall at timeToFall.
     */
    private void reincarnate() {
        float timeToFall = MIN_TIME_TO_FALL + random.nextFloat() * MAX_TIME_TO_FALL;
        this.setCenter(originalPosition); // reset posit
        this.renderer().setOpaqueness(LEAF_OPAQUENESS);
        new ScheduledTask(this, timeToFall, false, this::fallAndFade);
    }

    /**
     * the 2nd method resets position and opaqueness of leaf and then calls fall.
     */
    private void fallAndFade(){
        transition = new Transition<>(this, this::movement, 0f,
                1f, Transition.LINEAR_INTERPOLATOR_FLOAT, TIME_TO_FADE,
                Transition.TransitionType.TRANSITION_ONCE, null);
        this.renderer().fadeOut(TIME_TO_FADE, this::die);// set fade
    }

    /**
     * finally the 3rd method schedules the reincarnation of the leaf by calling reincarnate at timeToDie.
     */
    private void die(){
        float timeToDie = random.nextFloat() * MAX_TIME_TO_DIE;
        new ScheduledTask(this, timeToDie, false, this::reincarnate);
    }

    /**
     * This method defines the movement of a falling leaf
     * The movement is downwards and sinusoidal in the x direction.
     * @param time - time elapsed since fall began.
     */
    private void movement(float time){
        // time is factored so is to slow down the sin movement
        float slowTime = time / SLOW_MO_FACTOR;
        float xCoordinate = originalPosition.x() + ZIGZAG_RADIUS * (float)sin(2 * PI * slowTime);
        float yCoordinate = originalPosition.y() + time * MAX_LEAF_HEIGHT;
        Vector2 placement = new Vector2(xCoordinate, yCoordinate);
        this.setCenter(placement);
    }
}