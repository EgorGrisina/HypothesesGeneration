package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.HypothesisConfidence;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.trees.Tree;

public class NumberProcessingRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        //PrintWriter out = new PrintWriter(System.out);
        List<InputHypothesis> result = new ArrayList<InputHypothesis>();


        for (InputHypothesis hypothesis : inputHypothesisList) {
            Map<Tree, HypothesisConfidence> resultMap = replaceNo(hypothesis.getHTree().deepCopy(), hypothesis.getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }

        }

        List<InputHypothesis> withoutNumberResult = new ArrayList<InputHypothesis>();

        for (int i = 0; i < result.size(); i++) {

            Map<String, HypothesisConfidence> resultMap = removeNumberWord2(result.get(i).getHTree().deepCopy(), result.get(i).getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                withoutNumberResult.add(new InputHypothesis(getNewTree((String) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }
        }

        withoutNumberResult = cleanHypothesisList(withoutNumberResult);

        /*for(Tree tree : inputTrees) {
            tree.pennPrint(out);
            out.flush();
        }*/

        return withoutNumberResult;
    }

    private Map<String, HypothesisConfidence> removeNumberWord2(Tree tree, HypothesisConfidence confidence) {
        PrintWriter out = new PrintWriter(System.out);
        List<LabeledWord> words = tree.labeledYield();
        int count = 0;

        for (int i = 0; i < words.size() - 1; i++) {
            if (words.get(i).word().equals(CoreNlpConstants.NUMBER)) {
                if (words.get(i + 1).tag().value().equals(CoreNlpConstants.CD)) {
                    words.remove(i);
                    i--;
                    count++;
                }
            }
        }
        String sentence = "";
        for (LabeledWord word : words) {
            sentence += word.word() + " ";
        }

        double confVal = confidence.getConfidence();
        confVal -= ((double)count/(double)confidence.getWordCount()) * CoreNlpConstants.NUMBERc;
        confidence.setConfidence(confVal);

        Map<String, HypothesisConfidence> resultMap = new HashMap<String, HypothesisConfidence>();
        resultMap.put(sentence, confidence);
        return resultMap;
    }

    private Map<Tree, HypothesisConfidence> removeNumberWord(Tree tree, HypothesisConfidence confidence) {
        List<Tree> childs = tree.getChildrenAsList();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth() > 0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);

                if (children.value().toLowerCase().equals(CoreNlpConstants.NUMBER)) {
                    tree.removeChild(i);
                    childs.remove(i);
                    i--;
                }
            }

        } else {

            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);
                Map<Tree, HypothesisConfidence> result = removeNumberWord(children, confidence);

                for (Map.Entry entry : result.entrySet()) {
                    confidence = (HypothesisConfidence) entry.getValue();
                    tree.setChild(i, (Tree) entry.getKey());
                }

            }
        }

        Map<Tree, HypothesisConfidence> resultMap = new HashMap<Tree, HypothesisConfidence>();
        resultMap.put(tree, confidence);
        return resultMap;
    }

    private Map<Tree, HypothesisConfidence> replaceNo(Tree tree, HypothesisConfidence confidence) {

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth() > 0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];

                for (String numb : CoreNlpConstants.NUMBList) {
                    if (children.value().toLowerCase().equals(numb)) {
                        children.setValue(CoreNlpConstants.NUMBER);
                        double confVal = confidence.getConfidence();
                        confVal -= (1.0/(double)confidence.getWordCount()) * CoreNlpConstants.ReplaceNOc;
                        confidence.setConfidence(confVal);
                    }
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Map<Tree, HypothesisConfidence> result = replaceNo(children, confidence);

                for (Map.Entry entry : result.entrySet()) {
                    confidence = (HypothesisConfidence) entry.getValue();
                    tree.setChild(i, (Tree) entry.getKey());
                }
            }
        }

        Map<Tree, HypothesisConfidence> resultMap = new HashMap<Tree, HypothesisConfidence>();
        resultMap.put(tree, confidence);
        return resultMap;
    }

}
