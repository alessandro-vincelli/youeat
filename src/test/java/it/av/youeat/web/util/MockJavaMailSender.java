/**
 * 
 */
package it.av.youeat.web.util;

import java.io.InputStream;

import javax.mail.internet.MimeMessage;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

/**
 * @author alessandro
 *
 */
public class MockJavaMailSender implements JavaMailSender {

    /* (non-Javadoc)
     * @see org.springframework.mail.MailSender#send(org.springframework.mail.SimpleMailMessage)
     */
    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.springframework.mail.MailSender#send(org.springframework.mail.SimpleMailMessage[])
     */
    @Override
    public void send(SimpleMailMessage[] simpleMessages) throws MailException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#createMimeMessage()
     */
    @Override
    public MimeMessage createMimeMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#createMimeMessage(java.io.InputStream)
     */
    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#send(javax.mail.internet.MimeMessage)
     */
    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#send(javax.mail.internet.MimeMessage[])
     */
    @Override
    public void send(MimeMessage[] mimeMessages) throws MailException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#send(org.springframework.mail.javamail.MimeMessagePreparator)
     */
    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.springframework.mail.javamail.JavaMailSender#send(org.springframework.mail.javamail.MimeMessagePreparator[])
     */
    @Override
    public void send(MimeMessagePreparator[] mimeMessagePreparators) throws MailException {
        // TODO Auto-generated method stub

    }

}
