package randomreverser.reversal.asm;

import randomreverser.reversal.Program;
import randomreverser.reversal.calltype.CallType;
import randomreverser.reversal.calltype.CallTypes;
import randomreverser.reversal.constraint.Constraint;
import randomreverser.reversal.constraint.ConstraintType;
import randomreverser.reversal.instruction.Instruction;
import randomreverser.reversal.instruction.Instructions;
import randomreverser.util.LCG;
import randomreverser.util.Pair;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReversalASM {

    private static final int LANGUAGE_VERSION = 0;

    public static String toAsm(Program program, boolean verbose) {
        StringBuilder str = new StringBuilder();
        str.append("version ").append(LANGUAGE_VERSION).append(";\n");
        str.append("lcg ").append(program.getLcg().multiplier).append(" ").append(program.getLcg().addend).append(" ").append(program.getLcg().modulus).append(";\n");
        str.append("\n");
        str.append("calls {\n");
        for (CallType<?> callType : program.getCalls()) {
            str.append("  ").append(callType.getSteps()).append(" ").append(CallTypes.getName(callType)).append(" ");
            callType.writeOperands(str, verbose);
            str.append(";\n");
        }
        str.append("}\n");
        str.append("\n");
        str.append("constraints {\n");
        List<Constraint<?>> constraints = program.getConstraints();
        for (int i = 0; i < constraints.size(); i++) {
            Constraint<?> constraint = constraints.get(i);
            str.append("  #").append(i).append(" : ").append(constraint.getType().getName()).append(" ").append(constraint.getSteps()).append(" ");
            constraint.writeOperands(str, verbose);
            str.append(";\n");
        }
        str.append("}\n");
        str.append("\n");
        for (Instruction insn : program.getInstructions()) {
            str.append(Instructions.getMnemonic(insn)).append(" ");
            insn.writeOperands(str, verbose, constraint -> "#" + program.getConstraintIndex(constraint));
            str.append(";\n");
        }
        return str.toString();
    }

    public static ProgramWithSource fromAsm(String asm) {
        StringParser parser = StringParser.of(asm);
        ProgramWithSource program = fromAsm(asm, parser);
        parser.expectEof();
        return program;
    }

    private static ProgramWithSource fromAsm(String source, StringParser parser) {
        parser.expect("version");
        Pair<BigInteger, Token> versionPair = parser.consumeInteger();
        int version = versionPair.getFirst().intValue();
        if (version > LANGUAGE_VERSION) {
            throw new ParseException("Cannot parse future version", versionPair.getSecond());
        }
        parser.expect(";");

        parser.expect("lcg");
        LCG lcg = new LCG(parser.consumeInteger().getFirst().longValue(), parser.consumeInteger().getFirst().longValue(), parser.consumeInteger().getFirst().longValue());
        parser.expect(";");

        List<CallType<?>> calls = new ArrayList<>();
        parser.expect("calls");
        parser.expect("{");
        while (parser.peek().filter(token -> !token.getText().equals("}")).isPresent()) {
            long steps = parser.consumeInteger().getFirst().longValue();
            Token nameToken = parser.consume();
            if (!CallTypes.isCallType(nameToken.getText())) {
                throw new ParseException("Unknown call type '" + nameToken.getText() + "'", nameToken);
            }
            CallType<?> callType = CallTypes.createEmptyCallType(nameToken.getText());
            callType.setSteps(steps);
            callType.readOperands(parser);
            parser.expect(";");
            calls.add(callType);
        }
        parser.expect("}");

        List<Constraint<?>> constraints = new ArrayList<>();
        Map<String, Constraint<?>> constraintNames = new HashMap<>();
        parser.expect("constraints");
        parser.expect("{");
        while (parser.peek().filter(token -> !token.getText().equals("}")).isPresent()) {
            String constraintLabel = parser.consume().getText();
            parser.expect(":");
            long steps = parser.consumeInteger().getFirst().longValue();
            Token constraintNameToken = parser.consume();
            if (!ConstraintType.isConstraintType(constraintNameToken.getText())) {
                throw new ParseException("'" + constraintNameToken.getText() + "' is not a constraint type", constraintNameToken);
            }
            Constraint<?> constraint = ConstraintType.byName(constraintNameToken.getText()).createEmpty();
            constraint.setSteps(steps);
            constraint.readOperands(parser);
            constraints.add(constraint);
            constraintNames.put(constraintLabel, constraint);
            parser.expect(";");
        }
        parser.expect("}");
        // TODO: validate constraints against call types

        List<Instruction> instructions = new ArrayList<>();
        while (parser.peek().isPresent()) {
            Token mnemonicToken = parser.consume();
            if (!Instructions.isInstruction(mnemonicToken.getText())) {
                throw new ParseException("'" + mnemonicToken.getText() + "' is not an instruction mnemonic", mnemonicToken);
            }
            Instruction insn = Instructions.createEmptyInstruction(mnemonicToken.getText());
            insn.readOperands(parser, lcg, constraintNames::get);
            instructions.add(insn);
            parser.expect(";");
        }

        return new ProgramWithSource(lcg, calls, constraints, instructions, source);
    }

}
