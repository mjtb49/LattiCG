package randomreverser.reversal.calltype;

import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.observation.Observation;

import java.util.List;

public abstract class CallType<T> {
    private final Class<T> type;
    /**
     * The number of random calls relative to the previous constraint.
     * Defaults to 1, as constraints do not automatically imply a change to the seed!
     */
    private long steps;
    /**
     * The number of steps this random call takes up implicitly.
     * Not recursive! For choice types, does not include the children's implied steps.
     */
    private final long impliedSteps;

    public CallType(Class<T> type, long impliedSteps) {
        this.type = type;
        this.impliedSteps = impliedSteps;
    }

    public final Class<T> getType() {
        return type;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public abstract void addObservations(T value, List<Observation> observations);

    public abstract void addConstraints(List<Constraint<?>> constraints);

    public long getImpliedSteps() {
        return impliedSteps;
    }
}
