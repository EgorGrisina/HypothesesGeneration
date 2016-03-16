package com.motorolasolution.inputhypothesis.rules;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class PunctuationRule extends AbstractHypothesisRule {

    public String removePunctuation(String input){
        input = input.replaceAll("[,.;!?(){}\\[\\]<>%]", "");
        return input;
    }

    @Override
    protected Tree getNewTree(Tree oldTree) {
        return null;
    }
}
