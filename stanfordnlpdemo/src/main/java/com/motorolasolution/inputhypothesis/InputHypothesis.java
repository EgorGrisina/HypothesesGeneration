package com.motorolasolution.inputhypothesis;

import java.io.PrintWriter;

import edu.stanford.nlp.trees.Tree;

public class InputHypothesis implements Comparable<InputHypothesis>{

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
        hConfidence.setWordCount(tree.getLeaves().size());
        hConfidence.setTreeDeep(tree.depth());
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

    @Override
    public int compareTo(InputHypothesis o) {
        if (this.getHConfidence().getConfidence() > o.getHConfidence().getConfidence()) {
            return -1;
        }
        if (this.getHConfidence().getConfidence() < o.getHConfidence().getConfidence()) {
            return 1;
        }
        return 0;
    }
}
