package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;

import java.awt.*;

public class Terrain {
    private static final int BLOCK_FLOOR_OFFSET = 1; // ground will always be at least this height
    private final int COLUMN_OFFSET = 3;
    private final int NOISE_SCALE_UP;
    private final float AVERAGE_HEIGHT;
    private final int NOISE_X_RANGE_SCALING;
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final PerlinNoise perlinNoise;
    private final int[] terrainedSpace = {0, -90}; // this ensures offset columns are terrained at start
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final Block[][] terrainCache;
    private int positiveBlockColumnIndex = -1;
    private int negativeBlockColumnIndex = 0;
    private int blockColumnIndex = 0;

    /**
     * The Terrain class constructs the terrain of the level out of Block objects.
     *
     * @param gameObjects      - reference to objects in game
     * @param groundLayer      - the layer of the ground
     * @param windowDimensions - the dimensions of the window
     * @param seed             - a random number to generate the terrain height using a perlin noise function.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer, Vector2 windowDimensions,
                   int seed) {
        // initialize parameters
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.perlinNoise = new PerlinNoise(seed);

        // noise parameters
        this.NOISE_X_RANGE_SCALING = 500;
        this.AVERAGE_HEIGHT = 25;
        this.NOISE_SCALE_UP = 15;

        // initialize block array
        int BLOCKS_IN_FRAME = (int)windowDimensions.x() / Block.SIZE + COLUMN_OFFSET;
        terrainCache = new Block[BLOCKS_IN_FRAME][];
    }

    /**
     * This method creates the terrain out of Block objects in the range given.
     * the method iterates over the range given in block size hops, and calculates
     * the height at the point, employing the perlin noise function, from the PerlinNoise class.
     * the height is then rounded to the block size, and the blocks at the point are rendered to fill
     * the height.
     * @param minX - range start
     * @param maxX - range end
     */
    public void createInRange(int minX, int maxX) {

        // set min/maxX to grid locations
        minX = (minX / Block.SIZE - COLUMN_OFFSET) * Block.SIZE;
        maxX = (maxX / Block.SIZE + COLUMN_OFFSET) * Block.SIZE;

        // set necessary range(not intersecting with existing range)
        boolean velocityDirection = maxX > terrainedSpace[1];
        float actualMaxX = velocityDirection ? maxX : terrainedSpace[0];
        float actualMinX = velocityDirection ? terrainedSpace[1] : minX;

        // iterate over all x's in range(Block.SIZE hops)
        for(; actualMinX <= actualMaxX; actualMinX += Block.SIZE){

            // set block indices
            positiveBlockColumnIndex += velocityDirection ? 1 : -1;
            negativeBlockColumnIndex += velocityDirection ? 1 : -1;
            blockColumnIndex = velocityDirection ? positiveBlockColumnIndex : negativeBlockColumnIndex;
            blockColumnIndex = mod(blockColumnIndex, terrainCache.length); // pick index

            renderPoleAtX(actualMinX);
        }

        // update terrained space array
        terrainedSpace[1] = maxX;
        terrainedSpace[0] = minX;
    }

    /**
     * This method returns the mod of a number with a continuation for negatives.
     * for negative numbers the mod is a periodic repeat of the positive numbers mod.
     * @param a - first argument of mod
     * @param b - second argument of mod(a % b)
     * @return - mod result
     */
    private int mod(int a, int b){
        if(a >= 0){
            return a % b;
        }
        a += b * ((-a / b) + 1);
        return a % b;
    }

    /**
     * This method renders a pole of blocks at x according to the perlin function.
     * @param x - x coordinate to render pole
     */
    private void renderPoleAtX(float x) { // todo: maybe only last two layers of bricks can be on a different
                                          //     that will collide with leaf.

        // calculate pole of blocks at x
        int y = (int)groundHeightAt(x) / Block.SIZE + BLOCK_FLOOR_OFFSET;

        // remove previously placed blocks in pole
        if(terrainCache[blockColumnIndex] != null){
            for(int i = 0; i < terrainCache[blockColumnIndex].length; i++){
                gameObjects.removeGameObject(terrainCache[blockColumnIndex][i]);
            }
        }

        // init new block column in array
        terrainCache[blockColumnIndex] = new Block[y];

        // create block pole at x
        for(int i = 0; i < y; i++){
            // randomize block color
            Renderable blockRenderable = new RectangleRenderable(
                    ColorSupplier.approximateColor(BASE_GROUND_COLOR));

            // calculate block location
            Vector2 blockLocation = new Vector2(x,
                    Block.SIZE * (PepseGameManager.WINDOW_BLOCK_SIZE - (y - i - 1)));

            // add block to terrain cache entry
            terrainCache[blockColumnIndex][i] = new Block(blockLocation, blockRenderable);

            // add block to game
            gameObjects.addGameObject(terrainCache[blockColumnIndex][i], groundLayer);
        }
    }


    /**
     * This method returns the height of the ground at a given point.
     * The ground level is calculated using a perlin noise function.
     *
     * @param x - location on x-axis
     * @return - height at y-axis
     */
    public float groundHeightAt(float x) {//FIXME: do they want the number of blocks, or the actual height?
        x = (float) ((int)x/Block.SIZE * Block.SIZE);
        double noise = perlinNoise.perlinFunction(x/NOISE_X_RANGE_SCALING);
        return (int) (AVERAGE_HEIGHT + noise * NOISE_SCALE_UP) * Block.SIZE;
    }
}

