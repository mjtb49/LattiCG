package randomreverser.reversal.instruction;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;
import randomreverser.math.lattice.enumeration.Enumerate;
import randomreverser.math.optimize.Optimize;
import randomreverser.reversal.ProgramInstance;
import randomreverser.reversal.asm.ParseException;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.Token;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.RangeConstraint;
import randomreverser.reversal.observation.RangeObservation;
import randomreverser.util.LCG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

public class StaticLatticeInstruction extends Instruction {
    private BigMatrix lattice;
    private BigVector translation;
    private List<RangeConstraint> rangeConstraints;
    private LCG vecToSeed;

    public StaticLatticeInstruction() {
    }

    public StaticLatticeInstruction(BigMatrix lattice, BigVector translation, List<RangeConstraint> rangeConstraints, LCG vecToSeed) {
        this.lattice = lattice;
        this.translation = translation;
        this.rangeConstraints = rangeConstraints;
        this.vecToSeed = vecToSeed;
    }

    @Override
    public LongStream filter(ProgramInstance program, LongStream seeds) {
        Optimize.Builder builder = Optimize.Builder.ofSize(lattice.getRowCount());
        for (int i = 0; i < rangeConstraints.size(); i++) {
            RangeObservation observation = program.getObservation(rangeConstraints.get(i));
            builder.withLowerBound(i, new BigFraction(observation.getMin())).withUpperBound(i, new BigFraction(observation.getMax()));
        }
        return Enumerate.enumerate(lattice, translation, builder.build())
                .mapToLong(vec -> vecToSeed.nextSeed(vec.get(0).getNumerator().longValue()));
    }

    @Override
    public void writeOperands(StringBuilder output, boolean verbose, Function<Constraint<?>, String> constraintNames) {
        if (verbose) {
            output.append("/* lattice = */ ");
        }
        output.append(lattice.toString().replaceAll("}(\\s*),", "}$1,\n  ")).append("\n");
        output.append("  ");
        if (verbose) {
            output.append("/* translation = */ ");
        }
        output.append(translation).append("\n");
        output.append("  ");
        if (verbose) {
            output.append("/* constraints = */ ");
        }
        for (Constraint<?> constraint : rangeConstraints) {
            output.append(constraintNames.apply(constraint)).append(" ");
        }
        output.append("\n  ");
        if (verbose) {
            output.append("/* vec2seed multiplier = */ ");
        }
        output.append(vecToSeed.multiplier).append(" ");
        if (verbose) {
            output.append("/* addend = */ ");
        }
        output.append(vecToSeed.addend);
    }

    @Override
    public void readOperands(StringParser parser, LCG lcg, Function<String, Constraint<?>> constraintRetriever) {
        lattice = BigMatrix.parse(parser);
        translation = BigVector.parse(parser);
        rangeConstraints = new ArrayList<>();
        for (int i = 0; i < lattice.getRowCount(); i++) {
            Token constraintToken = parser.consume();
            Constraint<?> constraint = constraintRetriever.apply(constraintToken.getText());
            if (constraint == null) {
                throw new ParseException("Undeclared constraint '" + constraintToken.getText() + "'", constraintToken);
            }
            if (!(constraint instanceof RangeConstraint)) {
                throw new ParseException("static_lattice can only take range constraints, not '" + constraintToken.getText() + "'", constraintToken);
            }
            rangeConstraints.add((RangeConstraint) constraint);
        }
        long multiplier = parser.consumeInteger().getFirst().longValue();
        long addend = parser.consumeInteger().getFirst().longValue();
        this.vecToSeed = new LCG(multiplier, addend, lcg.modulus);
    }
}
