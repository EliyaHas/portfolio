package pepse.world;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

 class EnergyCounter extends GameObject {
     private static final double CONSUME_SPEED = 0.5;
     private final Counter counter;

     /**
      * Construct a new EnergyCounter instance
      * @param topLeftCorner -position of the object, in window coordinates (pixels).
      *                       note that (0,0) is the top-left corner of the window.
      * @param dimensions    -width and height in window coordinates.
      * @param renderable    -the renderable representing the object. Can be null, in which case
      *                       the GameObject will not be rendered.
      * @param counter       - counter used to show and recover energy
      */
    public EnergyCounter(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Counter counter) {
        super(topLeftCorner, dimensions, renderable);
        this.counter = counter;
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
        TextRenderable text = new TextRenderable("Energy : " + (double)counter.value()*CONSUME_SPEED);
        renderer().setRenderable(text);
    }
}
