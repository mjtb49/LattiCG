package randomreverser.reversal.observation;

import randomreverser.reversal.asm.StringParser;

public abstract class Observation {

    public abstract void readOperands(StringParser parser);

    public abstract void writeOperands(StringBuilder output, boolean verbose);

}
