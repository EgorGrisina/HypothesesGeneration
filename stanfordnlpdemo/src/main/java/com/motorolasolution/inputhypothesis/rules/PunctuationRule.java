package com.motorolasolution.inputhypothesis.rules;

public class PunctuationRule extends BaseHypothesisRule {

    public String removePunctuation(String input){
        input = input.replaceAll("[\",.;!:?(){}\\[\\]<>%]", " ");

        return input;
    }
}
