import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Assembler {

    private boolean DEBUG = true;

    private final Map<String, String> TYPE_MAP = Map.of(
        "add", "R",
        "nand", "R",
        "lw", "I",
        "sw", "I",
        "beq", "I",
        "jarl", "J",
        "halt", "O",
        "noop", "O",
        ".fill", "F"
    );
    private final Map<String, String> OPCODE_MAP = Map.of(
        "add", "000",
        "nand", "001",
        "lw", "010",
        "sw", "011",
        "beq", "100",
        "jarl", "101",
        "halt", "110",
        "noop", "111",
        ".fill", "FIL"
    );
    private final Map<String, Integer> NUMERIC_FIELD_COUNT_MAP = Map.of(
        "add", 3,
        "nand", 3,
        "lw", 2,
        "sw", 2,
        "beq", 2,
        "jarl", 2,
        "halt", 0,
        "noop", 0,
        ".fill", 0
    );
    private final String MC_STARTER = "0000000";        // bits[31-25] (7 bits) should always be 0 
    
    private final Pattern NUMERIC_PATTERN = Pattern.compile("-?\\d+(\\.\\d+)?");

    private Tokenizer tok;

    private Map<String, Integer> labelMap = new HashMap<>();

    private int currentLine = 0;
    private List<String> lineData = new ArrayList<>();
    private List<String> machineCodes = new ArrayList<>();
    private String machineCode = MC_STARTER;
    
    
    Assembler(String assembly){
        tok = new Tokenizer(assembly);
    }

    private void loadLine(){
        lineData.clear();

        while(tok.hasNext()){
            String token = tok.next();
            if (isLineBreak(token)){
                break;
            }

            lineData.add(token);
        }
    }

    private boolean isNumeric(String token){
        return NUMERIC_PATTERN.matcher(token).matches();
    }
    private int toNumeric(String token){
        return Integer.parseInt(token);
    }
    private String toBinaryString(int number){
        String bin = "";
        while(number != 0){
            bin = bin + number%2;
            number /= 2;
        }

        return bin;
    }

    private boolean isLineBreak(String token){
        return token == "\n";
    }

    private boolean isInstruction(String token){
        return TYPE_MAP.containsKey(token);
    }

    private boolean isLabel(String token){
        return labelMap.containsKey(token);
    }

    private boolean isValidLabel(String token){
        return !(
            token.length() > 6
            || labelMap.containsKey(token)
            || isNumeric(token.charAt(0) + "")
        );
    }

    public List<String> assembleIntoMachineCode(){
        // Iterate once to fill labelMap
        loadLine();
        while(!lineData.isEmpty()){
            if(DEBUG){
                System.out.println("Filling labelMap, lineData: " + lineData);
            }

            if(!isInstruction(lineData.get(0))){
                if(isValidLabel(lineData.get(0))){
                    labelMap.put(lineData.get(0), currentLine);
                }else{
                    exit(1);
                }
            }
            currentLine++;
            loadLine();
        }
        //////////

        tok.resetIterator();
        currentLine = 0;

        // Iterate again to translate into machine code
        loadLine();
        int instIndex = 0;
        while(!lineData.isEmpty()){

            if(!isInstruction(lineData.get(instIndex))){
                if(!isValidLabel(lineData.get(instIndex))){
                    exit(1);
                }else{
                    instIndex++;
                    if(!isInstruction(lineData.get(instIndex))){
                        exit(1);
                    }
                }
            }
            String inst = lineData.get(instIndex);
            String opcode = OPCODE_MAP.get(inst);
            String type = TYPE_MAP.get(inst);
            int fieldCount = NUMERIC_FIELD_COUNT_MAP.get(inst);
            String[] fields = {"", "", ""};

            machineCode = MC_STARTER;
            machineCode += opcode;
            
            for(int j = 0 ; j < fieldCount; ++j){
                fields[j]= lineData.get(instIndex+1+j);
                if(!isNumeric(fields[j])){
                    exit(1);
                }else{
                    fields[j] = toBinaryString(toNumeric(fields[j]));
                }
            }

            if(type == "R"){
                machineCode += fields[0];
                machineCode += fields[1];
                machineCode += "0000000000000";
                machineCode += fields[2];
            }else if(type == "I"){
                // check offsetField in [-32768,32767] & turn into 16 bit 2's compliment 
                // fields[2]
                // check for symbolic address

                machineCode += fields[0];
                machineCode += fields[1];
                int offsetField = 0;
                if(isNumeric(fields[2])){
                    offsetField = toNumeric(fields[2]);
                    
                }else if(isLabel(fields[2])){
                    offsetField = labelMap.get(fields[2]);
                }else{
                    exit(1);
                }
                if(offsetField> 32767 ||offsetField < -32768){
                    exit(1);
                }else{
                    String bin;
                    if(offsetField >= 0){
                        bin = toBinaryString(offsetField);
                        bin = fillBits("0",bin, 16);
        
                    }else {
                        offsetField = -offsetField;
                        bin = toBinaryString(offsetField);
                        bin = fillBits("0",bin, 16);
                        twosCompliment(bin);
                    }
                    
                    machineCode += bin;
                }
            }else if(type == "J"){
                machineCode += fields[0];
                machineCode += fields[1];
                machineCode += "0000000000000000";
            }else if(type == "O"){
                machineCode += "0000000000000000000000";
            }else if(inst == ".fill"){
                String field = lineData.get(instIndex+1);
                if(isNumeric(field)){
                    int dec = toNumeric(field);
                    String bin = toBinaryString(dec);
                    machineCode = bin;
                    machineCode = fillBits("0",machineCode, 32);
                }else if(isLabel(field)){
                    int dec = labelMap.get(field);
                    String bin = toBinaryString(dec);
                    machineCode = bin;
                    machineCode = fillBits("0",machineCode, 32);
                }else{
                    exit(1);
                }
            }

            machineCodes.add(machineCode);

            currentLine++;
            loadLine();
        }

        return new ArrayList<String>(machineCodes);
    }

    public void exit(int exitCode){
        System.out.println("Exit with status " + exitCode);
        System.exit(exitCode);
    }

    public void printAssemblyWithCurrentLine(){
        System.out.println("line[" + currentLine + "] ");
    }

    private String fillBits(String filler, String base, int length){
        StringBuilder sb = new StringBuilder("");

        while(base.length() + sb.length() < length){
            sb = sb.append(filler);
        }
        sb = sb.append(base);

        return sb.toString();
    }

    public String twosCompliment(String bin) {
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

    // public String addTo16Bit(String bin){
    //     if(bin.length() != 16){
    //         StringBuilder builder = new StringBuilder();
    //         for(int i = 0;i < 16-bin.length();i++){
    //             builder.append(0);
    //         }
    //         bin = builder.toString()+bin;
    //     }
    //     return bin;
    // }

    public char flip(char c) {
        if(c == '0'){
            return '1';
        }else{
            return '0';
        }
    }
}
