package com.project.kisan_setu.specification;

import com.project.kisan_setu.dto.RequestDto.MarketFilterRequestDto;
import com.project.kisan_setu.entity.Listing;
import com.project.kisan_setu.enums.AuctionStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ListingSpecification {

    public static Specification<Listing> filterListings(MarketFilterRequestDto filter) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("status"), AuctionStatus.ACTIVE));

            if (filter.getCropId() != null) {
                predicates.add(cb.equal(root.get("cropName"), filter.getCropId()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.ge(root.get("pricePerKg"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.le(root.get("pricePerKg"), filter.getMaxPrice()));
            }

            if (filter.getStateId() != null) {
                predicates.add(cb.equal(root.get("district"), filter.getStateId()));
            }
            if (filter.getDistrictId() != null) {
                predicates.add(cb.equal(root.get("district"), filter.getDistrictId()));
            }

            if (filter.getSaleType() != null) {
                predicates.add(cb.equal(root.get("saleType"), filter.getSaleType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
