package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.restaurantDtos.RestaurantResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Service for searching and filtering restaurants.
 * Supports combined filtering by cuisine type, minimum average rating
 * and a case-insensitive name search.
 * @author Stefan Marinkovic
 */
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Retrieves all restaurants matching the given filter criteria.
     * Every criterion is optional; criteria that are null are ignored,
     * so calling the method without any criteria returns all restaurants.
     *
     * @param cuisine cuisine type the restaurant must serve, or null to ignore.
     * @param minRating minimum average rating between 0 and 5 the restaurant must have, or null to ignore.
     * @param name part of the restaurant name to search for, or null to ignore.
     * @return List of RestaurantResponseDto objects representing all restaurants that match the criteria.
     * @throws java.lang.IllegalArgumentException If the minimum rating is outside the 0 to 5 range.
     */
    @Transactional
    public List<RestaurantResponseDto> filter(String cuisine, Double minRating, String name){
        if (minRating != null && (minRating < 0 || minRating > 5)) {
            throw new IllegalArgumentException("Minimum rating must be between 0 and 5");
        }
        List<RestaurantResponseDto> responseList = new ArrayList<>();
        List<Restaurant> restaurants = restaurantRepository.filter(cuisine, minRating, name);
        for (Restaurant restaurant : restaurants) {
            responseList.add(RestaurantResponseDto.fromEntity(restaurant));
        }
        return responseList;
    }
}
