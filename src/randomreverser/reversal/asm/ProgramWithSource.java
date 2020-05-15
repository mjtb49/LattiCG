package randomreverser.reversal.asm;

import randomreverser.reversal.Program;
import randomreverser.reversal.calltype.CallType;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.instruction.Instruction;
import randomreverser.util.LCG;

import java.util.List;

public class ProgramWithSource extends Program {
    private final String source;

    protected ProgramWithSource(LCG lcg, List<CallType<?>> calls, List<Constraint<?>> constraints, List<Instruction> instructions, String source) {
        super(lcg, calls, constraints, instructions);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}
