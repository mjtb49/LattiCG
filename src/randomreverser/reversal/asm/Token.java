package randomreverser.reversal.asm;

public class Token {

    private final String text;
    private final int line;

    public Token(String text, int line) {
        this.text = text;
        this.line = line;
    }

    public String getText() {
        return text;
    }

    public int getLine() {
        return line;
    }
}
