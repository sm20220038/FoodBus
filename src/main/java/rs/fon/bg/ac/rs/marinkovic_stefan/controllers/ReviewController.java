package rs.fon.bg.ac.rs.marinkovic_stefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.reviewDtos.ReviewAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.reviewDtos.ReviewResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.services.ReviewService;

@RestController
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("api/reviews")
    public ResponseEntity<Object> create(@RequestBody ReviewAddDto reviewAdd){
        try {
            ReviewResponseDto response = reviewService.create(reviewAdd);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
