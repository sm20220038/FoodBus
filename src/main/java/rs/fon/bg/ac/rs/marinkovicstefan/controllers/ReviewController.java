package rs.fon.bg.ac.rs.marinkovicstefan.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos.ReviewAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos.ReviewResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.reviewDtos.ReviewUpdateDto;
import rs.fon.bg.ac.rs.marinkovicstefan.services.ReviewService;

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

    @PutMapping("api/reviews/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody ReviewUpdateDto reviewUpdate){
        try {
            ReviewResponseDto response = reviewService.update(id, reviewUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("api/reviews/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id){
        try {
            reviewService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body("Review deleted");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
