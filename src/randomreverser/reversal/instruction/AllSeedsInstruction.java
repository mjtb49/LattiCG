package randomreverser.reversal.instruction;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.util.LCG;

import java.util.function.Function;
import java.util.stream.LongStream;

public class AllSeedsInstruction extends Instruction {
    @Override
    public LongStream filter(ProgramInstance program, LongStream seeds) {
        return LongStream.range(0, program.getProgram().getLcg().modulus).parallel();
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose, Function<Constraint<?>, String> constraintNames) {
    }

    @Override
    public void readOperands(StringParser parser, LCG lcg, Function<String, Constraint<?>> constraintRetriever) {
    }
}
