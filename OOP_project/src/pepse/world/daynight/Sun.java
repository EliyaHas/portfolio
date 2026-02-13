package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

public class Sun {
    private static final Color SUN_COLOR = Color.YELLOW;
    private static final float SUN_SIZE = 200;
    private static final float HEIGHT_OFFSET = 20;
    private static final double CYCLE_START_DEGREE = -90;
    private static final double CYCLE_END_DEGREE = 270;

    /**
     * Static function to create the sun cycle for the game
     * @param gameObjects       -reference to objects in game
     * @param layer             -the layer of the sun
     * @param windowDimensions  -the dimensions of the game window
     * @param cycleLength       -length of the sun cycle
     * @return created Sun instance
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {

        // initiate sun object
        GameObject sun = new GameObject(Vector2.ZERO,Vector2.ONES.mult(SUN_SIZE),
                new OvalRenderable(SUN_COLOR));
                sun.setCenter(new Vector2(windowDimensions.x()/2, SUN_SIZE*2));

        // Sun moves with camera
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        // function to calculate sun's placement by degrees
        Consumer<Float> movement = degree -> {
            float width = windowDimensions.x() / 2;
            float height = windowDimensions.y() / 2 - HEIGHT_OFFSET;
            Vector2 placement = windowDimensions.mult(0.5f).add(new Vector2(
                    (float) (width * Math.cos(degree)), (float) (height * Math.sin(degree))));
            sun.setCenter(placement);
        };

        //setup transition to move sun
        new Transition<>(sun, movement, (float)Math.toRadians(CYCLE_START_DEGREE),
                (float)Math.toRadians(CYCLE_END_DEGREE), Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP, null);

        gameObjects.addGameObject(sun, layer);
        sun.setTag("sun");
        return sun;
    }
}
