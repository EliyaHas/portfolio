package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    /**
     * Static function to create the SunHalo for the game
     * @param gameObjects -reference to objects in game
     * @param layer       -the layer of the SunHalo
     * @param sun         -the sun SunHalo needs to follow
     * @param color       -color of the SunHalo
     * @return created SunHalo instance
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun,
                                    Color color) {

        //TrackerObject has override update to track the given object to track
        TrackerObject halo = new TrackerObject(Vector2.ZERO,sun.getDimensions().mult(2f),
                new OvalRenderable(color),sun);
        halo.setCenter(new Vector2(sun.getCenter()));


        // make halo move with camera
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(halo, layer);
        sun.setTag("halo");
        return halo;
    }
static class TrackerObject extends GameObject{
    private GameObject objectToTrack;

    /**
     * Construct a new TrackerObject instance
     * @param topLeftCorner -position of the object, in window coordinates (pixels).
     *                       note that (0,0) is the top-left corner of the window.
     * @param dimensions    -width and height in window coordinates.
     * @param renderable    -the renderable representing the object. Can be null, in which case
     *      *                       the GameObject will not be rendered.
     * @param objectToTrack - gameObject we wish to follow
     */
    public TrackerObject(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                         GameObject objectToTrack) {
        super(topLeftCorner, dimensions, renderable);
        this.objectToTrack = objectToTrack;
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
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.setCenter(objectToTrack.getCenter());
    }
}
    }
