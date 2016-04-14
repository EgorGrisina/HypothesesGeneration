package com.motorolasolution.inputhypothesis;

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

    public HypothesisConfidence copy(){
        return new HypothesisConfidence(wordCount, treeDeep, confidence);
    }
}
