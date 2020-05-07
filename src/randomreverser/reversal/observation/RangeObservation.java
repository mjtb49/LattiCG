package randomreverser.reversal.observation;

import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.constraint.RangeConstraint;

public class RangeObservation extends Observation {
    private long min; // inclusive
    private long max; // inclusive

    RangeObservation(RangeConstraint constraint) {
    }

    public RangeObservation(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public void readOperands(StringParser parser) {
        min = parser.consumeInteger().getFirst().longValue();
        max = parser.consumeInteger().getFirst().longValue();
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose) {
        if (verbose) {
            output.append("/* min = */ ");
        }
        output.append(min).append(" ");
        if (verbose) {
            output.append("/* max = */ ");
        }
        output.append(max);
    }
}
