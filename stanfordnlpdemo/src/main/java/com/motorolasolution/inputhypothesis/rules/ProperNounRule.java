package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.HypothesisConfidence;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.nlp.trees.Tree;

public class ProperNounRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        List<InputHypothesis> result = new ArrayList<InputHypothesis>();
        result.addAll(inputHypothesisList);

        for (InputHypothesis hypothesis : inputHypothesisList) {
            Map<Tree, HypothesisConfidence> resultMap = removeNNP(hypothesis.getHTree().deepCopy(), hypothesis.getHConfidence().copy());

            for (Map.Entry entry : resultMap.entrySet()) {
                result.add(new InputHypothesis(getNewTree((Tree) entry.getKey()), (HypothesisConfidence) entry.getValue()));
            }

        }

        result = cleanHypothesisList(result);

        /*PrintWriter out = new PrintWriter(System.out);
        for (Tree tree : result) {
            out.println(" ");
            tree.pennPrint(out);
            out.flush();
        }*/
        return result;
    }

    private Map<Tree, HypothesisConfidence> removeNNP(Tree tree, HypothesisConfidence confidence) {

        List<Tree> childs = tree.getChildrenAsList();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>1) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);

                for (String nnp : CoreNlpConstants.NNPList) {
                    if (children.label().value().equals(nnp)) {
                        tree.removeChild(i);
                        childs.remove(i);
                        i--;
                        /*WARNING: there are no updating of depth of the tree! only word count*/
                        confidence.updateConfidence(1, children.label().value());
                        confidence.setWordCount(confidence.getWordCount()-1);
                    }
                }
            }

        } else {
            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);
                Map<Tree, HypothesisConfidence> resultMap= removeNNP(children, confidence);

                for (Map.Entry entry : resultMap.entrySet()) {
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
