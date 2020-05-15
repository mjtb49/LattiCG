package randomreverser.reversal.calltype;

import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.TypeSerializers;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.observation.Observation;

import java.util.List;

public abstract class CallType<T> {

    private Class<T> type;
    /**
     * The number of random calls relative to the previous constraint.
     * Defaults to 1, as constraints do not automatically imply a change to the seed!
     */
    private long steps;
    /**
     * The number of steps this random call takes up implicitly.
     * Not recursive! For choice types, does not include the children's implied steps.
     */
    private long impliedSteps;

    protected CallType() {
    }

    public CallType(Class<T> type, long impliedSteps) {
        setType(type);
        this.impliedSteps = impliedSteps;
    }

    public final Class<T> getType() {
        return type;
    }

    public final void setType(Class<T> type) {
        if (!TypeSerializers.canSerialize(type)) {
            throw new IllegalArgumentException("Cannot use " + type.getName() + " in a call type! Use TypeSerializers.registerType() if you want to use it.");
        }
        this.type = type;
    }

    public long getSteps() {
        return steps;
    }

    public void setSteps(long steps) {
        this.steps = steps;
    }

    public abstract void addObservations(T value, List<Observation> observations);

    public abstract void addConstraints(List<Constraint<?>> constraints);

    public abstract void writeOperands(StringBuilder output, boolean verbose);

    public abstract void readOperands(StringParser parser);

    protected T readValue(StringParser parser) {
        return TypeSerializers.read(parser, type);
    }

    protected void writeValue(StringBuilder output, T value) {
        TypeSerializers.write(output, type, value);
    }

    public long getImpliedSteps() {
        return impliedSteps;
    }

    public void setImpliedSteps(long impliedSteps) {
        this.impliedSteps = impliedSteps;
    }
}
