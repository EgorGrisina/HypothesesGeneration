package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

public class NumberProcessingRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        //PrintWriter out = new PrintWriter(System.out);
        List<InputHypothesis> result = new ArrayList<InputHypothesis>();


        for (InputHypothesis hypothesis : inputHypothesisList) {
            Map<Tree, Double> resultMap = replaceNo(hypothesis.getHTree().deepCopy(), hypothesis.getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (Double) entry.getValue()));
            }

        }

        List<InputHypothesis> withoutNumberResult = new ArrayList<InputHypothesis>();

        for (int i = 0; i < result.size(); i++) {

            Map<String, Double> resultMap = removeNumberWord2(result.get(i).getHTree().deepCopy(), result.get(i).getHConfidence());

            for (Map.Entry entry : resultMap.entrySet()) {
                withoutNumberResult.add(new InputHypothesis(getNewTree((String) entry.getKey()), (Double) entry.getValue()));
            }
        }

        withoutNumberResult = cleanHypothesisList(withoutNumberResult);

        /*for(Tree tree : inputTrees) {
            tree.pennPrint(out);
            out.flush();
        }*/

        return withoutNumberResult;
    }

    private Map<String, Double> removeNumberWord2(Tree tree, Double confidence) {
        PrintWriter out = new PrintWriter(System.out);
        List<LabeledWord> words = tree.labeledYield();

        for (int i = 0; i < words.size() - 1; i++) {
            if (words.get(i).word().equals(CoreNlpConstants.NUMBER)) {
                if (words.get(i + 1).tag().value().equals(CoreNlpConstants.NUMERAL)) {
                    words.remove(i);
                    i--;
                }
            }
        }
        String sentence = "";
        for (LabeledWord word : words) {
            sentence += word.word() + " ";
        }
        Map<String, Double> resultMap = new HashMap<String, Double>();
        resultMap.put(sentence, confidence);
        return resultMap;
    }

    private Map<Tree, Double> removeNumberWord(Tree tree, double confidence) {
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
                Map<Tree, Double> result = removeNumberWord(children, confidence);

                for (Map.Entry entry : result.entrySet()) {
                    confidence = (Double) entry.getValue();
                    tree.setChild(i, (Tree) entry.getKey());
                }

            }
        }

        Map<Tree, Double> resultMap = new HashMap<Tree, Double>();
        resultMap.put(tree, confidence);
        return resultMap;
    }

    private Map<Tree, Double> replaceNo(Tree tree, double confidence) {

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
                    }
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Map<Tree, Double> result = replaceNo(children, confidence);

                for (Map.Entry entry : result.entrySet()) {
                    confidence = (Double) entry.getValue();
                    tree.setChild(i, (Tree) entry.getKey());
                }
            }
        }

        Map<Tree, Double> resultMap = new HashMap<Tree, Double>();
        resultMap.put(tree, confidence);
        return resultMap;
    }

}
