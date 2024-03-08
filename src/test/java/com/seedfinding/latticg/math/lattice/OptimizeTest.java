package com.seedfinding.latticg.math.lattice;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigFractionUtil;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.component.BigVectorUtil;
import com.seedfinding.latticg.math.optimize.Optimize;
import com.seedfinding.latticg.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OptimizeTest {
    @Test
    public void test2() {
        BigFraction lower = new BigFraction(3);
        BigFraction upper = new BigFraction(5);
        BigVector gradient = new BigVector(1, 1, 1, 1);

        Optimize optimize = Optimize.Builder
            .ofSize(4)
            .withLowerBound(0, lower).withUpperBound(0, upper)
            .withLowerBound(1, lower).withUpperBound(1, upper)
            .withLowerBound(2, lower).withUpperBound(2, upper)
            .withLowerBound(3, lower).withUpperBound(3, upper)
            .build();

        Pair<BigVector, BigFraction> result = optimize.minimize(gradient);

        assertEquals(new BigVector(3, 3, 3, 3), result.getFirst());
        assertEquals(new BigFraction(12), result.getSecond());
    }

    @Test
    public void test3() {
        BigVector lower = BigVectorUtil.fromString("{211106232532992, 211106232532981, 210828868589894, 199388147328707, 161385748837116, 108479823158593, 185398950615714, 185126754296559, 73966775769528, 62839209804621, 83194595169726, 145472338376155, -22881604128716, -51152864657895, 119381387175578, 107356151384967, 249574878786160}");
        BigVector upper = BigVectorUtil.fromString("{281474976710656, 281474976710645, 281197612767558, 269756891506371, 231754493014780, 178848567336257, 255767694793378, 255495498474223, 144335519947192, 133207953982285, 153563339347390, 215841082553819, 47487140048948, 19215879519769, 121580410431130, 124948337429383, 267167064830576}");

        Optimize.Builder builder = Optimize.Builder.ofSize(17);

        for (int i = 0; i < 17; ++i) {
//            System.out.printf("subject to c: x%d >= %s;%n", i + 1, lower.get(i));
//            System.out.printf("subject to c: x%d <= %s;%n", i + 1, upper.get(i));

            builder.withLowerBound(i, lower.get(i)).withUpperBound(i, upper.get(i));
        }

        builder.withStrictBound(BigVectorUtil.fromString("{1/140737488355328, 1/281474976710656, 0, 0, 3/281474976710656, 0, 1/281474976710656, 1/140737488355328, 0, 1/140737488355328, 1/140737488355328, 1/140737488355328, 0, 1/281474976710656, 23/281474976710656, 1/35184372088832, 1/281474976710656}"), BigFractionUtil.fromString("24"));
        builder.withStrictBound(BigVectorUtil.fromString("{-1/281474976710656, -1/281474976710656, 1/140737488355328, 0, 3/281474976710656, 0, -1/281474976710656, -1/281474976710656, 1/281474976710656, 1/281474976710656, 0, 3/281474976710656, -1/281474976710656, 1/140737488355328, -25/281474976710656, -1/281474976710656, 7/281474976710656}"), BigFractionUtil.fromString("-2"));
        builder.withStrictBound(BigVectorUtil.fromString("{0, -1/281474976710656, 0, -1/281474976710656, 1/140737488355328, 0, 0, 1/281474976710656, -3/281474976710656, 1/281474976710656, 0, 1/70368744177664, 1/281474976710656, 1/140737488355328, -5/17592186044416, 3/140737488355328, 1/70368744177664}"), BigFractionUtil.fromString("-26"));
        builder.withStrictBound(BigVectorUtil.fromString("{-1/281474976710656, 1/281474976710656, 1/281474976710656, 1/281474976710656, 0, 1/140737488355328, 1/281474976710656, 1/140737488355328, 1/281474976710656, 1/281474976710656, 1/281474976710656, 3/281474976710656, -1/140737488355328, 1/140737488355328, -45/281474976710656, 11/281474976710656, -7/281474976710656}"), BigFractionUtil.fromString("-12"));
        builder.withStrictBound(BigVectorUtil.fromString("{-1/281474976710656, 1/281474976710656, 3/281474976710656, 1/281474976710656, -1/281474976710656, 0, 1/140737488355328, -1/281474976710656, 1/70368744177664, -1/281474976710656, -1/281474976710656, 0, 1/281474976710656, -1/281474976710656, -29/140737488355328, -1/140737488355328, -3/140737488355328}"), BigFractionUtil.fromString("-25"));
//        builder.withEqual(BigVectorUtil.fromString("{1/281474976710656, -1/140737488355328, -1/281474976710656, 0, -3/281474976710656, 0, 1/281474976710656, 1/140737488355328, 1/140737488355328, 1/281474976710656, 0, 1/70368744177664, 1/140737488355328, 1/281474976710656, -19/140737488355328, 1/140737488355328, -1/70368744177664}"), BigFractionUtil.fromString("-16"));

        Optimize optimize = builder.build()
            .withStrictBound(BigVectorUtil.fromString("{1/281474976710656, -1/140737488355328, -1/281474976710656, 0, -3/281474976710656, 0, 1/281474976710656, 1/140737488355328, 1/140737488355328, 1/281474976710656, 0, 1/70368744177664, 1/140737488355328, 1/281474976710656, -19/140737488355328, 1/140737488355328, -1/70368744177664}"), BigFractionUtil.fromString("-16"));

        Pair<BigVector, BigFraction> min = optimize.minimize(BigVectorUtil.fromString("{-1/281474976710656, 0, -3/281474976710656, 0, 1/140737488355328, 0, 0, 1/281474976710656, 0, 3/281474976710656, 1/140737488355328, 1/70368744177664, 0, 1/281474976710656, -69/281474976710656, -1/70368744177664, -1/70368744177664}"));
        Pair<BigVector, BigFraction> max = optimize.maximize(BigVectorUtil.fromString("{-1/281474976710656, 0, -3/281474976710656, 0, 1/140737488355328, 0, 0, 1/281474976710656, 0, 3/281474976710656, 1/140737488355328, 1/70368744177664, 0, 1/281474976710656, -69/281474976710656, -1/70368744177664, -1/70368744177664}"));

//        Pair<BigVector, BigFraction> min = optimize.minimize(BigVectorUtil.fromString("{1/281474976710656, -1/140737488355328, -1/281474976710656, 0, -3/281474976710656, 0, 1/281474976710656, 1/140737488355328, 1/140737488355328, 1/281474976710656, 0, 1/70368744177664, 1/140737488355328, 1/281474976710656, -19/140737488355328, 1/140737488355328, -1/70368744177664}"));
//        Pair<BigVector, BigFraction> max = optimize.maximize(BigVectorUtil.fromString("{1/281474976710656, -1/140737488355328, -1/281474976710656, 0, -3/281474976710656, 0, 1/281474976710656, 1/140737488355328, 1/140737488355328, 1/281474976710656, 0, 1/70368744177664, 1/140737488355328, 1/281474976710656, -19/140737488355328, 1/140737488355328, -1/70368744177664}"));

       /* System.out.println(min.getFirst());
        System.out.println(min.getSecond());
        System.out.println(min.getSecond().toDouble());

        System.out.println(max.getFirst());
        System.out.println(max.getSecond());
        System.out.println(max.getSecond().toDouble());*/

        assertEquals(BigFractionUtil.fromString("-10547790643587294007/322288848333701120"), min.getSecond());
        assertEquals(BigFractionUtil.fromString("-4727348601358048099/151996487423754240"), max.getSecond());
    }
}
