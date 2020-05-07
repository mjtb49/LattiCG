package randomreverser.reversal.asm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class TypeSerializers {
    private static final Map<Class<?>, BiFunction<Class<?>, StringParser, ?>> typeReaders = new HashMap<>();
    private static final Map<Class<?>, BiConsumer<StringBuilder, ?>> typeWriters = new HashMap<>();

    static {
        registerPrimitive(boolean.class, Boolean.class, parser -> {
            Token token = parser.consume();
            if (token.getText().equals("false")) {
                return false;
            } else if (token.getText().equals("true")) {
                return true;
            } else {
                throw new ParseException("Illegal boolean: '" + token.getText() + "'", token);
            }
        });
        registerPrimitive(char.class, Character.class, parser -> {
            Token token = parser.consume();
            if (token.getText().length() != 1) {
                throw new ParseException("Illegal char: '" + token.getText() + "'", token);
            }
            return token.getText().charAt(0);
        });
        registerPrimitive(double.class, Double.class, parser -> parser.consumeDecimal().getFirst().doubleValue());
        registerPrimitive(float.class, Float.class, parser -> parser.consumeDecimal().getFirst().floatValue());
        registerPrimitive(int.class, Integer.class, parser -> parser.consumeInteger().getFirst().intValue());
        registerPrimitive(long.class, Long.class, parser -> parser.consumeInteger().getFirst().longValue());
        registerPrimitive(short.class, Short.class, parser -> parser.consumeInteger().getFirst().shortValue());
        registerType(Enum.class, (type, parser) -> {
            Token token = parser.consume();
            try {
                return enumValueOf(type, token.getText());
            } catch (IllegalArgumentException e) {
                throw new ParseException("Illegal " + type.getSimpleName() + ": '" + token.getText() + "'", token);
            }
        }, (builder, val) -> builder.append(val.name()));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends Enum<T>> Enum<?> enumValueOf(Class<Enum> type, String text) {
        //noinspection RedundantCast - wtf intellij/javac
        return Enum.valueOf((Class<T>) (Class<?>) type, text);
    }

    private static <T> void registerPrimitive(Class<T> primitive, Class<T> boxed, Function<StringParser, T> reader) {
        registerType(primitive, reader);
        registerType(boxed, reader);
    }

    public static <T> void registerType(Class<T> type, Function<StringParser, T> reader) {
        registerType(type, reader, StringBuilder::append);
    }

    public static <T> void registerType(Class<T> type, Function<StringParser, T> reader, BiConsumer<StringBuilder, T> writer) {
        typeReaders.put(type, (t, parser) -> reader.apply(parser));
        typeWriters.put(type, writer);
    }

    @SuppressWarnings("unchecked")
    public static <T, U extends T> void registerType(Class<T> type, BiFunction<Class<U>, StringParser, U> reader, BiConsumer<StringBuilder, T> writer) {
        typeReaders.put(type, (BiFunction<Class<?>, StringParser, ?>) (BiFunction<?, StringParser, ?>) reader);
        typeWriters.put(type, writer);
    }

    private static BiFunction<Class<?>, StringParser, ?> getTypeReader(Class<?> type) {
        for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
            BiFunction<Class<?>, StringParser, ?> typeReader = typeReaders.get(clazz);
            if (typeReader != null) {
                return typeReader;
            }
        }
        return null;
    }

    public static boolean canSerialize(Class<?> type) {
        return getTypeReader(type) != null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T read(StringParser parser, Class<T> type) {
        BiFunction<Class<?>, StringParser, ?> reader = getTypeReader(type);
        if (reader == null) {
            throw new IllegalArgumentException("Don't know how to parse a value of type " + type.getName());
        }
        return (T) reader.apply(type, parser);
    }

    @SuppressWarnings("unchecked")
    public static <T> void write(StringBuilder output, Class<T> type, T value) {
        for (Class<?> clazz = type; clazz != null; clazz = clazz.getSuperclass()) {
            BiConsumer<StringBuilder, T> typeWriter = (BiConsumer<StringBuilder, T>) typeWriters.get(clazz);
            if (typeWriter != null) {
                typeWriter.accept(output, value);
                return;
            }
        }
        throw new IllegalArgumentException("Don't know how to write a value of type " + type.getName());
    }
}
