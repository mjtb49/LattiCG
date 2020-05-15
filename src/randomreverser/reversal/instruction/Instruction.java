package randomreverser.reversal.instruction;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.util.LCG;

import java.util.function.Function;
import java.util.stream.LongStream;

public abstract class Instruction {

    public abstract LongStream filter(ProgramInstance program, LongStream seeds);

    public abstract void writeOperands(StringBuilder output, boolean verbose,
                                       Function<Constraint<?>, String> constraintNames);

    public abstract void readOperands(StringParser parser, LCG lcg, Function<String, Constraint<?>> constraintRetriever);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Instructions.mnemonics.getOrDefault(getClass(), "~~UNREGISTERED~~")).append(" ");
        writeOperands(sb, true, Constraint::toString);
        return sb.toString();
    }
}
