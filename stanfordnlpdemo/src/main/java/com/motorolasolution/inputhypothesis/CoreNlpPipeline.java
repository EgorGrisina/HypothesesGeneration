package com.motorolasolution.inputhypothesis;


import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

public class CoreNlpPipeline {

    StanfordCoreNLP mStanfordCoreNLP;

    public CoreNlpPipeline(){
        mStanfordCoreNLP = new StanfordCoreNLP();
    }

    public Annotation getAnnotation(String string){
        Annotation annotation = new Annotation(string);
        mStanfordCoreNLP.annotate(annotation);
        return annotation;
    }

    public List<CoreMap> getSentences(String string){
        Annotation annotation = getAnnotation(string);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        return sentences;
    }

    public Tree getTree(String string){

        List<CoreMap> sentences = getSentences(string);
        if (sentences!= null && !sentences.isEmpty()) {
            return getTree(sentences.get(0));
        } else {
            return null;
        }
    }

    public Tree getTree(CoreMap sentence){
        Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
        return tree;
    }

    public List<Tree> getTrees(String string){

        List<CoreMap> sentences = getSentences(string);
        List<Tree> trees = new ArrayList<Tree>();
        if (sentences!= null && !sentences.isEmpty()) {
            for(CoreMap sent : sentences) {
                trees.add(getTree(sent));
            }
        }
        return trees;
    }

}
