package com.project.kisan_setu.util;

import com.project.kisan_setu.entity.*;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.Role;
import com.project.kisan_setu.exception.ResourceNotFoundException;
import com.project.kisan_setu.exception.UserException;
import com.project.kisan_setu.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ValidatorMethods {
    private final ListingRepository listingRepository;
    private final UserRepository userRepository;
    private final CropRepository cropRepository;
    private final UnitRepository unitRepository;
    private final StateRepository stateRepository;
    private final DistrictRepository districtRepository;
    private final PackagingRepository packagingRepository;
    private final StorageRepository storageRepository;

    public Listing validateExists(Long listingId){
        return listingRepository.findById(listingId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Listing not found"));
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        return Long.parseLong(auth.getName());
    }

    public User validateUserById(Long userId){
        return userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User Not Found"));
    }
    public void checkStatus(Listing listing, AuctionStatus requiredStatus){
        if(listing.getStatus() != requiredStatus){
            throw new UserException("Operation allowed only when listing is "+requiredStatus);
        }
    }
    public CropMaster validateCrop(Long cropId) {
        return cropRepository.findById(cropId)
                .orElseThrow(() -> new RuntimeException("Crop not found"));
    }

    public UnitMaster validateUnit(Long unitId) {
        return unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
    }

    public StateMaster validateState(Long stateId) {
        return stateRepository.findById(stateId)
                .orElseThrow(() -> new RuntimeException("State not found"));
    }

    public DistrictMaster validateDistrict(Long districtId) {
        return districtRepository.findById(districtId)
                .orElseThrow(() -> new RuntimeException("District not found"));
    }
    public PackagingMaster validatePackaging(Long packagingId) {
        return packagingRepository.findById(packagingId)
                .orElseThrow(() -> new RuntimeException("Packaging not found"));
    }

    public StorageMaster validateStorage(Long storageId) {
        return storageRepository.findById(storageId)
                .orElseThrow(() -> new RuntimeException("Storage not found"));
    }

    public void validateAdminAccess() {
        Long userId = getCurrentUserId();
        User user = validateUserById(userId);
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied! Admin only.");
        }
    }
    public void validateUserAccess() {
        Long userId = getCurrentUserId();
        User user = validateUserById(userId);
        if (user.getRole() != Role.USER) {
            throw new RuntimeException("Access denied! Not allowed for admin.");
        }
    }

}
