package jas.core.components;

import jas.core.Node;

/**
 * Created by Jiachen on 3/20/18.
 */
public class Vector extends Node {
    @Override
    public boolean equals(Node other) {
        return false;
    }

    @Override
    public Node copy() {
        return null;
    }

    /**
     * orders the expression according to lexicographic order. This saves some computational resource when trying to decide
     * whether a node within the binary tree the the canonical form of another.
     * e.g. c*b*3*a would be reordered to something like 3*a*c*b
     */
    @Override
    public void order() {
        //TODO: implement
    }

    /**
     * returns 0, if the current instance is what you are looking for, i.e. this.equals(o);
     * returns -1 if not found.
     *
     * @param o the Node instance that you are looking for.
     * @return the level at which node is found.
     */
    @Override
    public int levelOf(Node o) {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    /**
     * numNodes() of expression "(3 + 4.5) * 5.3 / 2.7" returns 7
     * numNodes() of expression "(3 + 4.5) * ln(5.3 + 4) / 2.7 / (x + 1) * x / 3" returns 18
     * numNodes() of expression "3 - 2x + 4x - 4 + 7pi" returns 15
     *
     * @return number of nodes (including both leaf nodes and non-leaf nodes)
     */
    @Override
    public int numNodes() {
        return 0;
    }

    /**
     * traverses the composite tree and evaluates every single node that represents a raw value.
     * e.g. if the Node represents the expression "(5 + 7) / 2 ^ 2", val() returns 3.
     * however, if variables exists in the expression, NaN is returned.
     * e.g. if the Node represents the expression "(5 + 7x) / 2 ^ 2", val() returns NaN.
     *
     * @return arbitrary value of the node.
     */
    @Override
    public double val() {
        return 0;
    }

    /**
     * @return level of complexity of the expression represented by an integer with 1 being
     * the most simplistic
     */
    @Override
    public int complexity() {
        return 0;
    }

    /**
     * NOTE: useful for simple simplifications.
     * Node::simplify(Node o) is optimized for complex simplifications like when taking the 10th derivative of x*cos(x)*sin(x)
     *
     * @return simplified expression
     */
    @Override
    public Node simplify() {
        return null;
    }

    /**
     * basically reversing the effects of toAdditionalOnly and toExponentialForm
     * a*b^(-1) -> a/b,
     * a*(1/3) -> a/3,
     * a+(-1)*b -> a-b
     * <p>
     * before invoking this method, the Node should already by at a stage where it is simplified,
     * converted to additional only and in exponential form.
     *
     * @return beautified version of the original
     */
    @Override
    public Node beautify() {
        return null;
    }

    /**
     * (-#)*x will be converted to (-1)*#*x, where # denotes a number
     * NOTE: does not modify self.
     *
     * @return explicit negative form of the original Node
     */
    @Override
    public Node explicitNegativeForm() {
        return null;
    }

    @Override
    public Node toAdditionOnly() {
        return null;
    }

    @Override
    public Node toExponentialForm() {
        return null;
    }

    /**
     * If this method is successfully implemented, it would be marked as a milestone.
     *
     * @param v the variable in which the first derivative is taken with respect to.
     * @return first derivative of the expression
     */
    @Override
    public Node firstDerivative(Variable v) {
        return null;
    }

    /**
     * Expand the expression; its behavior is exactly what you would expect.
     * e.g. (a+b+...)(c+d) = a*c + a*d + b*c + ...
     *
     * @return expanded expression of type Node
     */
    @Override
    public Node expand() {
        return null;
    }

    /**
     * @return string representation of the node coded with Ansi color codes.
     */
    @Override
    public String coloredString() {
        return null;
    }

    /**
     * @param o the node to be replaced
     * @param r the node to take o's place
     * @return the original node with o replaced by r.
     */
    @Override
    public Node replace(Node o, Node r) {
        return null;
    }

    @Override
    public boolean isUndefined() {
        return false;
    }

    @Override
    public double eval(double x) {
        return 0;
    }
}
