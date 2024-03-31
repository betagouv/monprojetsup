package fr.gouv.monprojetsup.tools;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class Sanitizer {

    private static PolicyFactory policyTopicBuilder = new HtmlPolicyBuilder().toFactory();

    private static int MAX_INPUT_SIZE = 512;

    public static String sanitize(String topic) {
        if(topic == null) return null;
        if(topic.length() > MAX_INPUT_SIZE) topic = topic.substring(0, MAX_INPUT_SIZE);
        return policyTopicBuilder.sanitize(topic);
    }
}
