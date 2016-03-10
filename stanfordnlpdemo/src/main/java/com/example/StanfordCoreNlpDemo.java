package com.example;

import java.io.*;
import java.util.*;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations;
import edu.stanford.nlp.semgraph.SemanticGraphEdge;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.util.*;

/**
 * This class demonstrates building and using a Stanford CoreNLP pipeline.
 */
public class StanfordCoreNlpDemo {

    /**
     * Usage: java -cp "*" StanfordCoreNlpDemo [inputFile [outputTextFile [outputXmlFile]]]
     */
    public static void main(String[] args) throws IOException {
        // set up optional output files
        PrintWriter out;
        StanfordCoreNLP pipeline = new StanfordCoreNLP();
        Annotation annotation;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        out = new PrintWriter(System.out);
        /*if (args.length > 1) {
            out = new PrintWriter(args[1]);
        } else {

        }
        PrintWriter xmlOut = null;
        if (args.length > 2) {
            xmlOut = new PrintWriter(args[2]);
        }*/

        System.out.print("Enter something:");

        String input = br.readLine();

        while (!input.equals("exit")) {
            // Create a CoreNLP pipeline. This line just builds the default pipeline.
            // In comments we show how you can build a particular pipeline
            // Properties props = new Properties();
            // props.put("annotators", "tokenize, ssplit, pos, lemma, ner, depparse");
            // props.put("ner.model", "edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
            // props.put("ner.applyNumericClassifiers", "false");
            // StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

            if (args.length > 0) {
                annotation = new Annotation(IOUtils.slurpFileNoExceptions(args[0]));
            } else if (input != null) {
                annotation = new Annotation(input);
            } else {
                annotation = new Annotation("Show me how to get to witness Bernie Dickens's house");
            }

            // run all the selected Annotators on this text
            pipeline.annotate(annotation);

            // print the results to file(s)
        /*pipeline.prettyPrint(annotation, out);
        if (xmlOut != null) {
            pipeline.xmlPrint(annotation, xmlOut);
        }*/

            // Access the Annotation in code
            // The toString() method on an Annotation just prints the text of the Annotation
            // But you can see what is in it with other methods like toShorterString()
    /*
    out.println();
    out.println("The top level annotation");
    out.println(annotation.toShorterString());*/

            // An Annotation is a Map and you can get and use the various analyses individually.
            // For instance, this gets the parse tree of the first sentence in the text.

            List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

            if (sentences != null && !sentences.isEmpty()) {

                CoreMap sentence = sentences.get(0);
      /*Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
      tree.pennPrint(out);*/
      /*  *//*printSentenceFromTree(tree, out);
        removeWordFromTree( tree, "with", out);
        printSentenceFromTree(tree, out);*//*



      out.println(tree.pennString());
      tree.firstChild().removeChild(0);
      printTree(tree, out);*/

      /*SemanticGraph dependencies1 = sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class);
      String dep_type = "BasicDependenciesAnnotation";
      out.println(dep_type+" ===>>");
      out.println("Sentence: " + sentence.toString());
      out.println("DEPENDENCIES: "+dependencies1.toList());
      out.println("DEPENDENCIES SIZE: "+dependencies1.size());
      List<SemanticGraphEdge> edge_set1 = dependencies1.edgeListSorted();
      int j=0;
      for(SemanticGraphEdge edge : edge_set1)
      {
        j++;
        System.out.println("------EDGE DEPENDENCY: "+j);
        Iterator<SemanticGraphEdge> it = edge_set1.iterator();
        IndexedWord dep = edge.getDependent();
        String dependent = dep.word();
        int dependent_index = dep.index();
        IndexedWord gov = edge.getGovernor();
        String governor = gov.word();
        int governor_index = gov.index();
        GrammaticalRelation relation = edge.getRelation();
        System.out.println("No:"+j+" Relation: "+relation.toString()+" Dependent ID: "+dep.index()+" Dependent: "+dependent.toString()+" Governor ID: "+gov.index()+" Governor: "+governor.toString());

      }/*//**/

                out.println();
                out.println("The first sentence is:");
                out.println(sentence.toShorterString());
                out.println();
                out.println("The first sentence tokens are:");
                for (CoreMap token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                    out.println(token.toShorterString());
                }
                Tree tree = sentence.get(TreeCoreAnnotations.TreeAnnotation.class);
                out.println();
                out.println("The first sentence parse tree is:");
                tree.pennPrint(out);
                out.flush();

            /*Tree[] children = tree.children();
            out.println();
            out.println("The first sentence basic dependencies are:");
            out.println(sentence.get(SemanticGraphCoreAnnotations.BasicDependenciesAnnotation.class).toString(SemanticGraph.OutputFormat.LIST));
            out.println("The first sentence collapsed, CC-processed dependencies are:");
            SemanticGraph graph = sentence.get(SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation.class);
            out.println(graph.toString(SemanticGraph.OutputFormat.LIST));

            // Access coreference. In the coreference link graph,
            // each chain stores a set of mentions that co-refer with each other,
            // along with a method for getting the most representative mention.
            // Both sentence and token offsets start at 1!
            out.println("Coreference information");
            Map<Integer, CorefChain> corefChains =
                    annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
            if (corefChains == null) {
                return;
            }
            for (Map.Entry<Integer, CorefChain> entry : corefChains.entrySet()) {
                out.println("Chain " + entry.getKey() + " ");
                for (CorefChain.CorefMention m : entry.getValue().getMentionsInTextualOrder()) {
                    // We need to subtract one since the indices count from 1 but the Lists start from 0
                    List<CoreLabel> tokens = sentences.get(m.sentNum - 1).get(CoreAnnotations.TokensAnnotation.class);
                    // We subtract two for end: one for 0-based indexing, and one because we want last token of mention not one following.
                    out.println("  " + m + ", i.e., 0-based character offsets [" + tokens.get(m.startIndex - 1).beginPosition() +
                            ", " + tokens.get(m.endIndex - 2).endPosition() + ")");
                }
            }*/
            }
            out.flush();
            System.out.println();
            System.out.print("Enter something:");
            input = br.readLine();
        }
        IOUtils.closeIgnoringExceptions(out);
        //IOUtils.closeIgnoringExceptions(xmlOut);
    }

    static Tree removeWordFromTree(Tree tree, String removeWord, PrintWriter out) {

        //printSentenceFromTree(tree, out);
        Tree[] childs = tree.children();
        for (int i = 0; i < childs.length; i++) {
            Tree children = childs[i];
            if (children.yieldWords().size() > 1) {
                tree.setChild(i, removeWordFromTree(children, removeWord, out));
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

    static void printSentenceFromTree(Tree tree, PrintWriter out) {
        List<CoreLabel> list = tree.taggedLabeledYield();

        out.println("");
        for (CoreLabel label : list) {
            out.print(label.word() + " ");
        }

        out.println("");
        out.flush();
/*
        List<Tree> leaves = tree.getLeaves(); //leaves correspond to the tokens
        for (Tree leaf : leaves){
            List<Word> words = leaf.yieldWords();
            for (Word word: words)
                out.print(word.word()+" ");
        }
        out.println("");*/
    }

    static void printTree(Tree tree, PrintWriter out) {
        if (tree != null) {
            out.println("Leaves count: " + tree.getLeaves().size());
            tree.pennPrint(out);
            out.println(" ");
            out.flush();
            for (Tree child : tree.children()) {
                printTree(child, out);
            }
        }
    }

}
