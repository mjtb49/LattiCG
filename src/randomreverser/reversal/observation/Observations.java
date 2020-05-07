package randomreverser.reversal.observation;

import randomreverser.reversal.constraint.ChoiceConstraint;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.RangeConstraint;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Observations {
    private static final Map<String, Function<Constraint<?>, ? extends Observation>> observationCreators = new HashMap<>();
    private static final Map<Class<? extends Observation>, String> names = new HashMap<>();

    static {
        registerObservation("range", RangeObservation.class, (Function<RangeConstraint, RangeObservation>) RangeObservation::new);
        registerChoiceObservation();
    }

    @SuppressWarnings("unchecked")
    private static void registerChoiceObservation() {
        registerObservation(
                "choice",
                (Class<ChoiceObservation<?>>) (Class<?>) ChoiceObservation.class,
                (Constraint<ChoiceObservation<?>> c) -> new ChoiceObservation<>((ChoiceConstraint<?>) (Constraint<?>) c)
        );
    }

    @SuppressWarnings("unchecked")
    public static <T extends Observation> void registerObservation(String name, Class<T> clazz, Function<? extends Constraint<T>, T> creator) {
        observationCreators.put(name, (Function<Constraint<?>, ? extends Observation>) (Function<?, ?>) creator);
        names.put(clazz, name);
    }

    public static String getName(Observation observation) {
        String name = names.get(observation.getClass());
        if (name == null) {
            throw new IllegalArgumentException("Unregistered observation " + observation.getClass().getName());
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Observation> T createEmptyObservation(String name, Constraint<T> constraint) {
        Function<Constraint<T>, T> creator = (Function<Constraint<T>, T>) (Function<?, ?>) observationCreators.get(name);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown observation '" + name + "'");
        }
        return creator.apply(constraint);
    }

    public static boolean isObservation(String name) {
        return observationCreators.containsKey(name);
    }
}
