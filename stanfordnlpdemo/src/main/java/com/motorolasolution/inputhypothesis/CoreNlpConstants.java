package com.motorolasolution.inputhypothesis;


import javafx.geometry.Pos;

public class CoreNlpConstants {

    public final static String EXIT = "exit";

    public final static String[] JJlist = {"JJ", "JJR", "JJS", "PRP$"};
    public final static String[] NNlist = {"NN", "NNS", "NNP", "NNPS", "PRP"};

    public final static String[] NUMBList = {"numb", "no.", "num", "n", "#"};
    public final static String NUMBER = "number";

    public final static String IN = "IN";
    public final static String NP = "NP";

    public final static String NN = "NN";
    public final static String NNS = "NNS";
    public final static String NNP = "NNP";
    public final static String NNPS = "NNPS";
    public final static String JJ = "JJ";
    public final static String JJS = "JJS";
    public final static String JJR = "JJR";
    public final static String RB = "RB";
    public final static String RBS = "RBS";
    public final static String RBR = "RBR";
    public final static String CD = "CD";
    public final static String PRP$ = "PRP$";
    public final static String POS = "POS";

    public final static String[] RBListNoclear = {"RBR", "RBS"};
    public final static String[] NNPList = {"NNP", "NNPS"};

    // Coefficients for POS
    public final static double NNc = 1.0;
    public final static double NNSc = 1.0;
    public final static double NNPc = 0.9;
    public final static double NNPSc = 0.9;
    public final static double JJc = 0.8;
    public final static double JJSc = 0.8;
    public final static double JJRc = 0.8;
    public final static double RBc = 0.7;
    public final static double RBSc = 0.6;
    public final static double RBRc = 0.6;
    public final static double CDc = 0.5;
    public final static double PRP$c = 0.2;
    public final static double POSc = 0.1;
    public final static double NUMBERc = 0.1;

    public final static double DEFAULTc = 0.15;

    public static double getPOScoefficient(String POSstring) {
        if (POSstring.equals(NN)) return NNc;
        if (POSstring.equals(NNS)) return NNSc;
        if (POSstring.equals(NNP)) return NNPc;
        if (POSstring.equals(NNPS)) return NNPSc;
        if (POSstring.equals(JJ)) return JJc;
        if (POSstring.equals(JJS)) return JJSc;
        if (POSstring.equals(JJR)) return JJRc;
        if (POSstring.equals(RB)) return RBc;
        if (POSstring.equals(RBS)) return RBSc;
        if (POSstring.equals(RBR)) return RBRc;
        if (POSstring.equals(CD)) return CDc;
        if (POSstring.equals(PRP$)) return PRP$c;
        if (POSstring.equals(POS)) return POSc;
        if (POSstring.equals(NUMBER)) return NUMBERc;

        System.out.println("\n !-------!\nNo coefficient for :" + POSstring+"\n!---------!");
        return DEFAULTc;
    }


    // Coefficients for Rules
    public final static double JJbeforeNNc = 1.0;
    public final static double JJbeforeJJc = 0.7;
    public final static double ReplaceNOc = 0.01;
    public final static double DatePeriodc = 0.4;

    public final static double NumeralNoINc = 0.7;
    public final static double NumeralInINc = 1.0;

    public final static double SimilarLeavesMaxDiff = 0.25;

}
