package specific;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import jas.core.Compiler;
import jas.core.components.RawValue;
import jas.core.operations.Binary;
import jas.core.operations.Operation;
import text.TestPrint;

/**
 * Created by Jiachen on 3/7/18.
 * Binary Test
 */
public class BinaryOperationTest {

    @Test
    public void test_binary_operations() { 
        Binary binOp = new Binary(RawValue.ZERO, "*", RawValue.ONE);

        assertEquals(RawValue.ZERO,binOp.getLeft());
        assertEquals(RawValue.ONE,binOp.getRight());
        assertTrue(binOp.is("*"));

        Binary.define("&", 3, (a, b) -> a + b);
        assertTrue(Binary.operators().contains("&"));
        assertTrue(Binary.operators(3).contains("&"));
        
        assertEquals(3*5,Operation.mult(3, 5).val());
        assertEquals(Math.pow(2,3),Operation.exp(2, new RawValue(3)).val());
        
        assertTrue(((Binary)Compiler.compile("a+b")).isCommutative());
    }

    @Test
    public void test_simplification() {
        String result = Compiler.compile("x+x*a").simplify().toString();
        assertEquals("x+x*a",result);
    }

    public static void main(String args[]) {
        Binary binOp = new Binary(RawValue.ZERO, "*", RawValue.ONE);
        l(binOp.getRight(), binOp.getLeft());
        binOp.setRight(RawValue.ONE);
        Binary.define("&", 3, (a, b) -> a + b);
        l(Binary.operators(), Binary.operators(3));
        l(Binary.getPriority("&"), Binary.getPriority("+"));
        l(new Binary(RawValue.ZERO, "*", RawValue.ONE).getPriority());
        l(new Binary(RawValue.ZERO, "*", RawValue.ONE).flattened());
        l(binOp.is("*"));
        l(Operation.mult(3, 5));
        l(Operation.exp(Math.random(), new RawValue(3)));
        l(Operation.exp(3, Math.random()));
        l(Compiler.compile("x+x*a").simplify());
        ((Binary) Compiler.compile("x^b")).flattened().forEach(TestPrint::l);
        l(Operation.div(17, 4).setOperand(binOp.getOperand(1), 1).setOperands(binOp.getOperands()));
        l(Operation.div(3, 4).setLeft(new RawValue(5)));
        l(((Binary) Compiler.compile("a+b")).isCommutative());
        l(Compiler.compile("a*c*d-1*-1"));
    }

    private static void l(Object... objects) {
        for (Object o : objects) {
            l(o);
        }
    }

    private static void l(Object o) {
        System.out.println(o);
    }
}
