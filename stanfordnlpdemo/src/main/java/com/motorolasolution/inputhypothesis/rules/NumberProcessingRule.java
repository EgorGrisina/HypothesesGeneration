package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpConstants;
import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.LabeledWord;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.trees.Tree;

public class NumberProcessingRule extends BaseHypothesisRule {

    @Override
    public List<InputHypothesis> getHypothesis(List<InputHypothesis> inputHypothesisList) {

        //PrintWriter out = new PrintWriter(System.out);
        List<InputHypothesis> result = new ArrayList<InputHypothesis>();


       /* for (int i = 0; i < inputTrees.size(); i++) {
            result.add(getNewTree(replaceNo(inputTrees.get(i).deepCopy())));
        }
*/
        List<InputHypothesis> withoutNumberResult = new ArrayList<InputHypothesis>();
        /*for (int i = 0; i < result.size(); i++) {
            withoutNumberResult.add(getNewTree(removeNumberWord2(result.get(i).deepCopy())));
        }*/

        withoutNumberResult = cleanHypothesisList(withoutNumberResult);

        /*for(Tree tree : inputTrees) {
            tree.pennPrint(out);
            out.flush();
        }*/

        return withoutNumberResult;
    }

    private String removeNumberWord2(Tree tree) {
        PrintWriter out = new PrintWriter(System.out);
        List <LabeledWord> words = tree.labeledYield();

        for (int i = 0; i < words.size()-1; i++) {
            if (words.get(i).word().equals(CoreNlpConstants.NUMBER)){
                if (words.get(i+1).tag().value().equals(CoreNlpConstants.NUMERAL)){
                    words.remove(i);
                    i--;
                }
            }
        }
        String sentence = "";
        for (LabeledWord word : words) {
            sentence+=word.word()+" ";
        }
        return sentence;
    }

    private Tree removeNumberWord(Tree tree) {
        List<Tree> childs = tree.getChildrenAsList();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.size(); i++) {

                Tree children = childs.get(i);

                    if (children.value().toLowerCase().equals(CoreNlpConstants.NUMBER)){
                        tree.removeChild(i);
                        childs.remove(i);
                        i--;
                    }
            }

        } else {

            for (int i = 0; i < childs.size(); i++) {
                Tree children = childs.get(i);
                Tree new_children = removeNumberWord(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }

    private Tree replaceNo(Tree tree) {

        Tree[] childs = tree.children();

        boolean isSimpleChilds = true;
        for (Tree children : childs) {
            if (children.depth()>0) {
                isSimpleChilds = false;
            }
        }

        if (isSimpleChilds) {

            for (int i = 0; i < childs.length; i++) {

                Tree children = childs[i];

                for (String numb : CoreNlpConstants.NUMBList){
                    if (children.value().toLowerCase().equals(numb)){
                        children.setValue(CoreNlpConstants.NUMBER);
                    }
                }
            }

        } else {

            for (int i = 0; i < childs.length; i++) {
                Tree children = childs[i];
                Tree new_children = replaceNo(children);
                tree.setChild(i, new_children);
            }
        }

        return tree;
    }

}
