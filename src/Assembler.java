import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Assembler {

    private boolean DEBUG = true;

    private String dir = "src/assemblyFiles/";

    private final Map<String, String> TYPE_MAP = Map.of(
        "add", "R",
        "nand", "R",
        "lw", "I",
        "sw", "I",
        "beq", "I",
        "jarl", "J",
        "halt", "O",
        "noop", "O"
    );
    private final Map<String, String> OPCODE_MAP = Map.of(
        "add", "000",
        "nand", "001",
        "lw", "010",
        "sw", "011",
        "beq", "100",
        "jarl", "101",
        "halt", "110",
        "noop", "111"
    );
    private final Map<String, Integer> FIELD_COUNT_MAP = Map.of(
        "add", 3,
        "nand", 3,
        "lw", 3,
        "sw", 3,
        "beq", 3,
        "jarl", 2,
        "halt", 0,
        "noop", 0
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

    public void assembleIntoMachineCode(){
        
        String fileName = "test01.s";

        String data = AssemblyFileReader.readFileToString(dir + fileName);
        if (DEBUG)
            System.out.println(data);

        // Iterate once to fill labelMap
        loadLine();
        while(!lineData.isEmpty()){
            if(!isInstruction(lineData.get(0))){
                if(isValidLabel(lineData.get(0))){
                    labelMap.put(lineData.get(0), currentLine);
                }else{
                    System.exit(1);
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
            int fieldCount = FIELD_COUNT_MAP.get(inst);
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

                machineCode += fields[0];
                machineCode += fields[1];
                if(Integer.parseInt(fields[2]) > 32767 ||Integer.parseInt(fields[2]) < -32768){
                    exit(1);
                }else{
                    int dec = Integer.parseInt(fields[2]);
                    String bin;
                    if(Integer.parseInt(fields[2]) >= 0){
                        bin = toBinaryString(dec);
        
                    }else {
                        dec = -dec;
                        bin = toBinaryString(dec);
                        // 2's complement
                        twosCompliment(bin);
                    }
                    //assign bin to 16-bit
                    if(bin.length() != 16){
                        StringBuilder builder = new StringBuilder();
                        for(int i = 0;i < 16-bin.length();i++){
                            builder.append(0);
                        }
                        bin = builder.toString()+bin;
                    }
                    machineCode += bin;
                }
            }else if(type == "J"){
                // check offsetField in [-32768,32767] & turn into 16 bit 2's compliment 
                // fields[2]

                machineCode += fields[0];
                machineCode += fields[1];
                machineCode += "0000000000000000";
            }else if(type == "O"){
                machineCode += "0000000000000000000000";
            }

            currentLine++;
            machineCode = MC_STARTER;
            loadLine();
        }
    }

    public void exit(int exitCode){
        System.exit(exitCode);
    }

    public void printAssemblyWithCurrentLine(){
        System.out.println("line[" + currentLine + "] ");
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

    public char flip(char c) {
        if(c == '0'){
            return '1';
        }else{
            return '0';
        }
    }
}
