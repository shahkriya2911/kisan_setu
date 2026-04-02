package com.project.kisan_setu.util;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.Order;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.OrderStatus;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Component
public class OtpGenerator {

    private final SecureRandom random = new SecureRandom();

    public String generateOtp() {

        int otp = 100000 + random.nextInt(900000);

        return String.valueOf(otp);
    }

}
