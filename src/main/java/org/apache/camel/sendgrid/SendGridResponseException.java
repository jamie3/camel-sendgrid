package org.apache.camel.sendgrid;

import com.sendgrid.Response;

public class SendGridResponseException extends Exception {

	public SendGridResponseException(Response response) {
		super("SendGrid status code: " + response.getStatusCode());
	}
}
