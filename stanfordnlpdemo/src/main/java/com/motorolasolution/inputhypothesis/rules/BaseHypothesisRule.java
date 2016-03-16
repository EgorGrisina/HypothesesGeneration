package com.motorolasolution.inputhypothesis.rules;

import com.motorolasolution.inputhypothesis.CoreNlpOutput;

import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class BaseHypothesisRule {

    protected CoreNlpRulesCallback mCoreNlpRulesCallback = null;

    public interface CoreNlpRulesCallback {
        Tree getNewTree(Tree oldTree);
    }

    public void setCoreNlpRulesCallback(CoreNlpRulesCallback callback){
        mCoreNlpRulesCallback = callback;
    }

    public String getRuleName(){
        return getClass().getSimpleName();
    }

    public List<Tree> getHypothesis(List<Tree> inputTrees){
        return inputTrees;
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
        for (int i = 0; i<treeList.size(); i++) {
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

    protected Tree getNewTree(Tree oldTree) {

        if (mCoreNlpRulesCallback != null) {
            return mCoreNlpRulesCallback.getNewTree(oldTree);
        } else {
            return oldTree;
        }
    }
}
