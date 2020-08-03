package com.seedfinding.latticg.reversal.asm;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Deprecated // we're not making an ASM language anymore
public class ParseException extends RuntimeException {

    public ParseException(String message, Token token) {
        super((token == null ? "1" : token.getLine()) + ": " + message);
    }

}
