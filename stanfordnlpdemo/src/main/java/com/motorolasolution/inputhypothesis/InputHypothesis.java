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
        hConfidence = new HypothesisConfidence(hypothesisConfidence.getWordCount(),
                hypothesisConfidence.getTreeDeep(), hypothesisConfidence.getConfidence());
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

    public class HypothesisConfidence {
        private int wordCount;
        private int treeDeep;
        private double confidence;

        public HypothesisConfidence(){
            wordCount = 0;
            treeDeep = 0;
            confidence = 0.0;
        }

        public HypothesisConfidence(int wCont, int tDeep, double conf){
            wordCount = wCont;
            treeDeep = tDeep;
            confidence = conf;
        }

        public int getWordCount() {
            return wordCount;
        }

        public void setWordCount(int wordCount) {
            this.wordCount = wordCount;
        }

        public int getTreeDeep() {
            return treeDeep;
        }

        public void setTreeDeep(int treeDeep) {
            this.treeDeep = treeDeep;
        }

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }
    }

}
