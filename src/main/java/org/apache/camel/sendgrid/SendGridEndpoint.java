package org.apache.camel.sendgrid;

import java.net.URI;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;

public class SendGridEndpoint extends DefaultEndpoint {

    private final String uri;
    private String apiKey;

    public SendGridEndpoint(String uri) {
        this.uri = uri;
        URI uriObj = URI.create(getEndpointUri());
        this.apiKey = uriObj.getAuthority();
        if (apiKey == null || apiKey.trim().length() == 0) {
            throw new NullPointerException("apiKey is null");
        }
    }

    @Override
    public Producer createProducer() throws Exception {
        return new SendGridProducer(this);
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getApiKey() {
        return apiKey;
    }

    @Override
    public String createEndpointUri() {
        return uri;
    }
}
