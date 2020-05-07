package randomreverser.reversal.instruction;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Instructions {
    private static final Map<String, Supplier<? extends Instruction>> instructionCreators = new HashMap<>();
    static final Map<Class<? extends Instruction>, String> mnemonics = new HashMap<>();

    static {
        registerInstruction("all_seeds", AllSeedsInstruction.class, AllSeedsInstruction::new);
        registerInstruction("apply_lcg", ApplyLcgInstruction.class, ApplyLcgInstruction::new);
        registerInstruction("check", CheckInstruction.class, CheckInstruction::new);
        registerInstruction("static_lattice", StaticLatticeInstruction.class, StaticLatticeInstruction::new);
    }

    public static <T extends Instruction> void registerInstruction(String mnemonic, Class<T> clazz, Supplier<T> creator) {
        instructionCreators.put(mnemonic, creator);
        mnemonics.put(clazz, mnemonic);
    }

    public static String getMnemonic(Instruction insn) {
        String mnemonic = mnemonics.get(insn.getClass());
        if (mnemonic == null) {
            throw new IllegalArgumentException("Unregistered instruction type " + insn.getClass().getName());
        }
        return mnemonic;
    }

    public static Instruction createEmptyInstruction(String mnemonic) {
        Supplier<? extends Instruction> creator = instructionCreators.get(mnemonic);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown instruction '" + mnemonic + "'");
        }
        return creator.get();
    }

    public static boolean isInstruction(String mnemonic) {
        return instructionCreators.containsKey(mnemonic);
    }
}
