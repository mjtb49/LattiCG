package randomreverser;

import randomreverser.math.component.BigMatrix;
import randomreverser.math.lattice.LLL.Params;
import randomreverser.math.lattice.LLL.Result;
import randomreverser.math.lattice.optimization.BKZ;

import static randomreverser.math.lattice.LLL.Params.recommendedDelta;

public class Test {
    public static void main(String[] args) {
        BigMatrix basis = BigMatrix.fromString(
                "{{1,25214903917,205749139540585,233752471717045,55986898099985,120950523281469,76790647859193,61282721086213,128954768138017,177269950146317,19927021227657,92070806603349}," +
                        "{0, 281474976710656, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}," +
                        "{0, 0, 281474976710656, 0, 0, 0, 0, 0, 0, 0, 0, 0}," +
                        "{0, 0, 0, 281474976710656, 0, 0, 0, 0, 0, 0, 0, 0}," +
                        "{0, 0, 0, 0, 281474976710656, 0, 0, 0, 0, 0, 0, 0}," +
                        "{0, 0, 0, 0, 0, 281474976710656, 0, 0, 0, 0, 0, 0}," +
                        "{0, 0, 0, 0, 0, 0, 281474976710656, 0, 0, 0, 0, 0}," +
                        "{0, 0, 0, 0, 0, 0, 0, 281474976710656, 0, 0, 0, 0}," +
                        "{0, 0, 0, 0, 0, 0, 0, 0, 281474976710656, 0, 0, 0}," +
                        "{0, 0, 0, 0, 0, 0, 0, 0, 0, 281474976710656, 0, 0}," +
                        "{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 281474976710656, 0}," +
                        "{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 281474976710656}}");
        Params params=new Params().setDelta(recommendedDelta);
        Result result = BKZ.reduce(basis, 2, params);
        System.out.println(result.getReducedBasis().toPrettyString());
    }

}
