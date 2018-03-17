package jmc.cas;


import jmc.Function;
import jmc.cas.operations.CompositeOperation;
import jmc.cas.operations.RegisteredManipulation;
import jmc.cas.operations.UnaryOperation;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Created by Jiachen on 3/2/18.
 * Assets
 */
public interface Assets {
    String DIGITS = "0123456789.";
    String SYMBOLS = ",()+-*/^<>";
    String VARS = "abcdfghjklmnopqrstuvwxyz";
    String LETTERS = "abcdefghijklmnopqrstuvwxyz";

    static ArrayList<String> reservedNames() {
        ArrayList<String> names = new ArrayList<>();
        names.addAll(UnaryOperation.registeredOperations().stream()
                .map(Function::getName)
                .collect(Collectors.toCollection(ArrayList::new)));
        names.addAll(CompositeOperation.registeredManipulations().stream()
                .map(RegisteredManipulation::getName)
                .collect(Collectors.toCollection(ArrayList::new)));
        return names;
    }
}
