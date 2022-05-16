package de.materna.dmn.tester.beans.util;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import de.materna.dmn.tester.beans.user.User;
import de.materna.dmn.tester.enums.Constants;

public class SendEmail {

	private User recipient;

	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public SendEmail(User recipient) {
		setRecipient(recipient);
	}

	public void sendConfirmationMail() {

		final Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.auth", Constants.EMAIL.AUTH.VALUE);
		properties.setProperty("mail.smtp.starttls.enabled", Constants.EMAIL.STARTTLS.VALUE);
		properties.setProperty("mail.smtp.host", Constants.EMAIL.HOST.VALUE);
		properties.setProperty("mail.smtp.port", Constants.EMAIL.PORT.VALUE);

		final Session session = Session.getDefaultInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Constants.EMAIL.SENDER.VALUE, Constants.EMAIL.PASSWORD.VALUE);
			}
		});

		try {
			if (!Constants.EMAIL.CONFIRMED.VALUE.equals(getRecipient().getConfirmation())) {
				final MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(Constants.EMAIL.SENDER.VALUE));
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(getRecipient().getEmail()));
				message.setSubject("Please verify your email address");
				message.setText("Click this link to confirm your email address :: "
						+ "http://localhost:8080/declab/portal/user/confirmEmail?email=" + getRecipient().getEmail()
						+ "&hash=" + getRecipient().getConfirmation());
				Transport.send(message);
			}
		} catch (final MessagingException e) {
			e.printStackTrace();
		}
	}
}