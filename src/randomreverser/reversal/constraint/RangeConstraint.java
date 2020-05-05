package randomreverser.reversal.constraint;

import randomreverser.reversal.ReversalProgramInstance;
import randomreverser.reversal.observation.RangeObservation;

public class RangeConstraint extends Constraint<RangeObservation> {
    private final long length;

    public RangeConstraint(long length) {
        super(ConstraintType.RANGE);
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    @Override
    public boolean check(ReversalProgramInstance program, long seed, RangeObservation observation) {
        if (observation.getMin() > observation.getMax()) {
            return seed > observation.getMin() || seed < observation.getMax();
        } else {
            return seed >= observation.getMin() && seed <= observation.getMax();
        }
    }
}
