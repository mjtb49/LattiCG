package randomreverser.reversal.calltype;

import randomreverser.reversal.asm.ParseException;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.Token;
import randomreverser.reversal.asm.TypeSerializers;
import randomreverser.reversal.constraint.ChoiceConstraint;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.ConstraintType;
import randomreverser.reversal.observation.ChoiceObservation;
import randomreverser.reversal.observation.Observation;
import randomreverser.reversal.observation.Observations;
import randomreverser.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ChoiceCallType<T> extends CallType<T> {
    private final Map<T, Pair<Constraint<?>, Observation>> values;

    public ChoiceCallType() {
        values = new HashMap<>();
    }

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
        observations.add(new ChoiceObservation<>(getType(), value));
    }

    @Override
    public void addConstraints(List<Constraint<?>> constraints) {
        constraints.add(new ChoiceConstraint<>(getType(), values));
    }

    @Override
    public void readOperands(StringParser parser) {
        setImpliedSteps(parser.consumeInteger().getFirst().longValue());
        Pair<Class<T>, Map<T, Pair<Constraint<?>, Observation>>> pair = readChoice(parser);
        setType(pair.getFirst());
        values.putAll(pair.getSecond());
    }

    @SuppressWarnings("unchecked")
    public static <T> Pair<Class<T>, Map<T, Pair<Constraint<?>, Observation>>> readChoice(StringParser parser) {
        Token typeToken = parser.peekNotEof();
        Class<T> choiceType = (Class<T>) parser.consumeClass();
        if (!TypeSerializers.canSerialize(choiceType)) {
            throw new ParseException("Cannot read values of type '" + choiceType.getName() + "'", typeToken);
        }
        Map<T, Pair<Constraint<?>, Observation>> childConstraints = new HashMap<>();
        parser.expect("{");
        while (parser.peek().filter(token -> !token.getText().equals("}")).isPresent()) {
            T key = TypeSerializers.read(parser, choiceType);
            parser.expect(":");
            Token constraintNameToken = parser.consume();
            if (!ConstraintType.isConstraintType(constraintNameToken.getText())) {
                throw new ParseException("'" + constraintNameToken.getText() + "' is not a constraint type", constraintNameToken);
            }
            Constraint<?> constraint = ConstraintType.byName(constraintNameToken.getText()).createEmpty();
            constraint.readOperands(parser);
            Token observationNameToken = parser.consume();
            if (!Observations.isObservation(observationNameToken.getText())) {
                throw new ParseException("'" + observationNameToken.getText() + "' is not an observation type", observationNameToken);
            }
            Observation observation;
            try {
                observation = Observations.createEmptyObservation(observationNameToken.getText(), constraint);
            } catch (ClassCastException e) {
                throw new ParseException("Observation type '" + observationNameToken.getText() + "' is not compatible with constraint type '" + constraint.getType() + "'", observationNameToken);
            }
            observation.readOperands(parser);
            parser.expect(";");
            childConstraints.put(key, new Pair<>(constraint, observation));
        }
        parser.expect("}");
        return new Pair<>(choiceType, childConstraints);
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose) {
        output.append(getImpliedSteps()).append(" ");
        writeChoice(output, verbose, getType(), values);
    }

    public static <T> void writeChoice(StringBuilder output, boolean verbose, Class<T> choiceType, Map<T, Pair<Constraint<?>, Observation>> childConstraints) {
        output.append(choiceType.getName().replace('$', '#')).append(" {\n");
        for (Map.Entry<T, Pair<Constraint<?>, Observation>> entry : childConstraints.entrySet()) {
            output.append("    ");
            TypeSerializers.write(output, choiceType, entry.getKey());
            output.append(" : ");
            output.append(entry.getValue().getFirst().getType().getName()).append(" ");
            entry.getValue().getFirst().writeOperands(output, verbose);
            output.append(" ");
            output.append(Observations.getName(entry.getValue().getSecond())).append(" ");
            entry.getValue().getSecond().writeOperands(output, verbose);
            output.append(";\n");
        }
        output.append("  }");
    }
}
