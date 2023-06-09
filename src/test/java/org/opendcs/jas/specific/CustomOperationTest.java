package org.opendcs.jas.specific;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendcs.jas.core.Compiler;
import org.opendcs.jas.core.Mode;
import org.opendcs.jas.core.components.Constants;
import org.opendcs.jas.core.components.RawValue;
import org.opendcs.jas.core.components.Variable;
import org.opendcs.jas.core.operations.Argument;
import org.opendcs.jas.core.operations.Custom;
import org.opendcs.jas.core.operations.Manipulation;
import org.opendcs.jas.core.operations.Signature;

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
    @Disabled("parenthesis not getting matched right for some reason.")
    public void test_custom_operations() {
        final String exp = "sum(4+7+5,5+x,log(7+cos(x)),x)";
        Custom co = (Custom) Compiler.compile(exp);
        assertEquals(exp,co.toString());
        assertEquals(31.86234979511418,co.eval(5),0.0000001);
        assertEquals("log(7+cos(x))+21+2*x",co.simplify().toString());


        var compiled = Compiler.compile("expand(a*(b+c))").exec();
        assertEquals("b*a+c*a",compiled.toString());
    }
}
