package randomreverser.math.lattice.LLL;

import randomreverser.math.component.BigFraction;

public class Params {
    protected BigFraction delta = new BigFraction(75, 100);
    protected boolean debug;
    protected int maxStage = -1;
    public static BigFraction recommendedDelta =new BigFraction(99,100);

    public Params setMaxStage(int maxStage) {
        this.maxStage = maxStage;
        return this;
    }

    public Params setDelta(BigFraction delta) {
        this.delta = delta;
        return this;
    }

    public Params setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
