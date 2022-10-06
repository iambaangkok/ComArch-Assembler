

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

    @Test
    public void testToDecimal(){
        Assembler as = new Assembler("");

        Assert.assertEquals(0, as.toDecimal("0"));
        Assert.assertEquals(1, as.toDecimal("01"));
        Assert.assertEquals(2, as.toDecimal("010"));
        Assert.assertEquals(7, as.toDecimal("0111"));
        Assert.assertEquals(7, as.toDecimal("0000111"));
        Assert.assertEquals(64+16+4+1, as.toDecimal("01010101"));

        Assert.assertEquals(-1, as.toDecimal("11"));
        Assert.assertEquals(-2, as.toDecimal("110"));
        
        Assert.assertEquals(-16, as.toDecimal("110000"));
        Assert.assertEquals(-11, as.toDecimal("110101"));


    }
    
}
