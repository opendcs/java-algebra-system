package jas.core;

import jas.core.components.*;
import jas.core.operations.Binary;
import jas.core.operations.Custom;
import jas.core.operations.Unary;

import java.util.ArrayList;

import static jas.core.Assets.*;
import static jas.utils.ColorFormatter.*;

/**
 * Created by Jiachen on 19/05/2017.
 * Compiler class that programmatically interprets mathematical expressions like "ln(x)^2/e*pi" into binary code.
 * An expression must be a function, but a function might not be able to be represented using an expression.
 */
public class Compiler {

    /**
     * @param exp the string representation of an expression to be incorporated into JMC.
     * @return the Function instance derived from the String representation
     * @since May 19th. This method is the core of JMC. Took Jiachen tremendous effort. This system of
     * method represents his life's work
     */
    public static Node compile(String exp) {
        if (exp.toLowerCase().contains("undef")) return RawValue.UNDEF;
        if ((exp = exp.replace(" ", "")).equals("")) throw new JASException("cannot compile an empty string");
        if (exp.contains(">") || exp.contains("<")) throw new JASException("angle brackets '<>' no longer supported");
        if (numOccurrence(exp, '(') != numOccurrence(exp, ')'))
            throw new JASException("'()' mismatch in " + "\"" + exp + "\"");
        if (exp.contains("&") || exp.contains("#")) throw new JASException("syntax: remove illegal character(s) &, #");
        if (numOccurrence(exp, '{') != numOccurrence(exp, '}'))
            throw new JASException("'{}' mismatch in " + "\"" + exp + "\"");
        if (numOccurrence(exp, '\'') % 2 != 0) throw new JASException("'' mismatch in " + "\"" + exp + "\"");
        exp = formatList(exp); // this has to happen before formatOperation()
        exp = formatOperations(exp.replace("(-", "(0-").replace(",-", ",0-"));
        exp = formatCoefficients(exp);
        exp = formatParenthesis(exp);
        exp = formatLiteral(exp);
        log(boldBlack("formatted input: ") + exp);
        ArrayList<Node> components = new ArrayList<>();
        int hashId = 0;
        while (exp.indexOf(')') != -1) {
            int[] indices = innermostIndices(exp, '(', ')');
            String innermost = exp.substring(indices[0] + 1, indices[1]);
            Node compiled = generateOperations(innermost, components);
            components.add(compiled);
            String left = indices[0] > 0 ? exp.substring(0, indices[0]) : "";
            exp = left + "&" + hashId + exp.substring(indices[1] + 1);
            log(lightCyan("func:\t") + colorMathSymbols(exp));
            hashId++;
        }
        Node node = generateOperations(exp, components);
        if (node instanceof Binary) ((Binary) node).setOmitParenthesis(true);
        String colored = colorMathSymbols(node.toString());
        log(lightRed("output:\t") + colored);
        return node;
    }

    private static String formatLiteral(String exp) {
        exp = exp.replace("'", "''");
        int i = 0;
        while (exp.contains("''")) {
            exp = exp.replaceFirst("''", i % 2 == 0 ? "('" : "')");
            i++;
        }
        return exp;
    }

    private static String formatList(String exp) {
        while (exp.contains("{")) {
            exp = replace(exp, '{', '}', "list(", ")");
        }
        return exp;
    }

    /**
     * @param exp    the expression to be modified
     * @param open   open bracket symbol
     * @param close  close bracket symbol
     * @param open1  replacement for "open"
     * @param close1 replacement for "close"
     * @return expression with "open" replaced with "open1" and close replaced with "close1"
     */
    @SuppressWarnings("SameParameterValue")
    private static String replace(String exp, char open, char close, String open1, String close1) {
        int[] indices = innermostIndices(exp, open, close);
        return exp.substring(0, indices[0])
                + open1 + exp.substring(indices[0] + 1, indices[1]) + close1
                + exp.substring(indices[1] + 1, exp.length());
    }


    private static String formatOperations(String exp) {
        while (isUnformatted(exp)) {
            int idx1 = -1;
            for (String name : Assets.reservedNames()) {
                String prefix = name + "(";
                if (exp.contains(prefix)) {
                    idx1 = exp.indexOf(prefix) + prefix.length() - 1;
                    break;
                }
            }
            int idx2 = findMatchingIndex(exp, idx1, ')');
            exp = replaceAt(replaceAt(exp, idx1, "<("), idx2 + 1, ")>");
        }
        return exp;
    }

    private static String replaceAt(String s, int idx, String c) {
        return s.substring(0, idx) + c + s.substring(idx + 1);
    }

    private static boolean isUnformatted(String exp) {
        for (String s : Assets.reservedNames()) {
            if (exp.contains(s + "("))
                return true;
        }
        return false;
    }

    public static String colorMathSymbols(String exp) {
        String colored = coloredLine("[36;1m", exp, "<", ">");
        colored = coloredLine("[1;m", colored, "+", "-", "*", "/", "^");
        colored = coloredLine("[1;34m", colored, ")", "(");
        colored = coloredLine("[34;1m", colored, "#");
        colored = coloredLine("[31;1m", colored, "&");
        colored = removeUnnecessaryDecimal(colored);
        return colored;
    }

    private static String removeUnnecessaryDecimal(String exp) {
        for (int i = 0; i < exp.length() - 1; i++) {
            String content = exp.substring(i, i + 2);
            if (content.equals(".0")) {
                if (i + 2 == exp.length()) {
                    exp = exp.substring(0, i);
                } else if (!DIGITS.contains(exp.substring(i + 2, i + 3))) {
                    String left = i == 0 ? "" : exp.substring(0, i);
                    exp = left + exp.substring(i + 2);
                }
            }
        }
        return exp;
    }


    /**
     * @param exp the expression to be analyzed.
     * @return the indices of the innermost opening parenthesis and the closing parenthesis
     * @since May 16th
     */
    private static int[] innermostIndices(String exp, char open, char close) {
        int closeIndex = exp.indexOf(close);
        int openIndex = exp.substring(0, closeIndex).lastIndexOf(open);
        return new int[]{openIndex, closeIndex};
    }

    private static void flat(Node tree, ArrayList<Node> flattened) {
        if (tree instanceof Binary && ((Binary) tree).is(",")) {
            Binary binOp = ((Binary) tree);
            flat(binOp.getLeft(), flattened);
            flat(binOp.getRight(), flattened);
        } else {
            if (tree instanceof Binary) ((Binary) tree).setOmitParenthesis(true);
            flattened.add(tree);
        }
    }

    private static Node generateOperations(String segment, ArrayList<Node> nodes) {
        log(lightGreen("exp:\t") + colorMathSymbols(segment)); // Colored output
        int nodeHashId = 0;
        ArrayList<Node> pending = new ArrayList<>(); // Pending operations
        while (segment.indexOf('<') != -1) {
            int indices[] = innermostIndices(segment, '<', '>');
            String extracted = segment.substring(indices[0] + 1, indices[1]);
            Node operand = generateOperations(extracted, nodes);
            String operationName = segment.substring(0, indices[0]);
            int startIndex = 0;
            for (int i = indices[0] - 1; i >= 0; i--) {
                char c = segment.charAt(i);
                if (isSymbol(c)) {
                    operationName = segment.substring(i + 1, indices[0]);
                    startIndex = i;
                    break;
                }
            }
            if (operationName.equals("list")) {
                ArrayList<Node> list = new ArrayList<>();
                if (!isList(operand)) {
                    if (!operand.equals(RawValue.UNDEF))
                        list.add(operand);
                } else list = toList(operand);
                pending.add(new List(list));
            } else if (isList(operand)) {
                pending.add(new Custom(operationName, toList(operand)));
            } else {
                ensureValidity(operand);
                if (Unary.isDefined(operationName)) {
                    pending.add(new Unary(operand, operationName));
                } else pending.add(new Custom(operationName, operand));
            }
            String left = startIndex == 0 ? "" : segment.substring(0, startIndex + 1);
            log(lightBlue("unary:\t") + colorMathSymbols(segment));
            segment = left + "#" + nodeHashId + segment.substring(indices[1] + 1);
            nodeHashId++;
        }
        for (int p = 1; p <= 4; p++) { //prioritize ^ over */ over +-. This way it is flexible for adding more operations.
            for (int i = 0; i < segment.length(); i++) { // p == 4 -> ',' for composite operations
                CharSequence op = segment.subSequence(i, i + 1);
                if (Binary.operators(p).contains(op) || p == 4 && op.charAt(0) == ',') {
                    int[] indices = operationIndices(segment, i);
                    String[] operandStrs = new String[]{segment.substring(indices[0], i), segment.substring(i + 1, indices[1] + 1)};
                    ArrayList<Node> operands = getOperands(pending, nodes, operandStrs);
                    ensureValidity(operands); // check to see if operands are missing
                    Binary operation = new Binary(operands.get(0), op.toString(), operands.get(1));
                    pending.add(operation);
                    String left = indices[0] == 0 ? "" : segment.substring(0, indices[0]);
                    segment = left + "#" + nodeHashId + segment.substring(indices[1] + 1);
                    log("->\t\t" + colorMathSymbols(segment));
                    i = 0;
                    nodeHashId++;
                }
            }
        }
        if (pending.size() == 0) {
            return getOperands(pending, nodes, new String[]{segment}).get(0);
        }
        return pending.get(pending.size() - 1);
    }

    /**
     * @param operand a list in the form of a binary tree -> (a,b),c...
     * @return list converted from the binary tree.
     */
    private static ArrayList<Node> toList(Node operand) {
        Binary tree = ((Binary) operand);
        ArrayList<Node> operands = new ArrayList<>();
        ensureValidity(operands);
        flat(tree, operands);
        return operands;
    }

    private static boolean isList(Node node) {
        return node instanceof Binary && ((Binary) node).is(",");
    }

    private static ArrayList<Node> getOperands(ArrayList<Node> pending, ArrayList<Node> nodes, String[] operandStrs) {
        ArrayList<Node> operands = new ArrayList<>();
        for (String operand : operandStrs) {
            if (operand.equals("")) { // pending operands... x/... x*..., etc.
                operands.add(RawValue.UNDEF);
                continue;
            }
            int componentIndex = operand.indexOf("#");
            int bundleIndex = operand.indexOf("&");
            if (componentIndex != -1)
                operands.add(pending.get(Integer.valueOf(operand.substring(componentIndex + 1))));
            else if (bundleIndex != -1)
                operands.add(nodes.get(Integer.valueOf(operand.substring(bundleIndex + 1))));
            else if (Constants.contains(operand)) {
                operands.add(Constants.get(operand));
            } else if (operand.charAt(0) == '\'') {
                if (numOccurrence(operand, '\'') != 2)
                    throw new JASException("syntax error due to ' in \"" + operand + "\"");
                else operands.add(new Literal(operand.substring(1, operand.length() - 1)));
            } else if (RawValue.isNumeric(operand)) operands.add(new RawValue(Double.valueOf(operand)));
            else if (isValidVarName(operand)) operands.add(new Variable(operand.toLowerCase()));
            else throw new JASException("unresolved symbol \"" + operand + "\"");
        }
        return operands;
    }

    private static void ensureValidity(ArrayList<Node> operands) {
        operands.forEach(Compiler::ensureValidity);
    }

    private static void ensureValidity(Node node) {
        if (node.equals(RawValue.UNDEF))
            throw new JASException("missing operand(s)");
    }


    /**
     * e.g. call to operationIndices("a*2*x^2-a*x^2",5) would give {4,6}
     *
     * @param segment the segment of expression
     * @param index   index of the binary operation
     * @return the indices of the beginning and the end of the binary operation, both inclusive
     */
    private static int[] operationIndices(String segment, int index) {
        String left = segment.substring(0, index);
        String right = segment.substring(index + 1);
        int begin = 0;
        for (int i = 0; i < left.length(); i++) {
            char op = left.charAt(i);
            if (isSymbol(op))
                begin = i + 1;
        }

        int end = segment.length() - 1;// debugged May 16th
        for (int i = 0; i < right.length(); i++) {
            char op = right.charAt(i);
            if (isSymbol(op)) {
                end = index + i;
                break;
            }
        }
        //log("single statement indices: " + begin + " " + end);// debug completed May 16th
        return new int[]{begin, end};
    }


    /**
     * @param s String that is going to be indexed for occurrence of char c
     * @param c char c for num of occurrence.
     * @return the number of occurrence of char c in String s.
     */
    private static int numOccurrence(String s, char c) {
        int count = 0;
        while (s.indexOf(c) != -1) {
            s = s.substring(s.indexOf(c) + 1);
            count++;
        }
        return count;
    }

    /**
     * Formats the raw String to prepare it for the formulation into a Function instance.
     * For instance, the call to formatCoefficients("x+2x^2+3x+4") would return "x+2*x^2+3*x+4"
     *
     * @param exp the expression to be formatted
     * @return the formatted expression.
     * @since May 16th: also handles the special unary operator that is the same as "-", so
     * "-6" would become "(0-6)"
     */
    private static String formatCoefficients(String exp) {
        for (int i = 0; i < exp.length() - 1; i++) {
            char digit = exp.charAt(i), x = exp.charAt(i + 1);
            if (DIGITS.contains(Character.toString(digit)) || digit == ')') {
                if (VARS.contains(Character.toString(x)) || startsWithConstant(exp.substring(i + 1))) {
                    exp = exp.replace(digit + "" + x, digit + "*" + x);
                }
            }
        }
        if (exp.charAt(0) == '-') exp = "0" + exp; // -x becomes 0-x
        for (int i = 1; i < exp.length() - 1; i++) {
            char subtract = exp.charAt(i), symbol = exp.charAt(i - 1);
            if (subtract == '-' && "(*/^<".contains(Character.toString(symbol))) {
                int[] indices = operationIndices(exp, i);
                String extracted = exp.substring(i, indices[1] + 1);
                exp = exp.replace(symbol + "" + extracted, symbol + "(0" + extracted + ")");
            }
        }
        return exp;
    }

    private static boolean startsWithConstant(String exp) {
        for (Constants.Constant constant : Constants.list()) {
            if (exp.startsWith(constant.toString()))
                return true;
        }
        return false;
    }

    /**
     * findMatchingIndex("(56+(34+2))*(32+(13+34+2))",4,')') would return 9
     * findMatchingIndex("(56+(34+2))*(32+(13+34+2))",0,')') would return 10
     *
     * @param exp        the expression to be inspected for a matched pair of closure.
     * @param init       the initial index, i.e beginning of the designated closure
     * @param lookingFor the char that signifies the termination of a stack of the closure.
     * @return the matching index in which the closure correspond to init terminates.
     * @since May 19th most efficient method I've ever wrote.
     */
    private static int findMatchingIndex(String exp, int init, char lookingFor) {
        char start = exp.charAt(init);
        for (int i = init + 1; i < exp.length(); i++) {
            char cur = exp.charAt(i);
            if (cur == start) i = findMatchingIndex(exp, i, lookingFor);
            else if (cur == lookingFor) return i;
        }
        return -1;
    }

    private static String formatParenthesis(String exp) {
        String candidates = LETTERS + DIGITS;
        for (char c : candidates.toCharArray()) {
            exp = exp.replace(">" + c, ">" + "*" + c); // for forms like log<&0>x
            exp = exp.replace(")" + c, ")*" + c);
        }
        candidates += ")>";
        for (char c : candidates.toCharArray()) {
            exp = exp.replace(c + "(", c + "*(");
        }
        return exp;
    }

    private static void log(Object o) {
        if (Mode.DEBUG) System.out.println(o);
    }

}
