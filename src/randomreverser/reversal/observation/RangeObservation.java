package randomreverser.reversal.observation;

public class RangeObservation extends Observation {
    private final long min; // inclusive
    private final long max; // inclusive

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
}
