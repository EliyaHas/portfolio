package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.util.PerlinNoise;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;
import java.util.TreeMap;

import static java.lang.Math.abs;

public class Tree {
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 10;
    private static final int TREE_CACHE_SIZE = 4;
    private static final int NOISE_X_RANGE_SCALING = 3000;
    private static final int TREE_DEPLETION_FACTOR = 3;
    private static final int COLUMN_OFFSET = 2;
    private final Color trunkColor = new Color(100, 50, 20);
    private final Vector2 blockDimensions = new Vector2(Block.SIZE, Block.SIZE);
    private final Terrain terrain;
    private static final GameObject[][] treeCache = new GameObject[TREE_CACHE_SIZE][];
    private final Canopy[] canopyCache = new Canopy[TREE_CACHE_SIZE];
    /*terrained space variables*/
    private final int[] terrainedSpace = {0, 0};
    private final PerlinNoise perlinNoise;
    private int positiveBlockColumnIndex = -1;
    private int negativeBlockColumnIndex = TREE_CACHE_SIZE;
    private int treeIndex;
    private final GameObjectCollection gameObjects;
    private final int treeLayer;

    public Tree(Terrain terrain, GameObjectCollection gameObjects, int layer, int seed) {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.treeLayer = layer;
        this.treeIndex = -1;
        this.perlinNoise = new PerlinNoise(seed);
    }

    /**
     * This method creates trees in the range given.
     * a tuple of the range that is already
     *
     * @param minX - min value of the range given
     * @param maxX - max value of the range given
     */
    public void createInRange(int minX, int maxX) {
        // set min/maxX to grid locations
        minX = (minX / Block.SIZE - COLUMN_OFFSET) * Block.SIZE;
        maxX = (maxX / Block.SIZE + COLUMN_OFFSET) * Block.SIZE;

        // set necessary range(not intersecting with existing range)
        boolean velocityDirection = maxX > terrainedSpace[1];
        int actualMaxX = velocityDirection ? maxX : terrainedSpace[0];
        int actualMinX = velocityDirection ? terrainedSpace[1] : minX;

        TreeMap<Integer, Float> treePositionsInRange = new TreeMap<>();
        for (int x = actualMinX; x < actualMaxX; x += Block.SIZE) {
            // flip perlin coin to determine the existence and height of tree at x
            float normalizedPerlinValue = normalizePerlin(x);
            if (treeExists(normalizedPerlinValue) && treeSpacePreserved(x)) {
                treePositionsInRange.put(x, normalizedPerlinValue); // get tree position and coin
                if (treePositionsInRange.size() == TREE_CACHE_SIZE) {
                    break;
                } // prevent overflow
            }
        }
        // render all trees
        for (var entry : treePositionsInRange.entrySet()) {
            setTreeAtX(entry.getKey(), entry.getValue(), velocityDirection);
        }
        // update terrained space array
        terrainedSpace[1] = maxX;
        terrainedSpace[0] = minX;
    }

    private float normalizePerlin(int x) {
        return (float) (1 + perlinNoise.perlinFunction((float) x / NOISE_X_RANGE_SCALING)) / 2;
    }

    private boolean treeSpacePreserved(int x) {
        if (treeIndex == -1) { // first tree
            return true;
        }
        final int MIN_TREE_SPACE = 10 * Block.SIZE;
        return abs(x - treeCache[treeIndex][0].getTopLeftCorner().x()) > MIN_TREE_SPACE;
    }


    /**
     * This method initializes variables for and renders a tree at position - x
     *
     * @param x                 - the x coordinate
     * @param perlinCoefficient - a random floating number in the range [0, 1] evaluated from a perlin function
     * @param velocityDirection - boolean determining side from which the tree appears (right/left)
     */
    private void setTreeAtX(int x, float perlinCoefficient, boolean velocityDirection) {
        updateTreeIndex(velocityDirection);

        // plant tree - get tree parameters
        int terrainHeight = (int) terrain.groundHeightAt(x) / Block.SIZE;
        int treeHeight = MIN_TREE_HEIGHT + (int) (perlinCoefficient * (MAX_TREE_HEIGHT - MIN_TREE_HEIGHT));

        // remove previously placed tree blocks and canopy
        removePreviousTree();
        renderNewTree(treeHeight, terrainHeight, x, perlinCoefficient);
    }

    /**
     * This method takes the position for a new tree and adds all necessary objects to the game.
     * all trunk blocks are added at the position with a tree height that is randomized,
     * based on the given value of the perlin function computed earlier.
     * a canopy is added to the canopy cache and a new canopy is generated at the trunk base.
     *
     * @param treeHeight        - given height of new tree(in blocks)
     * @param terrainHeight     - height of the terrain at new tree position(in blocks)
     * @param x                 - position of new tree in blocks
     * @param perlinCoefficient - normalized(to [0, 1] range) random value of perlin function.
     */
    private void renderNewTree(int treeHeight, int terrainHeight, int x, float perlinCoefficient) {
        Renderable trunkBlockRenderable;

        // initialize tree cache entry(for trunk)
        treeCache[treeIndex] = new GameObject[treeHeight];

        int correctedY = 0; // actual y location on screen to render the blocks
        for (int i = 0; i < treeHeight; i++) {
            // randomize block color
            trunkBlockRenderable = new RectangleRenderable(
                    ColorSupplier.approximateColor(trunkColor));

            // calculate block location
            correctedY = Block.SIZE * (PepseGameManager.WINDOW_BLOCK_SIZE - terrainHeight - i);
            Vector2 blockLocation = new Vector2(x, correctedY);

            // add trunk blocks to tree cache
            treeCache[treeIndex][i] = new GameObject(blockLocation, blockDimensions,
                    trunkBlockRenderable);

            // make sure avatar does collide with tree
            treeCache[treeIndex][i].physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
            treeCache[treeIndex][i].physics().preventIntersectionsFromDirection(Vector2.ZERO);

            // add block to game
            gameObjects.addGameObject(treeCache[treeIndex][i], treeLayer);
        }
        canopyCache[treeIndex] = new Canopy(x, correctedY, perlinCoefficient, gameObjects);
        canopyCache[treeIndex].generateCanopy();

    }

    /**
     * This method removes all leaf and trunk objects of a tree(trunk & canopy) from the game.
     * The tree and canopy cache are iterated over and each game object is removed.
     */
    private void removePreviousTree() {
        if (treeCache[treeIndex] != null) {
            for (int i = 0; i < treeCache[treeIndex].length; i++) {
                gameObjects.removeGameObject(treeCache[treeIndex][i]);
            }
            canopyCache[treeIndex].removeCanopy();
        }
    }

    /**
     * This method updates the index of the tree and canopy caches.
     * it's based on the direction of the movement of the camera(positive/negative)
     *
     * @param velocityDirection - a boolean representing the positivity of the camera velocity
     */
    private void updateTreeIndex(boolean velocityDirection) {
        // set entry indices - update treeIndex
        if (velocityDirection) {
            positiveBlockColumnIndex++;
            if (positiveBlockColumnIndex == negativeBlockColumnIndex) {
                negativeBlockColumnIndex++;
            }
        } else {
            negativeBlockColumnIndex--;
            if (positiveBlockColumnIndex == negativeBlockColumnIndex) {
                positiveBlockColumnIndex--;
            }
        }
        treeIndex = velocityDirection ? positiveBlockColumnIndex : negativeBlockColumnIndex;// pick index
        treeIndex = mod(treeIndex, treeCache.length); // modulo index to range
    }

    /**
     * This method returns a boolean for tree existence at x based on the perlin number's 9 decimal digit.
     *
     * @param perlinCoefficient - a normalization of a random number to positive value
     *                          x is used to evaluate a perlin function to get the value.
     * @return - boolean for existence of tree at x
     */
    private boolean treeExists(double perlinCoefficient) {
        int FACTOR = 1000000000;
        int roundedPerlinCoefficient = (int) (perlinCoefficient * FACTOR);
        Random random = new Random(roundedPerlinCoefficient);
        return roundedPerlinCoefficient - 10 * (roundedPerlinCoefficient / 10) == 1
                && random.nextInt(Tree.TREE_DEPLETION_FACTOR - 1) == 0;
    }

    /**
     * This method returns the mod of a number with a continuation for negatives.
     * for negative numbers the mod is a periodic repeat of the positive numbers mod.
     *
     * @param a - first argument of mod
     * @param b - second argument of mod(a % b)
     * @return - mod result
     */
    private int mod(int a, int b) {
        if (a >= 0) {
            return a % b;
        }
        a += b * ((-a / b) + 1);
        return a % b;
    }
}
