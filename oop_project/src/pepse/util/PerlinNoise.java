package pepse.util;

import java.util.Random;

import static java.lang.Math.abs;

public class PerlinNoise {
    private final int[] permutation = new int[256];

    public PerlinNoise(int seed) {
        Random random = new Random(seed);
        for (int i = 0; i < 256; i++) {
            permutation[i] = i;
        }
        for (int i = 0; i < 256; i++) {
            int j = random.nextInt(256);
            int temp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = temp;
        }
    }

    private double grad(int h, double x) {
        h &= 0xF;
        double angle = (h * Math.PI) / 16;
        return x * Math.cos(angle) + x * Math.sin(angle);
    }

    public double perlinFunction(double x) {
        int X = (int) (abs(x)) & 0xFF;
        x -= (int) (x);
        double absX = x >= 0 ? x : -x;
        double u = fade(absX);
        int A = permutation[X];
        int AA = permutation[A];
        int AB = permutation[(A + 1) % 256];
        return lerp(u, grad(permutation[AA], absX), grad(permutation[AB], absX - 1));
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }
}

