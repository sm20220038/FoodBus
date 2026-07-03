package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.reviewDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Review;

import java.time.LocalDateTime;
public record ReviewAddDto(Long customerId, Long restaurantId, int rating, String comment) {
    public Review toEntity(){
        return Review.builder().rating(rating).comment(comment).createdAt(LocalDateTime.now()).build();
    }
}
