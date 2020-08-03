package randomreverser.reversal.calltype;

/**
 * Represents a type of random call you are expecting
 */
public abstract class CallType<T> {

    private final Class<T> type;

    public CallType(Class<T> type) {
        this.type = type;
    }

    public final Class<T> getType() {
        return type;
    }

    public CallType<T> not() {
        throw unsupported("not");
    }

    public CallType<Boolean> betweenII(T min, T max) {
        throw unsupported("betweenII");
    }

    public CallType<Boolean> betweenIE(T min, T max) {
        throw unsupported("betweenIE");
    }

    public CallType<Boolean> betweenEI(T min, T max) {
        throw unsupported("betweenEI");
    }

    public CallType<Boolean> betweenEE(T min, T max) {
        throw unsupported("betweenEE");
    }

    public CallType<Boolean> equalTo(T value) {
        throw unsupported("equalTo");
    }

    public CallType<Boolean> notEqualTo(T value) {
        return equalTo(value).not();
    }

    public CallType<Boolean> lessThan(T value) {
        throw unsupported("lessThan");
    }

    public CallType<Boolean> lessThanEqual(T value) {
        throw unsupported("lessThanEqual");
    }

    public CallType<Boolean> greaterThan(T value) {
        return lessThanEqual(value).not();
    }

    public CallType<Boolean> greaterThanEqual(T value) {
        return lessThan(value).not();
    }

    private UnsupportedOperationException unsupported(String methodName) {
        return new UnsupportedOperationException("Method \"" + methodName + "\" is not supported by " + getClass().getName());
    }
}
