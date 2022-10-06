import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        
        String srcDir = "src/assemblyFiles/";
        String srcFileName = "test01.s";

        String outputDir = "src/machineCodes/";
        String outputFileName = "test01.o";
        
        Assembler as = new Assembler(FileReaderWriter.readFileToString(srcDir+srcFileName));

        as.assembleIntoMachineCode();
        List<String> decimalMachineCodes = as.getDecimalMachineCodes();

        FileReaderWriter.writeStringToFile(outputDir + outputFileName, decimalMachineCodes);

    }
}
