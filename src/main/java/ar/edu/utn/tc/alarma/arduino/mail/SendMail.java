/*
 * Copyright 2015 UTN Facultad Regional Resistencia.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ar.edu.utn.tc.alarma.arduino.mail;

/**
 * Clase utilitaria para facilitar el envío de mails. Actualmente hace el envío a través de mandrill.
 *
 * @author Rodrigo M. Tato Rothamel mailto:rotatomel@gmail.com
 */
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    private static final Logger LOG = Logger.getLogger(SendMail.class.getName());

    /**
     * Realiza el envío de un correo electrónico a la dirección toAddress con el mensaje msg y con el asunto subject
     *
     * @param toAddress la dirección de destino
     * @param msg el mensaje a enviar
     * @param subject el asunto del correo
     */
    public static void sendMail(String toAddress, String msg, String subject) {

        final String username = "rotatomel@gmail.com";
        final String password = "ASglevlndFJLchfWK5alMQ";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mandrillapp.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("alarma@prueba.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setText(msg);

            Transport.send(message);

            LOG.log(Level.INFO, String.format("El email se ha enviado a %s", toAddress));

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
