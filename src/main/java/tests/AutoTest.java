package tests;

import jas.core.Compiler;
import jas.core.JASException;
import jas.core.Node;
import jas.core.operations.Binary;
import jas.utils.Utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static jas.utils.ColorFormatter.*;
import static tests.TestPrint.l;

/**
 * Created by Jiachen on 3/10/18.
 * Automatic Test
 */
@SuppressWarnings("unused")
public class AutoTest {
    public static boolean WRITE = false;

    public static void main(String args[]) throws Exception {
        configureCAS();
        Compiler.compile("((-1)*ln(x)+a)*(4+x)*cos(a)+(c+x)*sin(a)");

        l(boldBlack("Updating expression library... this takes a while..."));
        updateCandidates(
//                args
        );

        String tests[] = new String[]{
                "testSimplify",
                "testSimplest",
                "testNumNodes",
                "testExpand",
                "testToExponentialForm",
                "testToAdditionOnly",
                "testBeautify",
                "testComplexity"
        };
        for (String test : tests) {
            getMethod(AutoTest.class, test).invoke(null);
        }
    }

    private static void testComplexity() throws Exception {
        l(lightPurple("\n---------> Complexity \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/complexity/bin_ops.txt", false, "complexity");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/complexity/u_ops.txt", false, "complexity");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/complexity/irr_num.txt", false, "complexity");
    }


    private static void testSimplify() throws Exception {
        l(lightPurple("\n---------> Simplification \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/simplification/bin_ops.txt", true, "simplify");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/simplification/u_ops.txt", true, "simplify");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/simplification/irr_num.txt", true, "simplify");
    }

    private static void testSimplest() throws Exception {
        l(lightPurple("\n---------> Simplest (Complex Simplification) \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/simplest/bin_ops.txt", true, "simplest");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/simplest/u_ops.txt", true, "simplest");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/simplest/irr_num.txt", true, "simplest");
    }

    private static void testBeautify() throws Exception {
        l(lightPurple("\n---------> Beautification \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/beautify/bin_ops.txt", true, "simplify", "beautify");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/beautify/u_ops.txt", true, "simplify", "beautify");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/beautify/irr_num.txt", true, "simplify", "beautify");
    }

    private static void testNumNodes() throws Exception {
        l(lightPurple("\n---------> Number of Nodes \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/nodes/bin_ops.txt", false, "numNodes");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/nodes/u_ops.txt", false, "numNodes");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/nodes/irr_num.txt", false, "numNodes");
    }

    private static void testExpand() throws Exception {
        l(lightPurple("\n---------> Expansion \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/expansion/bin_ops.txt", true, "expand", "simplify");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/expansion/u_ops.txt", true, "expand", "simplify");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/expansion/irr_num.txt", true, "expand", "simplify");
    }

    private static void testToExponentialForm() throws Exception {
        l(lightPurple("\n---------> Exponential Form \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/exponential/bin_ops.txt", true, "toExponentialForm");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/exponential/u_ops.txt", true, "toExponentialForm");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/exponential/irr_num.txt", true, "toExponentialForm");
    }

    private static void testToAdditionOnly() throws Exception {
        l(lightPurple("\n---------> Addition Only Form \n"));
        l(boldBlack("\n---------> Binary Operations Test \n"));
        test("/tests/files/additional/bin_ops.txt", true, "toAdditionOnly");
        l(boldBlack("\n---------> Unary Operations Test \n"));
        test("/tests/files/additional/u_ops.txt", true, "toAdditionOnly");
        l(boldBlack("\n---------> Irrational Numbers Test \n"));
        test("/tests/files/additional/irr_num.txt", true, "toAdditionOnly");
    }


    @SuppressWarnings("unchecked")
    private static Method getMethod(Class c, String name) {
        try {
            return c.getDeclaredMethod(name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("this exception will never happen");
    }

    private static ArrayList<String> getLines(String raw) {
        ArrayList<String> lines = new ArrayList<>();
        Collections.addAll(lines, raw.split("\n"));
        return lines;
    }

    /**
     * update the expressions in /additional, /exponential, /simplification according to ones listed in /expressions
     */
    private static void updateCandidates(String... subdirectories) {
        ArrayList<ArrayList<String>> pool = new ArrayList<>();
        String baseDir = "/tests/files/", ext = ".txt";
        String[] types = new String[]{"bin_ops", "u_ops", "irr_num"};
        for (String type : types) {
            pool.add(getLines(Utils.read(baseDir + type + ext)));
        }
        for (String subDir : subdirectories) {
            for (int m = 0; m < types.length; m++) {
                String type = types[m];
                String dir = baseDir + subDir + type + ext;
                ArrayList<String> lines = getLines(Utils.read(dir));
                for (String newLine : pool.get(m)) {
                    if (!contains(lines, newLine)) {
                        lines.add(getOriginal(newLine));
                        l(lightGreen("\"" + getOriginal(newLine) + "\"") + " added to " + lightBlue(dir));
                    }
                }
                writeLines(dir, lines);
                l(dir + lightGreen(" ... UPDATED"));
            }
        }
    }

    private static boolean contains(ArrayList<String> lines, String s) {
        for (String line : lines) {
            String original = getOriginal(line);
            try {
                if (Compiler.compile(original).equals(Compiler.compile(getOriginal(s)))) {
                    return true;
                }
            } catch (JASException e) {
                return false;
            }
        }
        return false;
    }

    private static String getOriginal(String l) {
        return l.contains("->") ? l.substring(0, l.indexOf("->")) : l;
    }

    private static String getComputed(String l) {
        return l.contains("->") ? l.substring(l.indexOf("->") + 2) : "";
    }

    private static void test(String fileName, boolean testValue, String... methods) throws Exception {
        ArrayList<String> lines = getLines(Utils.read(fileName));
        ArrayList<Node> ops = (ArrayList<Node>) lines.stream()
                .map(l -> Compiler.compile(getOriginal(l)))
                .collect(Collectors.toList());

        ArrayList<String> simplifiedStrs = (ArrayList<String>) lines.stream()
                .map(AutoTest::getComputed)
                .collect(Collectors.toList());

        int maxLength = 0;
        for (Node op : ops) {
            int l = op.toString().length();
            maxLength = l > maxLength ? l : maxLength;
        }
        maxLength += 1;

        int finalMaxLength = maxLength;
        lines = (ArrayList<String>) ops.stream()
                .map(op -> ensureLength(op.toString(), finalMaxLength))
                .collect(Collectors.toList());

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i), prev = simplifiedStrs.get(i).replace(" ", "");
            Object obj = ops.get(i).copy();
            for (String method : methods) {
                if (obj instanceof Node) {
                    obj = getMethod(Node.class, method).invoke(obj);
                } else break;
            }

            String now = obj.toString();
            if (!prev.equals(now)) {
                l(line + lightRed("CHANGED ")
                        + lightBlue(prev) + lightRed(" ≠ ")
                        + lightCyan(now));
            }
            if (testValue) {
                Node o1 = Compiler.compile(line), o2 = Compiler.compile(now);
                if (o1.numVars() > 0) {
                    ArrayList<String> errs = new ArrayList<>();
                    for (int k = 0; k <= 10; k++) {
                        int t = (int) Math.pow(k, 2);
                        double diffx = o1.eval(k) - o2.eval(k);
                        if (diffx > 1E-10) {
                            errs.add(ensureLength("", maxLength) + boldBlack("DIFF(x=" + t + "): ") + lightRed(diffx));
                        }
                    }
                    if (errs.size() > 0) {
                        if (prev.equals(now))
                            l(line + lightGreen("SAME") + boldBlack(" = ") + lightBlue(prev + " "));
                        errs.forEach(TestPrint::l);
                    }
                } else {
                    double v = o1.val() - o2.val();
                    if (v > 1E-10) {
                        if (prev.equals(now))
                            l(line + lightGreen("SAME") + boldBlack(" = ") + lightBlue(prev + " "));
                        l(ensureLength("", maxLength) + boldBlack("RAW DIFF: ") + lightRed(v));
                    }
                }
            }

            lines.set(i, line + "-> " + now);
        }
        if (WRITE) writeLines(fileName, lines);
    }

    private static void writeLines(String fileName, ArrayList<String> lines) {
        Optional<String> content = lines.stream().map(l -> l + "\n").reduce((a, b) -> a + b);
        content.ifPresent(c -> Utils.write(fileName, c));
    }

    private static void configureCAS() {
        Binary.define("%", 2, (a, b) -> a % b);
    }

    private static String ensureLength(String s, int length) {
        StringBuilder sBuilder = new StringBuilder(s);
        while (sBuilder.length() < length) {
            sBuilder.append(" ");
        }
        s = sBuilder.toString();
        return s;
    }
}
