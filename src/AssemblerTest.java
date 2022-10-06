

import org.junit.Assert;
import org.junit.Test;

public class AssemblerTest {
    
    @Test
    public void testToBinaryString(){
        Assert.assertEquals("0", Assembler.toBinaryString(0));
        Assert.assertEquals("1", Assembler.toBinaryString(1));
        Assert.assertEquals("10", Assembler.toBinaryString(2));
        Assert.assertEquals("110", Assembler.toBinaryString(6));
        Assert.assertEquals("1111111", Assembler.toBinaryString(127));
        Assert.assertEquals("10000000", Assembler.toBinaryString(128));
    }

    @Test
    public void testToDecimal(){
        Assert.assertEquals(0, Assembler.toDecimal("0"));
        Assert.assertEquals(1, Assembler.toDecimal("01"));
        Assert.assertEquals(2, Assembler.toDecimal("010"));
        Assert.assertEquals(7, Assembler.toDecimal("0111"));
        Assert.assertEquals(7, Assembler.toDecimal("0000111"));
        Assert.assertEquals(64+16+4+1, Assembler.toDecimal("01010101"));

        Assert.assertEquals(-1, Assembler.toDecimal("11"));
        Assert.assertEquals(-2, Assembler.toDecimal("110"));
        Assert.assertEquals(-16, Assembler.toDecimal("110000"));
        Assert.assertEquals(-11, Assembler.toDecimal("110101"));
    }
    
}
