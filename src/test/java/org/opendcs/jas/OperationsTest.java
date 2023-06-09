package org.opendcs.jas;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.opendcs.jas.core.Compiler;
import org.opendcs.jas.core.JASException;
import org.opendcs.jas.core.Node;
import org.opendcs.jas.core.operations.Binary;
import org.opendcs.jas.utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.opendcs.jas.utils.ColorFormatter.*;


/**
 * Created by Jiachen on 3/10/18.
 * Automatic Test
 */
@SuppressWarnings("unused")
public class OperationsTest {
    public static boolean WRITE = false;

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/additional/bin_ops.txt",
        "/additional/irr_num.txt",
        "/additional/u_ops.txt",
        "/additional/u_der.txt"
    })
    public void test_toAdditionOnly(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.toAdditionOnly().toString());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/beautify/bin_ops.txt",
        "/beautify/irr_num.txt",
        "/beautify/u_ops.txt",
        "/beautify/u_der.txt"
    })
    public void test_beautify(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.simplify().beautify().toString());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/complexity/bin_ops.txt",
        "/complexity/irr_num.txt",
        "/complexity/u_ops.txt",
        "/complexity/u_der.txt"
    })
    public void test_complexity(String input, Integer complexity) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(complexity,inputNode.complexity());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/nodes/bin_ops.txt",
        "/nodes/irr_num.txt",
        "/nodes/u_ops.txt",
        "/nodes/u_der.txt"
    })
    public void test_numberOfNodes(String input, Integer numberOfNodes) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(numberOfNodes,inputNode.numNodes());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/expansion/bin_ops.txt",
        "/expansion/irr_num.txt",
        "/expansion/u_ops.txt",
        "/expansion/u_der.txt"
    })
    public void test_expansion(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.expand().simplify().toString());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/exponential/bin_ops.txt",
        "/exponential/irr_num.txt",
        "/exponential/u_ops.txt",
        "/exponential/u_der.txt"
    })
    public void test_toExponential(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.toExponentialForm().toString());
    }

    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/simplest/bin_ops.txt",
        "/simplest/irr_num.txt",
        "/simplest/u_ops.txt",
        "/simplest/u_der.txt"
    })
    public void test_simplest(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.simplest().toString());
    }
    
    @ParameterizedTest
    @CsvFileSource(delimiterString = "->",resources = {
        "/simplification/bin_ops.txt",
        "/simplification/irr_num.txt",
        "/simplification/u_ops.txt"
    })
    public void test_simplify(String input, String output) throws Exception {
        Node inputNode = Compiler.compile(input);
        assertEquals(output.trim(),inputNode.simplify().toString());
    }

    @BeforeAll
    public static void configureCAS() {
        Binary.define("%", 2, (a, b) -> a % b);
    }
}
