package rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Review;

import java.time.LocalDateTime;
public record ReviewResponseDto(Long id, int rating, String comment, LocalDateTime createdAt,
                                String customerName, String restaurantName) {

    public static ReviewResponseDto fromEntity(Review review){
        String customerName = (review.getCustomer() != null) ? review.getCustomer().getName() : "Nema kupca";
        String restaurantName = (review.getRestaurant() != null) ? review.getRestaurant().getName() : "Nema restorana";
        return new ReviewResponseDto(review.getId(), review.getRating(), review.getComment(),
                review.getCreatedAt(), customerName, restaurantName);
    }
}
