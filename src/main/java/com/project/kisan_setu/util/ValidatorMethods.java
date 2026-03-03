package com.project.kisan_setu.util;

import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.entity.User;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.exception.ResourceNotFoundException;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.ListingRepository;
import com.project.kisan_setu.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorMethods {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;

    public Listing validateExists(Long listingId){
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Listing not found"));
    }

    public String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return auth.getName();
    }

    public User validateUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
    }
    public void checkStatus(Listing listing, AuctionStatus requiredStatus){
        if(listing.getStatus() != requiredStatus){
            throw new UserException("Operation allowed only when listing is "+requiredStatus);
        }
    }

}
