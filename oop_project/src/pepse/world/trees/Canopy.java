package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Canopy {
    final int MAX_BASE_NEIGHBORS = 5;
    final int MIN_BASE_NEIGHBORS = 3;
    final int MAX_LEAF_COUNT = getMaxLeafs();
    final Vector2 leafDimensions = new Vector2(Block.SIZE, Block.SIZE);
    private final Color leafColor = new Color(50, 200, 30);
    public final Renderable leafRenderable = new RectangleRenderable(
            ColorSupplier.approximateColor(leafColor));
    private final Vector2 canopyBaseVector;
    private final double perlinCoefficient;
    private final GameObjectCollection gameObjects;
    private final Random random;
    final private GameObject[] canopyLeafCache = new GameObject[MAX_LEAF_COUNT];
    private int leafIndex = 0;

    public Canopy(float canopyBaseX, float canopyBaseY, float perlinCoefficient,
                  GameObjectCollection gameObjects) {
        this.canopyBaseVector = new Vector2(canopyBaseX, canopyBaseY);
        this.perlinCoefficient = perlinCoefficient;
        this.gameObjects = gameObjects;

        // initialize Random object with seed based off of perlinValue
        final int roundingFactor = 1000000;
        int roundedPerlinValue = (int) (roundingFactor * perlinCoefficient);
        this.random = new Random(roundedPerlinValue);
    }

    /**
     * This method generates a leaf canopy around a tree
     * this is done recursively, as each leaf that is generated, randomizes the locations of its leaf
     * neighbors. the initial number of leaf friends is randomized, and each generation the number decreases.
     */
    public void generateCanopy() {
        // randomize number of neighbors in the range [MIN_NEIGHBORS, MAX_NEIGHBORS]
        int leafFriends = MIN_BASE_NEIGHBORS + random.nextInt(MAX_BASE_NEIGHBORS - MIN_BASE_NEIGHBORS);

        // call recursive function
        generateRecursiveLeaf(canopyBaseVector.x(), canopyBaseVector.y(), leafFriends);

        // add leafs to game
        for(int i = 0; i < leafIndex; i++) {
            gameObjects.addGameObject(canopyLeafCache[i]);
        }
    }

    /**
     * This method generates a leaf that generates its neighbor leafs recursively.
     * the first leaf is at (x, y) and the neighbor locations are randomized amongst the 8
     * neighboring squares.
     * at each generation the number of leaf neighbors goes down, so the canopy depletes as the leafs are
     * further away from the canopy base
     * @param x - x coordinate to create leaf
     * @param y - y coordinate to create leaf
     * @param leafFriends - number of neighbors
     */
    private void generateRecursiveLeaf(float x, float y, int leafFriends) {
        // stop generating if leaf cache is full
        if (leafIndex == MAX_LEAF_COUNT - 1) {
            return;
        }

        // generate leaf at position
        int leafPerlinValue = (int) (perlinCoefficient * x * leafIndex);
        canopyLeafCache[leafIndex++] = new LeafObject(new Vector2(x, y),
                leafDimensions, leafRenderable, leafPerlinValue);

        // initiate list of indices
        ArrayList<Integer> neighborIndexArray = new ArrayList<>();
        for (int i = 0; i < MAX_BASE_NEIGHBORS; i++) {
            neighborIndexArray.add(i);
        }

        // generate neighbors
        for (int i = 0; i < leafFriends; i++) {
            // calculate neighbor index
            int arrayIndex = random.nextInt(MAX_BASE_NEIGHBORS - i);
            int neighborIndex = neighborIndexArray.get(arrayIndex);
            neighborIndexArray.remove(arrayIndex); // remove index from list

            Vector2 neighborLocation = getNeighborLocation(x, y, neighborIndex);
            generateRecursiveLeaf(neighborLocation.x(), neighborLocation.y(), leafFriends - 1);
        }
    }

    /**
     * This method takes an index in the range [0,7], and returns the location of a neighboring leaf.
     * the index {neighborIndex} is mapped to one of the 8 squares(BLOCK.SIZE sized) surrounding the leaf
     * and using the reference leaf location, the neighbor location is returned
     * @param referenceLeafLocationX - x coordinate of reference leaf
     * @param referenceLeafLocationY - y coordinate of reference leaf
     * @param neighborIndex - index of neighboring leaf in neighbor square
     * @return - location of neighboring leaf
     */
    private Vector2 getNeighborLocation(float referenceLeafLocationX, float referenceLeafLocationY,
                                        int neighborIndex){
        final int CENTER_SQUARE_INDEX = 4;
        final int UNMAPPED_CORNER_SQUARE_INDEX = 8;

        // the center square is illegal and converted to the unmapped corner square
        if(neighborIndex == CENTER_SQUARE_INDEX){
            neighborIndex = UNMAPPED_CORNER_SQUARE_INDEX;
        }

        // calculate square location vector
        float x = referenceLeafLocationX + (float)(neighborIndex % 3 - 1) * Block.SIZE;
        float y = referenceLeafLocationY + (float)(neighborIndex / 3 - 1) * Block.SIZE;
        return new Vector2(x, y);
    }

    /**
     * This method removes all the leaves in the canopy from the game
     */
    public void removeCanopy(){
        for(int i = 0 ; i < leafIndex; i++){
            gameObjects.removeGameObject(canopyLeafCache[i]);
        }
    }

    /**
     * This method calculates the number of leaves on the canopy.
     * @return - total number of leaves on the canopy.
     */
    private int getMaxLeafs() {
        int product;
        int sum = 0;
        for(int min_index = 1;min_index < MAX_BASE_NEIGHBORS;min_index++) {
            product = 1;
            for (int i = MAX_BASE_NEIGHBORS; i >= min_index; i--) {
                product *= i;
            }
            sum += product;
        }
        return sum + 1;
    }
}
