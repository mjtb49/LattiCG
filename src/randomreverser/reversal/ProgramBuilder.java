package randomreverser.reversal;

import randomreverser.math.component.BigFraction;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;
import randomreverser.math.lattice.LLL.LLL;
import randomreverser.math.lattice.LLL.Params;
import randomreverser.math.lattice.LLL.Result;
import randomreverser.reversal.calltype.CallType;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.ConstraintType;
import randomreverser.reversal.constraint.RangeConstraint;
import randomreverser.reversal.instruction.AllSeedsInstruction;
import randomreverser.reversal.instruction.ApplyLcgInstruction;
import randomreverser.reversal.instruction.CheckInstruction;
import randomreverser.reversal.instruction.Instruction;
import randomreverser.reversal.instruction.StaticLatticeInstruction;
import randomreverser.util.LCG;
import randomreverser.util.Mth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static randomreverser.math.lattice.LLL.Params.recommendedDelta;

public class ProgramBuilder {

    private final LCG lcg;
    private List<CallType<?>> calls = new ArrayList<>();
    private long steps = 0;

    ProgramBuilder(LCG lcg) {
        this.lcg = lcg;
    }

    public ProgramBuilder add(CallType<?> call) {
        call.setSteps(steps);
        steps = call.getImpliedSteps();
        calls.add(call);
        return this;
    }

    public ProgramBuilder skip(long steps) {
        this.steps += steps;
        return this;
    }

    public Program build() {
        // TODO: improve this (a lot)
        List<Instruction> instructions = new ArrayList<>();

        List<Constraint<?>> constraints = createConstraintList();

        // Construct a lattice
        initialSeeds(instructions, constraints);

        // Check each of the observations
        bruteForceConstraints(instructions, constraints);

        // Final step: go back 1 seed
        instructions.add(new ApplyLcgInstruction(lcg.invert()));

        return new Program(lcg, calls, constraints, instructions);
    }

    private List<Constraint<?>> createConstraintList() {
        List<Constraint<?>> constraints = new ArrayList<>();
        long nextStepsDeduction = 0;
        for (CallType<?> call : calls) {
            int nextIndex = constraints.size();
            call.addConstraints(constraints);
            if (constraints.size() > nextIndex) {
                constraints.get(nextIndex).setSteps(call.getSteps() - nextStepsDeduction);
                nextStepsDeduction = 0;
                for (int i = nextIndex + 1; i < constraints.size(); i++) {
                    nextStepsDeduction += constraints.get(i).getSteps();
                }
            }
        }
        return constraints;
    }

    private void initialSeeds(List<Instruction> instructions, List<Constraint<?>> constraints) {
        int latticeSize = 0;
        BigInteger lcmSideLengths = BigInteger.ONE;
        for (Constraint<?> constraint : constraints) {
            if (constraint.getType() == ConstraintType.RANGE) {
                latticeSize++;
                BigInteger length = BigInteger.valueOf(((RangeConstraint) constraint).getLength());
                lcmSideLengths = Mth.lcm(lcmSideLengths, length);
            }
        }

        if (latticeSize == 0) {
            // parallel brute force
            instructions.add(new AllSeedsInstruction());
        } else {
            latticeReversal(instructions, constraints, latticeSize, lcmSideLengths);
        }
    }

    private void latticeReversal(List<Instruction> instructions, List<Constraint<?>> constraints,
                                 int latticeSize, BigInteger lcmSideLengths) {
        List<RangeConstraint> rangeConstraints = new ArrayList<>();
        BigMatrix untransformed = new BigMatrix(latticeSize+1, latticeSize);
        BigMatrix transform = new BigMatrix(latticeSize, latticeSize);
        BigVector translation = new BigVector(latticeSize);
        LCG vecToSeed = null;
        int latticeIndex = 0;
        long steps = 0;
        for (Constraint<?> constraint : constraints) {
            steps += constraint.getSteps();
            if (constraint.getType() == ConstraintType.RANGE) {
                RangeConstraint rangeConstraint = (RangeConstraint) constraint;
                rangeConstraints.add(rangeConstraint);
                LCG tmpLcg = lcg.combine(steps);
                if (vecToSeed == null) {
                    vecToSeed = tmpLcg.invert();
                }
                untransformed.set(latticeIndex+1, latticeIndex, new BigFraction(lcg.modulus));
                untransformed.set(0, latticeIndex, new BigFraction(tmpLcg.multiplier));
                BigInteger length = BigInteger.valueOf(rangeConstraint.getLength());
                transform.set(latticeIndex, latticeIndex, new BigFraction(lcmSideLengths.divide(length)));
                translation.set(latticeIndex, new BigFraction(tmpLcg.nextSeed(0)));
                latticeIndex++;
            }
        }
        BigMatrix unreduced = untransformed.multiply(transform);
        Result lllResult = LLL.reduce(unreduced, new Params().setDelta(recommendedDelta));
        //TODO: transform is diagonal so this is a little expensive
        BigMatrix reduced = lllResult.getReducedBasis().multiply(transform.inverse());
        // TODO: build this transpose into the entire matrix construction
        // *cough* matthew learnt the wrong standard *cough* excuse me I have a cough today
        BigMatrix lattice = reduced.transpose();

        instructions.add(new StaticLatticeInstruction(lattice, translation, rangeConstraints, vecToSeed));
    }

    private void bruteForceConstraints(List<Instruction> instructions, List<Constraint<?>> constraints) {
        long steps = 0;
        for (Constraint<?> constraint : constraints) {
            steps += constraint.getSteps();
            instructions.add(new CheckInstruction(lcg.combine(steps), constraint));
        }
    }

}
