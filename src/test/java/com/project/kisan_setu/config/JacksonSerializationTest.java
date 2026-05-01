package com.project.kisan_setu.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.project.kisan_setu.dto.ResponseDto.BidResponseDto;
import com.project.kisan_setu.dto.ResponseDto.SellerListingDto;
import com.project.kisan_setu.enums.AuctionStatus;
import com.project.kisan_setu.enums.BidStatus;
import com.project.kisan_setu.enums.PurchaseType;
import com.project.kisan_setu.enums.SaleType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class JacksonSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void springObjectMapperSerializesSellerListingDto() throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(createSellerListingDto());

        assertThat(json).contains("auctionEndTime");
    }

    @Test
    void redisSerializerSerializesSellerListingDto() {
        ObjectMapper cacheObjectMapper = objectMapper.copy();
        cacheObjectMapper.registerModule(new JavaTimeModule());

        GenericJackson2JsonRedisSerializer serializer = GenericJackson2JsonRedisSerializer
                .builder()
                .objectMapper(cacheObjectMapper)
                .defaultTyping(true)
                .build();

        byte[] bytes = serializer.serialize(createSellerListingDto());

        assertThat(bytes).isNotEmpty();
    }

    private SellerListingDto createSellerListingDto() {
        return new SellerListingDto(
                12L,
                "Wheat",
                "Sharbati",
                "A",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(25),
                "kg",
                BigDecimal.valueOf(2500),
                BigDecimal.valueOf(50),
                BigDecimal.valueOf(100),
                5L,
                3L,
                AuctionStatus.ACTIVE,
                LocalDateTime.of(2026, 4, 13, 18, 0),
                BigDecimal.valueOf(2750),
                PurchaseType.WHOLE_LOT_ONLY,
                SaleType.AUCTION,
                LocalDateTime.of(2026, 4, 13, 9, 0),
                "Kerala",
                "Kollam",
                "Pickup",
                "Cold Storage",
                List.of(new BidResponseDto(
                        1L,
                        21L,
                        BigDecimal.valueOf(2600),
                        "Buyer One",
                        LocalDateTime.of(2026, 4, 13, 10, 15),
                        BidStatus.PENDING,
                        99L
                )),
                0L,
                List.of(),
                LocalDate.of(2026, 4, 10),
                "Bag",
                99L
        );
    }
}
      
