package com.motorolasolution.inputhypothesis.test;

import com.motorolasolution.inputhypothesis.CoreNlpOutput;
import com.motorolasolution.inputhypothesis.CoreNlpPipeline;
import com.motorolasolution.inputhypothesis.InputHypothesis;
import com.motorolasolution.inputhypothesis.rules.AdverbRule;
import com.motorolasolution.inputhypothesis.rules.BaseHypothesisRule;
import com.motorolasolution.inputhypothesis.rules.DatePeriodRule;
import com.motorolasolution.inputhypothesis.rules.INinsideINRule;
import com.motorolasolution.inputhypothesis.rules.INprocessingRule;
import com.motorolasolution.inputhypothesis.rules.JJbeforeNounRule;
import com.motorolasolution.inputhypothesis.rules.NumberProcessingRule;
import com.motorolasolution.inputhypothesis.rules.NumeralRule;
import com.motorolasolution.inputhypothesis.rules.ProperNounRule;
import com.motorolasolution.inputhypothesis.rules.PunctuationRule;
import com.motorolasolution.inputhypothesis.rules.SProcessingRule;
import com.motorolasolution.inputhypothesis.rules.SimilarLeavesRule;
import com.motorolasolution.inputhypothesis.s2itest.S2iCommunicator;
import com.motorolasolutions.bigdata.vip.controller.request.message.AiResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class HypGenerator {

    final CoreNlpPipeline mCoreNlpPipeline;
    BaseHypothesisRule.CoreNlpRulesCallback mCoreNlpRulesCallback;
    BaseHypothesisRule rulesList[] = new BaseHypothesisRule[8];
    NumberProcessingRule mNumberProcessingRule;
    PunctuationRule mPunctuationRule;
    SProcessingRule mSProcessingRule;

    public HypGenerator() {

        mCoreNlpPipeline = new CoreNlpPipeline();
        mCoreNlpRulesCallback = new BaseHypothesisRule.CoreNlpRulesCallback() {
            @Override
            public Tree getNewTree(Tree oldTree) {
                return mCoreNlpPipeline.getTree(CoreNlpOutput.getSentenceFromTree(oldTree));
            }

            @Override
            public Tree getNewTree(String sentence) {
                return mCoreNlpPipeline.getTree(sentence);
            }
        };

        rulesList[0] = new JJbeforeNounRule();
        rulesList[1] = new DatePeriodRule();
        rulesList[2] = new NumeralRule();
        rulesList[3] = new AdverbRule();
        rulesList[4] = new ProperNounRule();
        rulesList[5] = new SimilarLeavesRule();
        rulesList[6] = new INprocessingRule();
        rulesList[7] = new INinsideINRule();

        for (int i = 0; i < rulesList.length; i++ ){
            rulesList[i].setCoreNlpRulesCallback(mCoreNlpRulesCallback);
        }

        mNumberProcessingRule = new NumberProcessingRule();
        mNumberProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
        mPunctuationRule = new PunctuationRule();
        mPunctuationRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);
        mSProcessingRule = new SProcessingRule();
        mSProcessingRule.setCoreNlpRulesCallback(mCoreNlpRulesCallback);

    }

    public List<InputHypothesis> generateHypothesis(String inputSentence) {

        List<InputHypothesis> inputHypothesises = new ArrayList<InputHypothesis>();
        List<InputHypothesis> results = new ArrayList<InputHypothesis>();

        List<Tree> sentencesTree = mCoreNlpPipeline.getTrees(inputSentence);

        for (Tree sentence : sentencesTree) {
            inputHypothesises.add(new InputHypothesis(sentence, 1.0));
        }

        inputHypothesises = mNumberProcessingRule.getHypothesis(inputHypothesises);
        inputHypothesises = mSProcessingRule.getHypothesis(inputHypothesises);

        for (int i = 0; i < inputHypothesises.size(); i++) {

            InputHypothesis hyp = new InputHypothesis();
            hyp.setHTree(mCoreNlpPipeline.getTree(mPunctuationRule.removePunctuation(CoreNlpOutput.getSentenceFromTree(inputHypothesises.get(i).getHTree()))));
            hyp.setHConfidence(inputHypothesises.get(i).getHConfidence());

            results.add(hyp);
        }

        results = rulesList[0].getHypothesis(results);
        results = rulesList[1].getHypothesis(results);
        results = rulesList[2].getHypothesis(results);
        results = rulesList[3].getHypothesis(results);
        results = rulesList[4].getHypothesis(results);
        results = rulesList[5].getHypothesis(results);
        results = rulesList[6].getHypothesis(results);
        results = rulesList[7].getHypothesis(results);

        Collections.sort(results);

        return results;
    }

    public AiResponse getS2iResponse(List<InputHypothesis> hypothesises) {

        AiResponse response = S2iCommunicator.query(CoreNlpOutput.getS2iQuery(hypothesises));
        return response;

    }

}
