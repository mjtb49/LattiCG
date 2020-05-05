package randomreverser.reversal.observation;

public class ChoiceObservation<T> extends Observation {
    private final T chosenValue;

    public ChoiceObservation(T chosenValue) {
        this.chosenValue = chosenValue;
    }

    public T getChosenValue() {
        return chosenValue;
    }
}
