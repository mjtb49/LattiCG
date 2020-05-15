package randomreverser.reversal.asm;

public class ParseException extends RuntimeException {

    public ParseException(String message, Token token) {
        super((token == null ? "1" : token.getLine()) + ": " + message);
    }

}
