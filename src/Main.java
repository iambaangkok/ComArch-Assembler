
public class Main {
    public static void main(String[] args) throws Exception {
        
        String data = AssemblyReader.readFileToString("src/assemblyFiles/test01.s");

        System.out.println(data);
    }
}
