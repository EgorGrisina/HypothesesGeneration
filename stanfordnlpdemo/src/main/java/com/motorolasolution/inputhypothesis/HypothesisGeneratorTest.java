package com.motorolasolution.inputhypothesis;

import com.motorolasolution.inputhypothesis.rules.BaseHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.AdverbRule;
import com.motorolasolution.inputhypothesis.rules.DatePeriodRule;
import com.motorolasolution.inputhypothesis.rules.INinsideINRule;
import com.motorolasolution.inputhypothesis.rules.INprocessingRule;
import com.motorolasolution.inputhypothesis.rules.INremovingRule;
import com.motorolasolution.inputhypothesis.rules.JJbeforeNounRule;
import com.motorolasolution.inputhypothesis.rules.NumberProcessingRule;
import com.motorolasolution.inputhypothesis.rules.NumeralRule;
import com.motorolasolution.inputhypothesis.rules.ProperNounRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;
import com.motorolasolution.inputhypothesis.rules.SProcessingRule;
import com.motorolasolution.inputhypothesis.rules.SimilarLeavesRule;
import com.motorolasolution.inputhypothesis.rules.SimilarLeavesSimpleRule;
import com.motorolasolution.inputhypothesis.s2itest.S2iCommunicator;
import com.motorolasolutions.bigdata.vip.controller.request.message.AiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class HypothesisGeneratorTest {

    public static void main(String[] args) throws IOException {

        PrintWriter out = new PrintWriter(System.out);
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        final CoreNlpPipeline mCoreNlpPipeline = new CoreNlpPipeline();

        BaseHypothesisRule.CoreNlpRulesCallback mCoreNlpRulesCallback = new BaseHypothesisRule.CoreNlpRulesCallback() {
            @Override
            public Tree getNewTree(Tree oldTree) {
                return mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(oldTree));
            }

            @Override
            public Tree getNewTree(String sentence) {
                return mCoreNlpPipeline.getTree(sentence);
            }
        };

        BaseHypothesisRule rulesList[] = new BaseHypothesisRule[9];

        rulesList[0] = new JJbeforeNounRule();
        rulesList[1] = new DatePeriodRule();
        rulesList[2] = new NumeralRule();
        rulesList[3] = new AdverbRule();
        rulesList[4] = new ProperNounRule();
        rulesList[5] = new SimilarLeavesSimpleRule();
        rulesList[6] = new INprocessingRule();
        rulesList[7] = new INinsideINRule();
        rulesList[8] = new INremovingRule();

        for (int i = 0; i < rulesList.length; i++ ){
            rulesList[i].setCoreNlpRulesCallback(mCoreNlpRulesCallback);
        }

        out.print("Enter something:");
        out.flush();

        String input = in.readLine();

        while (!input.equals(CoreNlpConstants.EXIT)) {

            List<InputHypothesis> inputHypothesises = new ArrayList<InputHypothesis>();
            List<InputHypothesis> results = new ArrayList<InputHypothesis>();

            out.println("Inpute text:");
            out.println(input);
            out.flush();

            List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(input);

            if (sentencesTree == null || sentencesTree.size() == 0) {
                out.println("tree is null.Enter something else: ");
                out.flush();
                input = in.readLine();
                continue;
            }

            for (Tree sentense : sentencesTree) {
                inputHypothesises.add(new InputHypothesis(sentense, 1.0));
            }

            out.println("");
            out.println("Input tree:");
            CoreNlpOutput.printHypothesisTrees(inputHypothesises, out);
            out.println("");
            out.flush();

            NumberProcessingRule mNumberProcessingRule = new NumberProcessingRule();
            mNumberProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
            PunctuationRule mPunctuationRule = new PunctuationRule();
            mPunctuationRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
            SProcessingRule mSProcessingRule = new SProcessingRule();
            mSProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);

            results = mNumberProcessingRule.getHypothesis(inputHypothesises);
            results = mSProcessingRule.getHypothesis(results);

            inputHypothesises = new ArrayList<InputHypothesis>();

            for (int i = 0; i < results.size(); i++) {

                InputHypothesis hyp = new InputHypothesis();
                hyp.setHTree(mCoreNlpPipeline.getTree(mPunctuationRule.removePunctuation(CoreNlpOutput.getSentenceFromTree(results.get(i).getHTree()))));
                //hyp.setHTree(mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(results.get(i).getHTree())));
                hyp.setHConfidence(results.get(i).getHConfidence());

                inputHypothesises.add(hyp);
            }
            results = new ArrayList<InputHypothesis>();

            out.println("");
            out.println("After prepare tree:");
            CoreNlpOutput.printHypothesisTrees(inputHypothesises, out);
            out.println("");
            out.flush();

            //results.addAll(sentencesTree);

            for (int i = 0; i < rulesList.length; i++ ){
                results = rulesList[i].getHypothesis(inputHypothesises);

                out.println("#"+ (i+1) +" " + rulesList[i].getRuleName()+" result:");
                for(int j = 0; j < results.size(); j++){
                    out.println(j + 1 +"."+
                            " w:"+results.get(j).getHConfidence().getWordCount()+
                            " d:"+results.get(j).getHConfidence().getTreeDeep()+
                            " c:"+results.get(j).getHConfidence().getConfidence()+
                            " : " + CoreNlpOutput.getSentenceFromTree(results.get(j).getHTree()));
                }
                out.println("");
                out.flush();
            }

            results = new ArrayList<InputHypothesis>();
            results.addAll(inputHypothesises);

            out.println("-----------All rules-----------");
            results = rulesList[0].getHypothesis(results);
            results = rulesList[1].getHypothesis(results);
            results = rulesList[2].getHypothesis(results);
            results = rulesList[3].getHypothesis(results);
            results = rulesList[4].getHypothesis(results);
            results = rulesList[5].getHypothesis(results);
            results = rulesList[6].getHypothesis(results);
            results = rulesList[7].getHypothesis(results);
            results = rulesList[8].getHypothesis(results);

            out.println("Input:\n0. " + input + "\n\nResult:");

            Collections.sort(results);

            for(int i = 0; i < results.size(); i++){
                out.println(i + 1 +"."+
                        " w:"+results.get(i).getHConfidence().getWordCount()+
                        " d:"+results.get(i).getHConfidence().getTreeDeep()+
                        " c:"+results.get(i).getHConfidence().getConfidence()+
                        " : " + CoreNlpOutput.getSentenceFromTree(results.get(i).getHTree()));
            }

            out.flush();
            out.println("\n----------------------------------------------");
            out.println("Start S2i processing");
            out.println(CoreNlpOutput.getS2iQuery(results));
            out.println("");
            AiResponse response = S2iCommunicator.query(CoreNlpOutput.getS2iQuery(results));
            if (response == null) {
                out.println("No response");
            } else {
                out.println("ResolvedQuery: "+response.getResult().getResolvedQuery());
                out.println("Action: "+response.getResult().getAction());
                out.println("");
            }

            out.flush();
            out.println("-------------------------");
            out.println("Enter something: ");
            out.flush();
            input = in.readLine();
        }
    }
}
