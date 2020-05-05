package randomreverser.reversal.calltype;

import randomreverser.reversal.constraint.ChoiceConstraint;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.observation.ChoiceObservation;
import randomreverser.reversal.observation.Observation;
import randomreverser.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChoiceCallType<T> extends CallType<T> {
    private final Map<T, Pair<Constraint<?>, Observation>> values;

    public ChoiceCallType(Class<T> type, long impliedSteps, Map<T, Pair<Constraint<?>, Observation>> values) {
        super(type, impliedSteps);
        this.values = values;
    }

    public <U> ChoiceCallType<U> map(Class<U> newType, Function<T, U> mapper) {
        Map<U, Pair<Constraint<?>, Observation>> newValues = new HashMap<>();
        for (Map.Entry<T, Pair<Constraint<?>, Observation>> entry : values.entrySet()) {
            newValues.put(mapper.apply(entry.getKey()), entry.getValue());
        }
        return new ChoiceCallType<>(newType, getImpliedSteps(), newValues);
    }

    @Override
    public void addObservations(T value, List<Observation> observations) {
        observations.add(new ChoiceObservation<>(value));
    }

    @Override
    public void addConstraints(List<Constraint<?>> constraints) {
        Map<T, Constraint<?>> childConstraintsMap = new HashMap<>();
        for (Map.Entry<T, Pair<Constraint<?>, Observation>> entry : values.entrySet()) {
            childConstraintsMap.put(entry.getKey(), entry.getValue().getFirst());
        }
        constraints.add(new ChoiceConstraint<>(childConstraintsMap));
    }
}
