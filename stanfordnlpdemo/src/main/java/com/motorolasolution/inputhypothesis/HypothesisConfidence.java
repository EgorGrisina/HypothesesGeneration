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

    public void updateConfidence(int rwc, double POSc, double RuleCoeff) {
        double confVal = getConfidence();
        confVal -= confVal
                *((double)(rwc)/(double)getWordCount())
                * POSc
                * RuleCoeff;

        if (confVal < 0 ) {
            confVal = 0.0;
        }
        setConfidence(confVal);
    }

    public void updateConfidence(int rwc, String POSname, double RuleCoeff) {
        updateConfidence(rwc, CoreNlpConstants.getPOScoefficient(POSname), RuleCoeff);
    }

    public void updateConfidence(int rwc, double RuleCoeff) {
        updateConfidence(rwc, 1.0, RuleCoeff);
    }

    public void updateConfidence(int rwc, String POSname) {
        updateConfidence(rwc, CoreNlpConstants.getPOScoefficient(POSname), 1.0);
    }
}
