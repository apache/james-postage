package org.apache.james.postage.mail;

import org.apache.james.postage.configuration.MailSender;
import org.apache.james.postage.result.MailProcessingRecord;

import javax.mail.Message;
import javax.mail.Session;

/**
 * creates one email for the requested sender profile
 */
public interface MailFactory {

    public Message createMail(Session mailSession, MailSender mailSender, MailProcessingRecord mailProcessingRecord);

}
