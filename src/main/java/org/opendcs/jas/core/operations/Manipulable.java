package org.opendcs.jas.core.operations;

import java.util.ArrayList;

import org.opendcs.jas.core.Node;

/**
 * Created by Jiachen on 3/17/18.
 * Manipulable
 */
public interface Manipulable {
    Node manipulate(ArrayList<Node> operands);
}
