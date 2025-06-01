package com.example.CryptoPortfolioTracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = new EmailService();
        try {
            var field = EmailService.class.getDeclaredField("mailSender");
            field.setAccessible(true);
            field.set(emailService, mailSender);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void sendEmail_ShouldSendEmailWithCorrectDetails() {
        String to = "mspraveenkumar77@gmail.com";
        String subject = "Test Subject";
        String body = "Test Body";
        emailService.sendEmail(to, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(to, sentMessage.getTo()[0]);
        assertEquals(subject, sentMessage.getSubject());
        assertEquals(body, sentMessage.getText());
        assertEquals("cgdevteam3@gmail.com", sentMessage.getFrom());
    }
}
