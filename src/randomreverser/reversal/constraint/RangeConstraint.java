package randomreverser.reversal.constraint;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.observation.RangeObservation;

public class RangeConstraint extends Constraint<RangeObservation> {
    private long length;

    RangeConstraint() {
        super(ConstraintType.RANGE);
    }

    public RangeConstraint(long length) {
        super(ConstraintType.RANGE);
        this.length = length;
    }

    public long getLength() {
        return length;
    }

    @Override
    public boolean check(ProgramInstance program, long seed, RangeObservation observation) {
        if (observation.getMin() > observation.getMax()) {
            return seed > observation.getMin() || seed < observation.getMax();
        } else {
            return seed >= observation.getMin() && seed <= observation.getMax();
        }
    }

    @Override
    public void readOperands(StringParser parser) {
        length = parser.consumeInteger().getFirst().longValue();
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose) {
        if (verbose) {
            output.append("/* length = */ ");
        }
        output.append(length);
    }
}
