
import java.util.StringTokenizer;

public class Tokenizer {
    StringTokenizer st;
    /**
     * Tokenizer takes input as an assembly String and 
     * provides methods of iterating through the List<String> containing each token
     * 
     */


    Tokenizer(String assembly){
        st = new StringTokenizer(assembly," ");
    }

    public boolean hasNext(){
        return st.hasMoreTokens();
    }

    public String next(){
        return st.nextToken();
    }

    /**
     * resets the iterator to the first token
     */
    public void resetIterator(){
        
    }

}
