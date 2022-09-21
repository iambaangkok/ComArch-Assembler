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

    private boolean isLineBreak(String token){
        return token == "\n";
    }

    private boolean isInstruction(String token){
        return TYPE_MAP.containsKey(token);
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

        String data = AssemblyReader.readFileToString(dir + fileName);
        if (DEBUG)
            System.out.println(data);

        // Iterate once to fill labelMap
        loadLine();
        while(!lineData.isEmpty()){
            if(!isInstruction(lineData.get(0))){
                if(isValidLabel(lineData.get(0))){
                    labelMap.put(lineData.get(0), currentLine);
                }
            }
            currentLine++;
            machineCode = MC_STARTER;
            loadLine();
        }
        //////////

        tok.resetIterator();
        currentLine = 0;

        // Iterate again to translate into machine code
        
    }

    public void printAssemblyWithCurrentLine(){
        System.out.println("line[" + currentLine + "] ");
    }
}
