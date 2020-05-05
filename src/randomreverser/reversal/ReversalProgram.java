package randomreverser.reversal;

import randomreverser.reversal.calltype.CallType;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.util.LCG;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

public class ReversalProgram {

    private final LCG lcg;
    private final List<CallType<?>> calls;
    private final List<Constraint<?>> constraints;
    private final Map<Constraint<?>, Integer> constraintIndices = new HashMap<>();
    private final List<Instruction> instructions;

    protected ReversalProgram(LCG lcg, List<CallType<?>> calls, List<Constraint<?>> constraints, List<Instruction> instructions) {
        this.lcg = lcg;
        this.calls = calls;
        this.constraints = constraints;
        this.instructions = instructions;
        for (int i = 0; i < constraints.size(); i++) {
            constraintIndices.put(constraints.get(i), i);
        }
    }

    public static ReversalProgramBuilder builder(LCG lcg) {
        return new ReversalProgramBuilder(lcg);
    }

    public ReversalProgramInstance start() {
        return new ReversalProgramInstance(this);
    }

    public LCG getLcg() {
        return lcg;
    }

    public List<CallType<?>> getCalls() {
        return calls;
    }

    public List<Constraint<?>> getConstraints() {
        return constraints;
    }

    public int getConstraintIndex(Constraint<?> constraint) {
        return constraintIndices.get(constraint);
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    @FunctionalInterface
    public interface Instruction {
        LongStream filter(ReversalProgramInstance program, LongStream seeds);
    }

}
