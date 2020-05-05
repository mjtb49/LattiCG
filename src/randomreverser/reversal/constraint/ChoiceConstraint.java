package randomreverser.reversal.constraint;

import randomreverser.reversal.ReversalProgramInstance;
import randomreverser.reversal.observation.ChoiceObservation;

import java.util.Map;

public class ChoiceConstraint<T> extends Constraint<ChoiceObservation<T>> {
    private final Map<T, Constraint<?>> childConstraints;

    public ChoiceConstraint(Map<T, Constraint<?>> childConstraints) {
        super(ConstraintType.CHOICE);
        this.childConstraints = childConstraints;
    }

    @Override
    public boolean check(ReversalProgramInstance program, long seed, ChoiceObservation<T> observation) {
        return false;
    }
}
