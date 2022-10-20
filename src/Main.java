import java.util.List;

public class Main {
    private static String SRC_DIR = "src/assemblyFiles/";
    private static String SRC_EXT = ".s";
    private static String OUTPUT_DIR = "src/machineCodes/";

    public static void main(String[] args) throws Exception {
        
        // compile("_multiplication");
        compile("_combination");
        // compile("test",1,1);

    }

    /**
     * Compile a single assembly file
     * @param fileName the name of the assembly file to be compiled
     */
    public static void compile(String fileName){
        Assembler as = new Assembler(FileReaderWriter.readFileToString(SRC_DIR + fileName + SRC_EXT));

        List<String> machineCodes = as.assembleIntoMachineCode();
        List<String> decimalMachineCodes = as.getDecimalMachineCodes();

        FileReaderWriter.writeStringToFile(OUTPUT_DIR + fileName+".bin", machineCodes);
        FileReaderWriter.writeStringToFile(OUTPUT_DIR + fileName+".txt", decimalMachineCodes);
    }
    /**
     * Compiles a series of continuous files
     * @param prefix file name prefix
     * @param start index of the first file
     * @param end index of the last file (inclusive)
     */
    public static void compile(String prefix, int start, int end){
        for(int i = start ; i <= end; ++i){
            compile(prefix+i);
        }
    }
}
