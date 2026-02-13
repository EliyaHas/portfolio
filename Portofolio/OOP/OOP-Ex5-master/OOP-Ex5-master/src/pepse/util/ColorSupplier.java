package pepse.util;

import java.awt.*;
import java.util.Random;

public class ColorSupplier {

    /**
     * This method returns a random shade of earth color(brown)
     * @return - the randomized color
     */
    public static Color approximateColor(Color baseGroundColor) {
        Random random = new Random();
        final int RANGE_FACTOR = 5;
        final int RANDOM_RANGE = 3;

        final int BASE_RED = baseGroundColor.getRed() + RANGE_FACTOR *
                (random.nextInt(RANDOM_RANGE));
        final int BASE_GREEN = baseGroundColor.getGreen() + RANGE_FACTOR *
                (random.nextInt(RANDOM_RANGE));
        final int BASE_BLUE = baseGroundColor.getBlue() + RANGE_FACTOR *
                (random.nextInt(RANDOM_RANGE));

        return new Color(BASE_RED, BASE_GREEN, BASE_BLUE);
    }
}
