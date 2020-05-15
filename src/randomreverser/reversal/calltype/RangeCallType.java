package randomreverser.reversal.calltype;

import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.RangeConstraint;
import randomreverser.reversal.observation.Observation;
import randomreverser.reversal.observation.RangeObservation;
import randomreverser.util.LCG;
import randomreverser.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RangeCallType<T extends Comparable<T>> extends CallType<T> {
    private LCG lcg;
    private T minPossible; // inclusive
    private T maxPossible; // inclusive
    private long seedsPerValue;

    public RangeCallType() {
    }

    public RangeCallType(Class<T> type, LCG lcg, long impliedSteps, T minPossible, T maxPossible, long seedsPerValue) {
        super(type, impliedSteps);
        this.lcg = lcg;
        this.minPossible = minPossible;
        this.maxPossible = maxPossible;
        this.seedsPerValue = seedsPerValue;
    }

    protected void setMinPossible(T minPossible) {
        this.minPossible = minPossible;
    }

    protected void setMaxPossible(T maxPossible) {
        this.maxPossible = maxPossible;
    }

    protected void setSeedsPerValue(long seedsPerValue) {
        this.seedsPerValue = seedsPerValue;
    }

    protected abstract long minSeedFor(T thing);

    protected long maxSeedFor(T thing) {
        return minSeedFor(thing) + seedsPerValue - 1;
    }

    // inclusive, inclusive
    protected ChoiceCallType<Boolean> betweenSeeds(long minSeed, long maxSeed) {
        Map<Boolean, Pair<Constraint<?>, Observation>> values = new HashMap<>();
        values.put(true, new Pair<>(
                new RangeConstraint(lcg.mod(maxSeed - minSeed)),
                new RangeObservation(minSeed, maxSeed)
        ));
        values.put(false, new Pair<>(
                new RangeConstraint(lcg.mod(minSeed - maxSeed)),
                new RangeObservation(lcg.mod(maxSeed + 1), lcg.mod(minSeed - 1))
        ));
        return new ChoiceCallType<>(Boolean.class, getImpliedSteps(), values);
    }

    public ChoiceCallType<Boolean> betweenInclusiveInclusive(T min, T max) {
        return betweenSeeds(minSeedFor(min), maxSeedFor(max));
    }

    public ChoiceCallType<Boolean> betweenInclusiveExclusive(T min, T max) {
        return betweenSeeds(minSeedFor(min), lcg.mod(minSeedFor(max) - 1));
    }

    public ChoiceCallType<Boolean> betweenExclusiveInclusive(T min, T max) {
        return betweenSeeds(lcg.mod(maxSeedFor(min) + 1), maxSeedFor(max));
    }

    public ChoiceCallType<Boolean> betweenExclusiveExclusive(T min, T max) {
        return betweenSeeds(lcg.mod(maxSeedFor(min) + 1), lcg.mod(minSeedFor(max) - 1));
    }

    public ChoiceCallType<Boolean> lessThan(T max) {
        return betweenInclusiveExclusive(minPossible, max);
    }

    public ChoiceCallType<Boolean> lessOrEqual(T max) {
        return betweenInclusiveInclusive(minPossible, max);
    }

    public ChoiceCallType<Boolean> greaterThan(T min) {
        return betweenExclusiveInclusive(min, maxPossible);
    }

    public ChoiceCallType<Boolean> greaterOrEqual(T min) {
        return betweenInclusiveInclusive(min, maxPossible);
    }

    public ChoiceCallType<Boolean> equalTo(T value) {
        return betweenInclusiveInclusive(value, value);
    }

    @Override
    public void addObservations(T value, List<Observation> observations) {
        if (value.compareTo(minPossible) < 0) {
            throw new IllegalArgumentException(value + " < min possible (" + minPossible + ")");
        }
        if (value.compareTo(maxPossible) > 0) {
            throw new IllegalArgumentException(value + " > max possible (" + maxPossible + ")");
        }
        observations.add(new RangeObservation(minSeedFor(value), maxSeedFor(value)));
    }

    @Override
    public void addConstraints(List<Constraint<?>> constraints) {
        constraints.add(new RangeConstraint(seedsPerValue));
    }
}
