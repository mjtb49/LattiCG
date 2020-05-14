import randomreverser.math.component.BigMatrix;
import randomreverser.math.lattice.LLLbis;

import static org.junit.Assert.assertEquals;

public class Test {


    public static void main(String[] args) {
        BigMatrix basis = BigMatrix.fromString("{{1, 2}, {3, 4}}");
        BigMatrix expected = BigMatrix.fromString("{{7847617, 4824621}, {-18218081, 24667315}}");
        LLLbis.Params params=new LLLbis.Params().setMaxStage(basis.getRowCount());
        LLLbis.reduce(basis,params);
        System.out.println();
        System.out.println(basis.toPrettyString());
    }

}
