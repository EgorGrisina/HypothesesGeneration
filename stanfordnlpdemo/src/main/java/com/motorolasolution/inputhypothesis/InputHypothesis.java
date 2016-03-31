package com.motorolasolution.inputhypothesis;

import edu.stanford.nlp.trees.Tree;

public class InputHypothesis {

    private Tree hTree;
    private double hConfidence;

    public InputHypothesis() {
        hTree = null;
        hConfidence = 0.0;
    }

    public InputHypothesis(Tree tree, double confidence) {
        hTree = tree;
        hConfidence = confidence;
    }

    public Tree getHTree() {
        return hTree;
    }
    public void setHTree(Tree tree) {
        hTree = tree;
    }

    public double getHConfidence() {
        return hConfidence;
    }
    public void setHConfidence(double confidence) {
        hConfidence = confidence;
    }

}
