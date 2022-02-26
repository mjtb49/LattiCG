package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.CallType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextBooleanCall extends CallType<Boolean> {
    @ApiStatus.Internal
    static final NextBooleanCall EQUAL_TO_TRUE = new NextBooleanCall(false);
    @ApiStatus.Internal
    static final NextBooleanCall EQUAL_TO_FALSE = new NextBooleanCall(true);
    private final boolean inverted;

    private NextBooleanCall(boolean inverted) {
        super(Boolean.class, 1);
        this.inverted = inverted;
    }

    @Override
    public CallType<Boolean> not() {
        return inverted ? EQUAL_TO_TRUE : EQUAL_TO_FALSE;
    }

    @Override
    public CallType<Boolean> equalTo(Boolean value) {
        return value ? EQUAL_TO_TRUE : EQUAL_TO_FALSE;
    }

    public boolean isInverted() {
        return inverted;
    }
}
