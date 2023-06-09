package specific;

import jas.core.Compiler;
import jas.core.Mode;
import jas.core.components.Constants;
import jas.core.components.RawValue;
import jas.core.components.Variable;
import jas.core.operations.Argument;
import jas.core.operations.Custom;
import jas.core.operations.Manipulation;
import jas.core.operations.Signature;

import static org.junit.jupiter.api.Assertions.*;
import static text.TestPrint.l;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Created by Jiachen on 3/17/18.
 * Composite Operation Test
 */
public class CustomOperationTest {

    @BeforeAll
    public static void setup() {
        Mode.DEBUG = true;

        Custom.register(new Manipulation("custom", new Signature(Argument.ANY), operands -> {
            double calc = Math.log(operands.get(0).numNodes());
            return new RawValue(calc);
        }));
    }

    @AfterAll
    public static void teardown() {
        Custom.unregister("custom", Signature.ANY);
    }

    @Test
    public void test_custom_operations() {
        final String exp = "sum(4+7+5,5+x,log(7+cos(x)),x)";
        Custom co = (Custom) Compiler.compile(exp);
        assertEquals(exp,co.toString());
        assertEquals(31.86234979511418,co.eval(5),0.0000001);
        assertEquals("log(7+cos(x))+21+2*x",co.simplify().toString());


        var compiled = Compiler.compile("expand(a*(b+c))").exec();
        assertEquals("b*a+c*a",compiled.toString());
    }
    public static void main(String args[]) {        
        l(Compiler.compile("a+log(3+a)+4"));
        Custom co = (Custom) Compiler.compile("sum(4+7+5,5+x,log(7+cos(x)),x)");
        l(co); //christ I finally did it!!!
        l(co.eval(5));
        l(co.simplify());
        l(RawValue.ONE.div(new Variable("x")).mult(Constants.E));
        l(RawValue.ONE.negate().div(RawValue.ONE.sub(new Variable("x").sq()).sqrt()));

//        l(Compiler.compile("a+log(3+a)+4"));
        l(Argument.NUMBER.equals(Argument.ANY));
        l(Argument.ANY.equals(Argument.NUMBER));
        l(Argument.VARIABLE.equals(Argument.NUMBER));
        l(Argument.NUMBER.equals(Argument.NUMBER));
        l(Argument.OPERATION.equals(Argument.NUMBER));
        l(Compiler.compile("expand(a*(b+c))").exec());

        

        l(Compiler.compile("custom(x+b-c)").val());
        
        //l(Compiler.compile("custom(x+b-c)").val());
    }
}
