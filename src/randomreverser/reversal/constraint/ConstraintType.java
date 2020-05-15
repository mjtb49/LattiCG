package randomreverser.reversal.constraint;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public enum ConstraintType {
    RANGE("range", RangeConstraint::new), CHOICE("choice", ChoiceConstraint::new);

    private final String name;
    private final Supplier<Constraint<?>> creator;
    ConstraintType(String name, Supplier<Constraint<?>> creator) {
        this.name = name;
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public Constraint<?> createEmpty() {
        return creator.get();
    }

    public static ConstraintType byName(String name) {
        ConstraintType type = BY_NAME.get(name);
        if (type == null) {
            throw new IllegalArgumentException("Unknown constraint type '" + name + "'");
        }
        return type;
    }

    public static boolean isConstraintType(String name) {
        return BY_NAME.containsKey(name);
    }

    private static final Map<String, ConstraintType> BY_NAME = new HashMap<>();
    static {
        for (ConstraintType constraintType : values()) {
            BY_NAME.put(constraintType.getName(), constraintType);
        }
    }
}
