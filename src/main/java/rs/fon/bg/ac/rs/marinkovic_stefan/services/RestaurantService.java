package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.restaurantDtos.RestaurantAddDto;
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

    /**
     * Registers a new restaurant with an initial rating of zero.
     * Enforces that the email address is not already used by another restaurant.
     *
     * @param restaurantAdd RestaurantAddDto data transfer object containing the restaurant details.
     * @return RestaurantResponseDto containing the information of the newly created restaurant.
     * @throws java.lang.IllegalArgumentException If a restaurant with the same email already exists.
     */
    @Transactional
    public RestaurantResponseDto create(RestaurantAddDto restaurantAdd){
        if (restaurantRepository.existsByEmail(restaurantAdd.email())) {
            throw new IllegalArgumentException("Restaurant with this email already exists");
        }
        return RestaurantResponseDto.fromEntity(restaurantRepository.save(restaurantAdd.toEntity()));
    }

    /**
     * Updates the details of an existing restaurant. The average rating is not changed here,
     * as it is derived from customer reviews.
     *
     * @param id unique identifier of the restaurant to update.
     * @param restaurantUpdate RestaurantAddDto containing the new restaurant details.
     * @return RestaurantResponseDto containing the updated restaurant information.
     * @throws jakarta.persistence.EntityNotFoundException If the restaurant cannot be found.
     */
    @Transactional
    public RestaurantResponseDto update(Long id, RestaurantAddDto restaurantUpdate){
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant doesnt exist"));
        restaurant.setName(restaurantUpdate.name());
        restaurant.setAddress(restaurantUpdate.address());
        restaurant.setCuisine(restaurantUpdate.cuisine());
        restaurant.setEmail(restaurantUpdate.email());
        return RestaurantResponseDto.fromEntity(restaurantRepository.save(restaurant));
    }

    /**
     * Deletes an existing restaurant.
     *
     * @param id unique identifier of the restaurant to delete.
     * @throws jakarta.persistence.EntityNotFoundException If the restaurant cannot be found.
     */
    @Transactional
    public void delete(Long id){
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Restaurant doesnt exist"));
        restaurantRepository.delete(restaurant);
    }
}
