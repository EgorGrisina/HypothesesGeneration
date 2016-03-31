package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolution.inputhypothesis.InputHypothesis;

import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class BaseHypothesisRule {

    protected CoreNlpRulesCallback mCoreNlpRulesCallback = null;

    public interface CoreNlpRulesCallback {
        Tree getNewTree(Tree oldTree);
        Tree getNewTree(String sentence);
    }

    public void setCoreNlpRulesCallback(CoreNlpRulesCallback callback){
        mCoreNlpRulesCallback = callback;
    }

    public String getRuleName(){
        return getClass().getSimpleName();
    }

    public List<InputHypothesis> getHypothesis(List<InputHypothesis> hypothesises){
        return hypothesises;
    }

    private Tree removeWordFromTree(Tree tree, String removeWord) {

        Tree[] childs = tree.children();
        for (int i = 0; i < childs.length; i++) {
            Tree children = childs[i];
            if (children.depth() > 1) {
                tree.setChild(i, removeWordFromTree(children, removeWord));
            } else {
                String word = children.yieldWords().get(0).word();
                if (word.equals(removeWord)) {
                    tree.removeChild(i);
                    return tree;
                }
            }
        }
        return tree;
    }

    protected List<Tree> cleanTreeList(List<Tree> treeList){
        for (int i = 0; i < treeList.size(); i++) {
            String treeString = CoreNlpOutput.getSentenceFromTree(treeList.get(i));
            for(int j = i+1; j < treeList.size(); j++) {
                if (treeString.equals(CoreNlpOutput.getSentenceFromTree(treeList.get(j)))) {
                    treeList.remove(j);
                    j--;
                }
            }
        }
        return treeList;
    }

    protected List<InputHypothesis> cleanHypothesisList(List<InputHypothesis> hypothesisList){
        for (int i = 0; i < hypothesisList.size(); i++) {
            String treeString = CoreNlpOutput.getSentenceFromTree(hypothesisList.get(i).getHTree());
            for(int j = i+1; j < hypothesisList.size(); j++) {
                if (treeString.equals(CoreNlpOutput.getSentenceFromTree(hypothesisList.get(j).getHTree()))) {
                    hypothesisList.remove(j);
                    j--;
                }
            }
        }
        return hypothesisList;
    }

    protected Tree getNewTree(Tree oldTree) {

        if (mCoreNlpRulesCallback != null) {
            return mCoreNlpRulesCallback.getNewTree(oldTree);
        } else {
            return oldTree;
        }
    }

    protected Tree getNewTree(String sentence) {

        if (mCoreNlpRulesCallback != null) {
            return mCoreNlpRulesCallback.getNewTree(sentence);
        } else {
            return null;
        }
    }
}
