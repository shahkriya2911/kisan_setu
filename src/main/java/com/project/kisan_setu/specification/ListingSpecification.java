package com.project.kisan_setu.specification;

import com.project.kisan_setu.dto.MarketFilterRequestDto;
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

            if (filter.getCropType() != null) {
                predicates.add(cb.equal(root.get("cropName"), filter.getCropType()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.ge(root.get("pricePerKg"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.le(root.get("pricePerKg"), filter.getMaxPrice()));
            }

            if (filter.getLocation() != null) {
                predicates.add(cb.equal(root.get("district"), filter.getLocation()));
            }

            if (filter.getSaleType() != null) {
                predicates.add(cb.equal(root.get("saleType"), filter.getSaleType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
