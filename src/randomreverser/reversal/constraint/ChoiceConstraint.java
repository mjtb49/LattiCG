package randomreverser.reversal.constraint;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.TypeSerializers;
import randomreverser.reversal.calltype.ChoiceCallType;
import randomreverser.reversal.observation.ChoiceObservation;
import randomreverser.reversal.observation.Observation;
import randomreverser.reversal.observation.Observations;
import randomreverser.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class ChoiceConstraint<T> extends Constraint<ChoiceObservation<T>> {
    private Class<T> choiceType;
    private final Map<T, Pair<Constraint<?>, Observation>> childConstraints;

    ChoiceConstraint() {
        super(ConstraintType.CHOICE);
        childConstraints = new HashMap<>();
    }

    public ChoiceConstraint(Class<T> choiceType, Map<T, Pair<Constraint<?>, Observation>> childConstraints) {
        super(ConstraintType.CHOICE);
        this.choiceType = choiceType;
        this.childConstraints = childConstraints;
    }

    public Class<T> getChoiceType() {
        return choiceType;
    }

    @Override
    public boolean check(ProgramInstance program, long seed, ChoiceObservation<T> observation) {
        return false;
    }

    @Override
    public void readOperands(StringParser parser) {
        Pair<Class<T>, Map<T, Pair<Constraint<?>, Observation>>> pair = ChoiceCallType.readChoice(parser);
        choiceType = pair.getFirst();
        childConstraints.putAll(pair.getSecond());
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose) {
        ChoiceCallType.writeChoice(output, verbose, choiceType, childConstraints);
    }
}
