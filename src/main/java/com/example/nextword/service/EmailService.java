package com.example.nextword.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarTokenRecuperacion(String destinatario, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("teamnextword@gmail.com");
        message.setTo(destinatario);
        message.setSubject("Código de Recuperación de Contraseña");
        message.setText("Hola,\n\nTu código de recuperación es: " + token + "\n\nIngresa este código en la aplicación para crear una nueva contraseña. Si no solicitaste esto, ignora este correo.");
        
        mailSender.send(message);
    }
}