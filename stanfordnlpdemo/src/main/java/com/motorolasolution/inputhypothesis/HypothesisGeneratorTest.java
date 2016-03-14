package com.motorolasolution.inputhypothesis;

import com.motorolasolution.inputhypothesis.rules.AbstractHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.JJafterNounRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class HypothesisGeneratorTest {

    public static void main(String[] args) throws IOException {

        PrintWriter out = new PrintWriter(System.out);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        CoreNlpPipeline mCoreNlpPipeline = new CoreNlpPipeline();

        AbstractHypothesisRule rulesList[] = new AbstractHypothesisRule[2];
        rulesList[0] = new PunctuationRule();
        rulesList[1] = new JJafterNounRule();

        out.print("Enter something:");
        out.flush();

        String input = in.readLine();

        while (!input.equals(InputMessageType.EXIT)) {


            //out.println(input);
           // out.flush();

            //input = ((PunctuationRule)rulesList[0]).removePunctuation(input);

            List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(input);

            if (sentencesTree == null || sentencesTree.size() == 0) {
                out.println("tree is null.Enter something else: ");
                out.flush();
                input = in.readLine();
                continue;
            }

            CoreNlpOutput.printTrees(sentencesTree, out);
            out.println("");
            out.flush();

            rulesList[1].getHypothesis(sentencesTree);

            out.println("Enter something: ");
            out.flush();
            input = in.readLine();
        }
    }
}
