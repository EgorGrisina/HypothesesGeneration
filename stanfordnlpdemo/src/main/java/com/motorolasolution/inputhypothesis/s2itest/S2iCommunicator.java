package com.motorolasolution.inputhypothesis.s2itest;

import com.motorolasolutions.bigdata.vip.controller.request.message.AiResponse;
import com.motorolasolutions.vip.utils.JsonMapper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class S2iCommunicator {

    static final String clientKey = "a0991cdbe2504c5192ae60b60c704d11";
    static final String subscriptionKey = "db5a659e-f840-4a4d-8fc5-fee52c99f321";
    static final String version = "20150701";

    public static AiResponse query(String phrase) {
        try {
            /*String EscapedPhrase = java.net.URLEncoder.encode(
                    phrase, "ISO-8859-1");*/
            URL url = new URL("https://api.api.ai/v1/query?"
                    + phrase
                    + "&timezone=America/Chicago&lang=EN&v="+version+"&resetContexts=true&sessionId=1234567890");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.addRequestProperty("Authorization", "Bearer "
                    + clientKey);
            connection.addRequestProperty("ocp-apim-subscription-subscriptionKey",
                    subscriptionKey);
            connection.addRequestProperty("Content-Type", "application/json");
            connection.addRequestProperty("Accept", "application/json");
            connection.connect();

            final InputStream inputStream = new BufferedInputStream(
                    connection.getInputStream());
            final String response = IOUtils.toString(inputStream,
                    Charsets.UTF_8);

            return JsonMapper.MAPPER.readValue(response, AiResponse.class);

        } catch (Exception e) {
            return null;
        }
    }

}
