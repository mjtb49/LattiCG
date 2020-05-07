package randomreverser.reversal.observation;

import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.TypeSerializers;
import randomreverser.reversal.constraint.ChoiceConstraint;

public class ChoiceObservation<T> extends Observation {
    private final Class<T> choiceType;
    private T chosenValue;

    ChoiceObservation(ChoiceConstraint<T> constraint) {
        this.choiceType = constraint.getChoiceType();
    }

    public ChoiceObservation(Class<T> choiceType, T chosenValue) {
        this.choiceType = choiceType;
        this.chosenValue = chosenValue;
    }

    public T getChosenValue() {
        return chosenValue;
    }

    @Override
    public void readOperands(StringParser parser) {
        chosenValue = TypeSerializers.read(parser, choiceType);
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose) {
        if (verbose) {
            output.append("/* value = */ ");
        }
        TypeSerializers.write(output, choiceType, chosenValue);
    }
}
