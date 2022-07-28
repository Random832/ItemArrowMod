package random832.itemarrows;

import net.minecraft.util.RandomSource;

public class MathHelper {
    public static int probabilisticDivide(int dividend, int divisor, RandomSource random) {
        int q = dividend / divisor;
        if (dividend % divisor == 0 || random.nextInt(divisor) >= dividend % divisor)
            return q;
        else
            return q + 1;
    }
}
