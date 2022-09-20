import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class AssemblyReader {

    /**
     * reads an assembly file and turns it into a long String
     * @param path the path to the source file
     * @return a String which concatenates every line of data in the source file
     */
    public static String readFileToString(String path){
        String data = "";
        try {
            File file = new File(path);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                data += sc.nextLine() + '\n';
            }
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            // e.printStackTrace();
        }
        return data;
    }
}