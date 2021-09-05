package com.seedfinding.latticg;

import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.lattice.LLL.Params;
import com.seedfinding.latticg.math.lattice.LLL.Result;
import com.seedfinding.latticg.math.lattice.optimization.BKZ;

import static com.seedfinding.latticg.math.lattice.LLL.Params.recommendedDelta;

public class Test {
    public static void main(String[] args) {
        BigMatrix basis = BigMatrix.fromString(
            "{{1,25214903917,205749139540585,233752471717045,55986898099985,120950523281469,76790647859193,61282721086213,128954768138017,177269950146317,19927021227657,92070806603349,28158748839985,118637304785629,127636996050457,12659659028133,120681609298497,262331189124013,31562171905705,1172916755445,193905135338833,247073002637693,112300943448121,219938666776133}," +
                "{281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656,0} ," +
                "{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,281474976710656}}");
        Params params = new Params().setDelta(recommendedDelta);
        Result result = BKZ.reduce(basis, 11, params);
        System.out.println(result.getReducedBasis().toPrettyString());
    }

}
