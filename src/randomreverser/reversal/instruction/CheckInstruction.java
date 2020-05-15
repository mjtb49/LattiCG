package randomreverser.reversal.instruction;

import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.ParseException;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.Token;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.observation.Observation;
import randomreverser.util.LCG;

import java.util.function.Function;
import java.util.stream.LongStream;

public class CheckInstruction extends Instruction {
    private LCG relativeLcg;
    private Constraint<?> constraint;

    public CheckInstruction() {
    }

    public CheckInstruction(LCG relativeLcg, Constraint<?> constraint) {
        this.relativeLcg = relativeLcg;
        this.constraint = constraint;
    }

    @Override
    public LongStream filter(ProgramInstance program, LongStream seeds) {
        return seeds.filter(seed -> check(constraint, program, relativeLcg.nextSeed(seed), program.getObservation(constraint)));
    }

    @SuppressWarnings("unchecked")
    private static <T extends Observation> boolean check(Constraint<?> constraint, ProgramInstance program, long seed, Observation observation) {
        return ((Constraint<T>) constraint).check(program, seed, (T) observation);
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose, Function<Constraint<?>, String> constraintNames) {
        if (verbose) {
            output.append("/* multiplier = */ ");
        }
        output.append(relativeLcg.multiplier).append(" ");
        if (verbose) {
            output.append("/* addend = */ ");
        }
        output.append(relativeLcg.addend).append(" ");
        if (verbose) {
            output.append("/* constraint = */ ");
        }
        output.append(constraintNames.apply(constraint));
    }

    @Override
    public void readOperands(StringParser parser, LCG lcg, Function<String, Constraint<?>> constraintRetriever) {
        long multiplier = parser.consumeInteger().getFirst().longValue();
        long addend = parser.consumeInteger().getFirst().longValue();
        Token constraintToken = parser.consume();
        Constraint<?> constraint = constraintRetriever.apply(constraintToken.getText());
        if (constraint == null) {
            throw new ParseException("Undeclared constraint '" + constraintToken.getText() + "'", constraintToken);
        }
        this.relativeLcg = new LCG(multiplier, addend, lcg.modulus);
        this.constraint = constraint;
    }
}
