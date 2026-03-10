package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
    //Mobile Verifications
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        logger.info("Sending otp to email...");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("KisanSetu - Mobile Verification OTP");
        message.setText("Dear User,\n\n" +
                "Your OTP for mobile number verification is: " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Do not share this OTP with anyone.\n\n" +
                "Team KisanSetu");
        logger.info("Otp sent to email success...");
        javaMailSender.send(message);

    }
}
