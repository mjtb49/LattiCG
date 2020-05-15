package randomreverser.reversal.instruction;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.util.LCG;

import java.util.function.Function;
import java.util.stream.LongStream;

public class ApplyLcgInstruction extends Instruction {
    private LCG lcg;

    public ApplyLcgInstruction() {
    }

    public ApplyLcgInstruction(LCG lcg) {
        this.lcg = lcg;
    }

    @Override
    public LongStream filter(ProgramInstance program, LongStream seeds) {
        return seeds.map(lcg::nextSeed);
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose, Function<Constraint<?>, String> constraintNames) {
        if (verbose) {
            output.append("/* multiplier = */ ");
        }
        output.append(lcg.multiplier).append(" ");
        if (verbose) {
            output.append("/* addend = */ ");
        }
        output.append(lcg.addend);
    }

    @Override
    public void readOperands(StringParser parser, LCG lcg, Function<String, Constraint<?>> constraintRetriever) {
        long multiplier = parser.consumeInteger().getFirst().longValue();
        long addend = parser.consumeInteger().getFirst().longValue();
        this.lcg = new LCG(multiplier, addend, lcg.modulus);
    }
}
