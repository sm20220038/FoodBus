package rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewdtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Review;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ReviewAddDto(Long customerId, Long restaurantId, int rating, String comment) {
    public Review toEntity(){
        return Review.builder().rating(rating).comment(comment).createdAt(LocalDateTime.now(ZoneId.of("Europe/Belgrade"))).build();
    }
}
