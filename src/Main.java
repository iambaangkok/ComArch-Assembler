import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        
        String srcDir = "src/assemblyFiles/";
        String srcFileName = "test01.s";

        String outputDir = "src/machineCodes/";
        String outputFileName = "test01.o";
        
        Assembler as = new Assembler(FileReaderWriter.readFileToString(srcDir+srcFileName));

        List<String> machineCodes = as.assembleIntoMachineCode();

        FileReaderWriter.writeStringToFile(outputDir + outputFileName, machineCodes);

    }
}
