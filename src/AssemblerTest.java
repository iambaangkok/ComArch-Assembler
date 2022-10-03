

import org.junit.Assert;
import org.junit.Test;

public class AssemblerTest {
    
    @Test
    public void testToBinaryString(){
        Assembler as = new Assembler("");

        Assert.assertEquals("0", as.toBinaryString(0));
        Assert.assertEquals("1", as.toBinaryString(1));
        Assert.assertEquals("10", as.toBinaryString(2));
        Assert.assertEquals("110", as.toBinaryString(6));
        Assert.assertEquals("1111111", as.toBinaryString(127));
        Assert.assertEquals("10000000", as.toBinaryString(128));

    }
    
}
