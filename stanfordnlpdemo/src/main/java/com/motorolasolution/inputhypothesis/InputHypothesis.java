package com.motorolasolution.inputhypothesis;

import edu.stanford.nlp.trees.Tree;

public class InputHypothesis {

    private Tree hTree;
    private HypothesisConfidence hConfidence;

    public InputHypothesis() {
        hTree = null;
        hConfidence = new HypothesisConfidence();
    }

    public InputHypothesis(Tree tree, double confidence) {
        hTree = tree;
        int wCount = tree.getLeaves().size();
        int tDeep = tree.depth();
        hConfidence = new HypothesisConfidence(wCount, tDeep, confidence);
    }

    public InputHypothesis(Tree tree, HypothesisConfidence hypothesisConfidence) {
        hTree = tree;
        hConfidence = hypothesisConfidence;
    }

    public Tree getHTree() {
        return hTree;
    }
    public void setHTree(Tree tree) {
        hTree = tree;
    }

    public HypothesisConfidence getHConfidence() {
        return hConfidence;
    }
    public void setHConfidence(HypothesisConfidence confidence) {
        hConfidence = confidence;
    }

}
