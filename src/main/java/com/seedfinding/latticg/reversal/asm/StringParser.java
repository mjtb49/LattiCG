package com.seedfinding.latticg.reversal.asm;

import com.seedfinding.latticg.util.Pair;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@ApiStatus.Internal
@Deprecated // we're not making an ASM language anymore
public final class StringParser {

    private final List<Token> tokens;
    private static final Pattern HEX_VALUE_PATTERN = Pattern.compile("0[Xx][0-9a-fA-F]+");
    private static final Pattern BINARY_VALUE_PATTERN = Pattern.compile("0[Bb][01]+");
    private static final Pattern DECIMAL_VALUE_PATTERN = Pattern.compile("[0-9]+");
    private int cursor = 0;

    private StringParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public static StringParser of(String text) {
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(text));
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');
        tokenizer.wordChars('A', 'Z');
        tokenizer.wordChars('a', 'z');
        tokenizer.wordChars(128 + 32, 255);
        tokenizer.wordChars('_', '_');
        tokenizer.wordChars('#', '#');
        tokenizer.whitespaceChars(0, ' ');
        tokenizer.slashSlashComments(true);
        tokenizer.slashStarComments(true);
        List<Token> tokens = new ArrayList<>();
        try {
            int tk;
            while ((tk = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
                if (tk == StreamTokenizer.TT_WORD) {
                    tokens.add(new Token(tokenizer.sval, tokenizer.lineno()));
                } else {
                    tokens.add(new Token(String.valueOf((char) tk), tokenizer.lineno()));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Reading from StringReader", e);
        }
        return new StringParser(tokens);
    }

    public Token consume() {
        Token ret = peek().orElseThrow(eofException());
        cursor++;
        return ret;
    }

    public void expectEof() {
        if (cursor != tokens.size()) {
            throw new ParseException("Expected EOF before '" + tokens.get(cursor).getText() + "' token", tokens.get(cursor));
        }
    }

    public Optional<Token> peek() {
        return cursor == tokens.size() ? Optional.empty() : Optional.of(tokens.get(cursor));
    }

    public Token peekNotEof() {
        return peek().orElseThrow(eofException());
    }

    public Token expect(String token) {
        Token consumed = consume();
        if (!consumed.getText().equals(token)) {
            throw new ParseException("Expected '" + token + "' before '" + consumed.getText() + "' token", consumed);
        }
        return consumed;
    }

    private Supplier<ParseException> eofException() {
        return () -> new ParseException("EOF token reached early", tokens.isEmpty() ? null : tokens.get(tokens.size() - 1));
    }

    private Token consumeNumberToken() {
        StringBuilder number = new StringBuilder();
        Token firstToken = peekNotEof();

        String sign = firstToken.getText();
        if (sign.equals("+") || sign.equals("-")) {
            consume();
            number.append(sign);
        }

        String firstPart = consume().getText();
        number.append(firstPart);
        if (firstPart.startsWith("0x") || firstPart.startsWith("0X")) {
            if (!HEX_VALUE_PATTERN.matcher(firstPart).matches()) {
                throw new ParseException("Invalid number '" + number + "'", firstToken);
            }
        } else if (firstPart.startsWith("0b") || firstPart.startsWith("0B")) {
            if (!BINARY_VALUE_PATTERN.matcher(firstPart).matches()) {
                throw new ParseException("Invalid number '" + number + "'", firstToken);
            }
        } else if (firstPart.equals(".")) {
            String lastPart = consume().getText();
            number.append(lastPart);
            if (!DECIMAL_VALUE_PATTERN.matcher(lastPart).matches()) {
                throw new ParseException("Invalid number '" + number + "'", firstToken);
            }
        } else {
            if (!DECIMAL_VALUE_PATTERN.matcher(firstPart).matches()) {
                throw new ParseException("Invalid number '" + number + "'", firstToken);
            }
            if (peek().filter(token -> token.getText().equals(".")).isPresent()) {
                consume();
                number.append(".");
                peek().filter(token -> DECIMAL_VALUE_PATTERN.matcher(token.getText()).matches()).ifPresent(token -> {
                    consume();
                    number.append(token.getText());
                });
            }
        }

        return new Token(number.toString(), firstToken.getLine());
    }

    public Pair<BigDecimal, Token> consumeDecimal() {
        Token token = consumeNumberToken();
        if (token.getText().startsWith("0x") || token.getText().startsWith("0X")) {
            return new Pair<>(new BigDecimal(new BigInteger(token.getText().substring(2), 16)), token);
        } else if (token.getText().startsWith("0b") || token.getText().startsWith("0B")) {
            return new Pair<>(new BigDecimal(new BigInteger(token.getText().substring(2), 2)), token);
        } else {
            return new Pair<>(new BigDecimal(token.getText()), token);
        }
    }

    public Pair<BigInteger, Token> consumeInteger() {
        Token token = consumeNumberToken();
        if (token.getText().startsWith("0x") || token.getText().startsWith("0X")) {
            return new Pair<>(new BigInteger(token.getText().substring(2), 16), token);
        } else if (token.getText().startsWith("0b") || token.getText().startsWith("0B")) {
            return new Pair<>(new BigInteger(token.getText().substring(2), 2), token);
        } else {
            try {
                return new Pair<>(new BigInteger(token.getText()), token);
            } catch (NumberFormatException e) {
                throw new ParseException("Invalid number '" + token.getText() + "'", token);
            }
        }
    }

    public Class<?> consumeClass() {
        Token firstToken = consume();
        switch (firstToken.getText()) {
            //@formatter:off
            case "boolean": return boolean.class;
            case "char":    return char.class;
            case "double":  return double.class;
            case "float":   return float.class;
            case "int":     return int.class;
            case "long":    return long.class;
            case "short":   return short.class;
            case "void":    return void.class;
            //@formatter:on
        }

        StringBuilder className = new StringBuilder(firstToken.getText());
        while (peek().filter(token -> token.getText().equals(".")).isPresent()) {
            expect(".");
            className.append(".").append(consume().getText());
        }

        String javaClass = className.toString().replace('#', '$');

        try {
            return Class.forName(javaClass);
        } catch (ClassNotFoundException e) {
            throw new ParseException("Class not found: '" + javaClass + "'", firstToken);
        }
    }
}
