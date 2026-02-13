package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;


public class Night {
    private static final Color NIGHT_COLOR = Color.black;
    private static final Float MIDNIGHT_OPACITY = 0.5f;
    private static final Float MIDDAY_OPACITY = 0f;

    /**
     * Static function to create the Night cycle for the game
     * @param gameObjects       -reference to objects in game
     * @param layer             -the layer of the night
     * @param windowDimensions  -the dimensions of the game window
     * @param cycleLength       -length of the night cycle
     * @return created Night instance
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength) {

        // initiate night object
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(NIGHT_COLOR));

        // night moves with camera
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        // setup transition of a day cycle
        new Transition<>(night, night.renderer()::setOpaqueness, MIDDAY_OPACITY,
                MIDNIGHT_OPACITY, Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength/2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);

        gameObjects.addGameObject(night, layer);
        night.setTag("night");
        return night;
    }
}

