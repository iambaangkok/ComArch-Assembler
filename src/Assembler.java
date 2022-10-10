import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Assembler {

    private static boolean DEBUG = true;

    private static final Map<String, String> TYPE_MAP = Map.of(
            "add", "R",
            "nand", "R",
            "lw", "I",
            "sw", "I",
            "beq", "I",
            "jalr", "J",
            "halt", "O",
            "noop", "O",
            ".fill", "F");
    private static final Map<String, String> OPCODE_MAP = Map.of(
            "add", "000",
            "nand", "001",
            "lw", "010",
            "sw", "011",
            "beq", "100",
            "jalr", "101",
            "halt", "110",
            "noop", "111",
            ".fill", "FIL");
    private static final Map<String, Integer> NUMERIC_FIELD_COUNT_MAP = Map.of(
            "add", 3,
            "nand", 3,
            "lw", 2,
            "sw", 2,
            "beq", 2,
            "jalr", 2,
            "halt", 0,
            "noop", 0,
            ".fill", 0);
    private static final String MC_STARTER = "0000000"; // bits[31-25] (7 bits) should always be 0

    private static final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    private Tokenizer tok;

    private Map<String, Integer> labelMap = new HashMap<>();

    private int currentLine = 0;
    private List<String> lineData = new ArrayList<>();
    private List<String> machineCodes = new ArrayList<>();
    private String machineCode = MC_STARTER;

    Assembler(String assembly) {
        tok = new Tokenizer(assembly);
    }

    private void loadLine() {
        lineData = new ArrayList<>();

        while (tok.hasNext()) {
            String token = tok.next();
            System.out.print(token);
            if (isLineBreak(token)) {
                break;
            }

            lineData.add(token);
        }
    }

    private static boolean isNumeric(String token) {
        return NUMERIC_PATTERN.matcher(token).matches();
    }

    private static int toNumeric(String token) {
        return Integer.parseInt(token);
    }

    public static String toBinaryString(int dec) {
        if(DEBUG) System.out.print("toBinaryString(" + dec + "): ");

        StringBuilder bin = new StringBuilder("");

        if(dec == 0 || dec == 1){
            bin.append(dec);
            return bin.toString();
        }

        while (dec > 0) {
            bin.append(dec % 2);
            dec /= 2;
        }

        bin.reverse();

        if(DEBUG) System.out.println(bin);

        return bin.toString();
    }

    public static int toDecimal(String bin){
        if(DEBUG) System.out.print("toDecimal(" + bin + "): ");

        int dec = 0;
        int power = 0;
        
        if(bin.charAt(0) == '0'){
            for(int i = bin.length()-1; i >=0; i--){
                dec += toNumeric(bin.charAt(i)+"")*Math.pow(2, power);
                power++;
            }
        }else{
            bin = twosCompliment(bin);
            for(int i = bin.length()-1; i >=0; i--){
                dec += toNumeric(bin.charAt(i)+"")*Math.pow(2, power);
                power++;
            }
            dec = -dec;
        }
        

        if(DEBUG) System.out.println(dec);

        return dec;
    }

    private static boolean isLineBreak(String token) {
        return token.equals("\n");
    }

    private static boolean isInstruction(String token) {
        return TYPE_MAP.containsKey(token);
    }

    private boolean isLabel(String token) {
        System.out.println("isLabel: " + token + " " + labelMap.containsKey(token));
        return labelMap.containsKey(token);
    }

    private boolean isValidLabel(String token) {
        return !(token.length() > 6
                || labelMap.containsKey(token)
                || isNumeric(token.charAt(0) + ""));
    }

    public List<String> assembleIntoMachineCode() {
        // Iterate once to fill labelMap
        loadLine();
        while (tok.hasNext()) {
            if(lineData.isEmpty()){
                loadLine();
                continue;
            }

            if (DEBUG) {
                System.out.println("Filling labelMap, lineData: " + lineData);
            }

            if (!isInstruction(lineData.get(0))) {
                if (isValidLabel(lineData.get(0))) {
                    labelMap.put(lineData.get(0), currentLine);
                } else {
                    exit(1);
                }
            }
            currentLine++;
            loadLine();
        }
        //////////

        if(DEBUG){
            System.out.println(labelMap);
        }

        tok.resetIterator();
        currentLine = 0;

        // Iterate again to translate into machine code
        loadLine();
        
        while (tok.hasNext()) {
            int instIndex = 0;
            if(lineData.isEmpty()){
                loadLine();
                continue;
            }

            if (DEBUG) {
                System.out.println("Filling labelMap, lineData: " + lineData);
            }

            if (!isInstruction(lineData.get(instIndex))) {
                if (!isLabel(lineData.get(instIndex))) {
                    if(DEBUG) System.out.println("A");
                    exit(1);
                } else {
                    instIndex++;
                    if (!isInstruction(lineData.get(instIndex))) {
                        if(DEBUG) System.out.println("B");
                        exit(1);
                    }
                }
            }
            String inst = lineData.get(instIndex);
            String opcode = OPCODE_MAP.get(inst);
            String type = TYPE_MAP.get(inst);
            int fieldCount = NUMERIC_FIELD_COUNT_MAP.get(inst);
            String[] fields = { "", "", "" };

            machineCode = MC_STARTER;
            machineCode += opcode;

            // Convert numeric fields to number
            for (int j = 0; j < fieldCount; ++j) {
                fields[j] = lineData.get(instIndex + 1 + j);
                if (!isNumeric(fields[j])) {
                    if(DEBUG) System.out.println("C");
                    exit(1);
                } else {
                    fields[j] = toBinaryString(toNumeric(fields[j]));
                    fields[j] = fillBits("0", fields[j], 3);
                }
            }

            if (type.equals("R")) {
                machineCode += fields[0];
                machineCode += fields[1];
                machineCode += "0000000000000";
                machineCode += fields[2];
            } else if (type.equals("I")) {
                // check offsetField in [-32768,32767] & turn into 16 bit 2's compliment
                // fields[2]
                // check for symbolic address

                machineCode += fields[0];
                machineCode += fields[1];
                fields[2] = lineData.get(instIndex+3);
                int offsetField = 0;
                if(DEBUG) System.out.println("field[2] = " + fields[2]);
                if (isNumeric(fields[2])) {
                    offsetField = toNumeric(fields[2]);
                    if(DEBUG) System.out.println("field[2] isNumeric = " + offsetField);
                } else if (isLabel(fields[2])) {
                    if(inst.equals("beq")){
                        // Addr = PC+1+offsetField
                        // offsetField = Addr-PC-1
                        offsetField = labelMap.get(fields[2])-currentLine-1;
                    }else{
                        offsetField = labelMap.get(fields[2]);
                    }
                    if(DEBUG) System.out.println("field[2] isLabel = " + offsetField);
                } else {
                    if(DEBUG) System.out.println("D");
                    exit(1);
                }

                if (offsetField > 32767 || offsetField < -32768) {
                    if(DEBUG) System.out.println("E");
                    exit(1);
                } else {
                    String bin;
                    if (offsetField >= 0) {
                        bin = toBinaryString(offsetField);
                        System.out.println("bin: "+bin);
                        bin = fillBits("0", bin, 16);

                    } else {
                        offsetField = -offsetField;
                        bin = toBinaryString(offsetField);
                        bin = fillBits("0", bin, 16);
                        bin = twosCompliment(bin);
                    }

                    machineCode += bin;
                }
            } else if (type.equals("J")) {
                machineCode += fields[0];
                machineCode += fields[1];
                machineCode += "0000000000000000";
                
            } else if (type.equals("O")) {
                machineCode += "0000000000000000000000";
                
            } else if (inst.equals(".fill")) {
                machineCode = "";
                String field = lineData.get(instIndex + 1);
                System.out.println("instIndex: " + instIndex + " field: " + field);
                if (isNumeric(field)) {
                    int dec = toNumeric(field);
                    String bin;
                    if(dec < 0){
                        dec = -dec;
                        bin = toBinaryString(dec);
                        bin = fillBits("0", bin, 32);
                        machineCode = twosCompliment(bin);
                    }else{
                        bin = toBinaryString(dec);
                        machineCode = bin;
                        machineCode = fillBits("0", machineCode, 32);
                    }
                } else if (isLabel(field)) {
                    int dec = labelMap.get(field);
                    String bin = toBinaryString(dec);
                    machineCode = bin;
                    machineCode = fillBits("0", machineCode, 32);
                } else {
                    if(DEBUG) System.out.println("F");
                    exit(1);
                }
            }
            if(DEBUG) System.out.println("machineCode: " + machineCode);
            machineCodes.add(machineCode);

            currentLine++;
            loadLine();
        }

        return new ArrayList<String>(machineCodes);
    }

    public List<String> getDecimalMachineCodes(){
        List<String> dmc = new ArrayList<>();
        for (String mc : machineCodes) {
            dmc.add(toDecimal(mc)+"");
        }

        return dmc;
    }

    public void exit(int exitCode) {
        System.out.println("Exited with status " + exitCode);
        System.exit(exitCode);
    }

    public void printAssemblyWithCurrentLine() {
        System.out.println("line[" + currentLine + "] ");
    }

    private static String fillBits(String filler, String base, int length) {
        StringBuilder sb = new StringBuilder("");

        while (base.length() + sb.length() < length) {
            sb = sb.append(filler);
        }
        sb = sb.append(base);

        return sb.toString();
    }

    public static String twosCompliment(String bin) {
        String twos = "", ones = "";

        for (int i = 0; i < bin.length(); i++) {
            ones += flip(bin.charAt(i));
        }
        StringBuilder builder = new StringBuilder(ones);

        for (int i = ones.length() - 1; i > 0; i--) {
            if (ones.charAt(i) == '1') {
                builder.setCharAt(i, '0');
            } else {
                builder.setCharAt(i, '1');
                break;
            }
        }
        twos = builder.toString();
        return twos;
    }

    public static char flip(char c) {
        if (c == '0') {
            return '1';
        } else {
            return '0';
        }
    }
}
