package com.project.kisan_setu.service;

public interface EmailService {
     void sendOtpEmail(String toEmail, String otp);
    void sendEmail(String to, String subject, String body);
}
