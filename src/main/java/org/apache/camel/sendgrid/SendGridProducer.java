package org.apache.camel.sendgrid;

import org.apache.camel.Attachment;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

public class SendGridProducer extends DefaultProducer {

	private SendGridEndpoint sendGridEndpoint;
	private SendGrid sendGrid;

	public SendGridProducer(SendGridEndpoint endpoint) {
		super(endpoint);
		this.sendGridEndpoint = endpoint;
	}

	@Override
	public void doStart() throws Exception {

		String apiKey = sendGridEndpoint.getApiKey();
		if (apiKey == null || apiKey.isEmpty()) {
			throw new IllegalArgumentException("apiKey is null or empty");
		}
		this.sendGrid = new SendGrid(apiKey);

		super.doStart();
	}

	@Override
	public void process(Exchange exchange) throws Exception {

		String to = exchange.getIn().getHeader("to", String.class);
		String from = exchange.getIn().getHeader("from", String.class);
		String subject = exchange.getIn().getHeader("subject", String.class);
		String sender = exchange.getIn().getHeader("sender", String.class);

		Email emailTo = new Email(to);
		Email emailFrom = sender != null ? new Email(from, sender) : new Email(from);

		String body = exchange.getIn().getBody(String.class);

		Content content = new Content("text/html", body);

		Mail mail = new Mail(emailFrom, subject, emailTo, content);

		for (String id : exchange.getIn().getAttachments().keySet()) {
			Attachment attachment = exchange.getIn().getAttachmentObject(id);

			Attachments attachments = new Attachments();
			attachments.setFilename(id);
			attachments.setType(attachment.getDataHandler().getContentType());
			byte[] bytes = IOUtils.toByteArray(attachment.getDataHandler().getInputStream());
			attachments.setContent(Base64.encodeBase64String(bytes));

			mail.addAttachments(attachments);
		}

		Request request = new Request();
		request.setMethod(Method.POST);
		// TODO make this configurable
		request.setEndpoint("mail/send");
		request.setBody(mail.build());

		Response response = sendGrid.api(request);
		if (response.getStatusCode() != 202) {
			throw new SendGridResponseException(response);
		}
	}
}
