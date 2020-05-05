package randomreverser.reversal.constraint;

import randomreverser.reversal.ReversalProgramInstance;
import randomreverser.reversal.observation.Observation;

public abstract class Constraint<O extends Observation> {
    private final ConstraintType type;
    /**
     * The number of random calls compared to the previous step (an observation does not imply any steps, so usually set to 1)
     */
    private long steps;

    public Constraint(ConstraintType type) {
        this.type = type;
    }

    public ConstraintType getType() {
        return type;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public abstract boolean check(ReversalProgramInstance program, long seed, O observation);
}
