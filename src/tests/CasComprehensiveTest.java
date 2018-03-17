package tests;

import jmc.Function;
import jmc.cas.*;
import jmc.cas.Compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static jmc.utils.ColorFormatter.*;
import static tests.TestPrint.l;

/**
 * Created by Jiachen on 16/05/2017.
 * toExponentialForm test
 */
public class CasComprehensiveTest {
    public static void main(String args[]) {
        //to exponential form test
        Operation operation = (Operation) Compiler.compile("x/(x-1)/(x+1/(x-1))");
        operation.toExponentialForm();
        operation.toAdditionOnly();
        l(Compiler.colorMathSymbols(operation.toString()));

        //to addition only test
        operation = (Operation) Compiler.compile("x-3x^2+4x-5");
        operation.toAdditionOnly();
        l(Compiler.colorMathSymbols(operation.toString()));

        //plug in test
        operation = (Operation) Compiler.compile("ln<log<x^(2*e^2+x)>>^(1/5)/(x^3+2*x+9)^(1/3*e*x)");
        operation.plugIn(new Variable("x"), Compiler.compile("h"));
        l(operation);

        //fraction test
        RawValue f1 = Fraction.convertToFraction(3.1415926535766);
        RawValue f2 = Fraction.convertToFraction(0.1403508772, 5E-7);
        RawValue f3 = Fraction.convertToFraction(12.375);
        RawValue f4 = Fraction.convertToFraction(12.375, 5E-14);
        RawValue f5 = new Fraction(5, 5);
        RawValue f6 = new Fraction(4, 4).reduce();
        l(f1, f2, f3, f4, f5, f6);

        RawValue raw = new RawValue(14.0);
        RawValue raw1 = new RawValue(14.3);
        l(raw.isInteger());

        Fraction.TOLERANCE = 5E-7;

        Fraction f7 = new Fraction(11, 7);
        Fraction f8 = new Fraction(6, 14);
        l(f7 + " + " + f8 + " = " + f7.copy().add(f8));
        l(f8 + " - " + f7 + " = " + f8.copy().sub(f7));
        l(raw1 + " + " + f7 + " = " + f7.copy().add(raw1));
        l(f7 + " x " + f8 + " = " + f7.copy().mult(f8));
        l(f8 + " x " + raw1 + " = " + f8.copy().mult(raw1));
        l(f8 + " / " + raw1 + " = " + f8.copy().div(raw1));
        l(f8 + " / " + f7 + " = " + f8.copy().div(f7));

        Operation op1 = (Operation) Compiler.compile("(3 + 4.5) * ln(5.3 + 4) / 2.7 / (x + 1) * x / 3");
        l(((BinaryOperation) op1).flattened());
        Operation op2 = (Operation) Compiler.compile("3 - 2x + 4x - 4 + 7pi");
        l(((BinaryOperation) op2).flattened());

        l(op1.numNodes(), op2.numNodes());

        Operable c = new Constants.Constant("e", () -> Math.E);
        l(c);

        ArrayList<String> ops = new ArrayList<>();
        Collections.addAll(ops, "(3 + 4.5x) * 5.3 / 2.7 * (5x + log(10))",
                "2^((x^2)^3)", "1473x",
                "0/0", "0/0 + 0",
                "5 + 7 -log(11) + e^2",
                "0^x",
                "(ln(x)+3)*5"
        );
        List<Operable> operables;
        operables = ops.stream().map(Compiler::compile).collect(Collectors.toList());
        operables.forEach(op -> {
            l(boldBlack("original: ") + Compiler.colorMathSymbols(op.toString()));
            l(boldBlack("plug in 5 for x: ") + op.copy().plugIn(new Variable("x"), new RawValue(5)));
            l(boldBlack("evaluated at 5: ") + op.copy().plugIn(new Variable("x"), new RawValue(5)).val());
            l(lightCyan("arbitrary value: ") + op.val());
            l(boldBlack("# vars: ") + Operable.numVars(op));
            l(lightRed("undefined: ") + op.isUndefined());
            l(lightBlue("level of x: ") + op.levelOf(new Variable("x")));
            if (op instanceof Operation) {
                l(lightGreen("simplified: ") + Compiler.colorMathSymbols(op.copy().simplify().toString()));
            }
            l("");
        });


        // RawValue
        l(new RawValue(5).equals(new RawValue(5.0)));
        l(RawValue.UNDEF.isUndefined());
        l(RawValue.UNDEF);
        l(RawValue.UNDEF.equals(RawValue.UNDEF));
        l(RawValue.INFINITY.doubleValue());
        l(RawValue.UNDEF.isInteger(), RawValue.INFINITY.negate().isInteger());
        l(RawValue.ONE);

        l(new Fraction(3, 0).isUndefined());
        l(Fraction.UNDEF.isUndefined());

        Operable operable = Compiler.compile("0^0");
        l(operable.eval(3));

        l(Operable.numVars(Compiler.compile("a+b*x+a+b/ln(c+e+pi)")));
        l(Operable.isMultiVar(Compiler.compile("x*3+e")));
        Operable operable1 = Compiler.compile("(x+a)*-3*(x+a)").simplify();
        l(operable1, operable1.explicitNegativeForm(), operable1);

        l(Compiler.compile("x*3+e").replace(new BinaryOperation(new Variable("x"), "*", new RawValue(3)), new Variable("a")));
        l(Compiler.compile("x*3").equals(Compiler.compile("3*x")));
        l(Compiler.compile("-6*x*-7").simplify().explicitNegativeForm());
        l(Compiler.compile("-1*x").simplify().explicitNegativeForm());


        l(BinaryOperation.expFormIdx(Compiler.compile("x^(-3/4)").simplify()));
        l(BinaryOperation.expFormIdx(Compiler.compile("x^(-4)").simplify()));
        l(BinaryOperation.expFormIdx(Compiler.compile("x^((-4)*x/3)").simplify()));


        // Constants
        l(Constants.E, Constants.PI, Constants.π);
        l(Constants.E.equals(Constants.E), Constants.π.equals(Constants.PI));
        l(Constants.getConstant("e").val());
        l(Constants.valueOf(Constants.E.getName()));


        // Function
        Function function = new Function(Compiler.compile("x^2"));
        l(function.eval(3), function.numericalSolve(3, -10, 10, 0.0001));
        function.setEvaluable((x) -> x * x);
        l(function.getEvaluable().eval(3));

    }

}
