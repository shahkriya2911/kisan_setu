package com.project.kisan_setu.service.impl;

import com.project.kisan_setu.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    //Mobile Verifications
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("KisanSetu - Mobile Verification OTP");
        message.setText("Dear User,\n\n" +
                "Your OTP for mobile number verification is: " + otp + "\n\n" +
                "This OTP is valid for 10 minutes.\n\n" +
                "Do not share this OTP with anyone.\n\n" +
                "Team KisanSetu");
        javaMailSender.send(message);

    }
}
