package com.seedfinding.latticg.reversal.asm;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Deprecated // we're not making an ASM language anymore
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
