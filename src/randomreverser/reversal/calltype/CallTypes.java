package randomreverser.reversal.calltype;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class CallTypes {
    private static final Map<String, Supplier<? extends CallType<?>>> callTypeCreators = new HashMap<>();
    private static final Map<Class<? extends CallType<?>>, String> names = new HashMap<>();

    static {
        registerCallType("java_boolean", JavaCalls.NextBooleanCallType.class, JavaCalls.NextBooleanCallType::new);
        registerCallType("java_float", JavaCalls.NextFloatCallType.class, JavaCalls.NextFloatCallType::new);
        registerCallType("java_int_pow2", JavaCalls.NextIntPowerOf2CallType.class, JavaCalls.NextIntPowerOf2CallType::new);
        registerCallType("choice", ChoiceCallType.class, ChoiceCallType::new);
    }

    public static <T extends CallType<?>> void registerCallType(String name, Class<T> clazz, Supplier<T> creator) {
        callTypeCreators.put(name, creator);
        names.put(clazz, name);
    }

    public static String getName(CallType<?> callType) {
        String name = names.get(callType.getClass());
        if (name == null) {
            throw new IllegalArgumentException("Unregistered call type " + callType.getClass().getName());
        }
        return name;
    }

    public static CallType<?> createEmptyCallType(String name) {
        Supplier<? extends CallType<?>> creator = callTypeCreators.get(name);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown call type '" + name + "'");
        }
        return creator.get();
    }

    public static boolean isCallType(String name) {
        return callTypeCreators.containsKey(name);
    }
}
