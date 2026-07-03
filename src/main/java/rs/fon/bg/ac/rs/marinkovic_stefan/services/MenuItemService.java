package rs.fon.bg.ac.rs.marinkovic_stefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.MenuItem;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos.MenuItemAddDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos.MenuItemResponseDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.dtos.menuItemDtos.MenuItemUpdateDto;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.MenuItemRepository;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.RestaurantRepository;

import java.util.ArrayList;
import java.util.List;
/**
 * Service for managing restaurant menu items.
 * Handles adding new items to a menu, removing existing items and
 * retrieving all available items of a restaurant.
 * @author Stefan Marinkovic
 */
@Service
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    public MenuItemService(MenuItemRepository menuItemRepository, RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Adds a new menu item to the menu of an existing restaurant.
     * Enforces that the menu item name is unique within the restaurant.
     *
     * @param menuItemAdd MenuItemAddDto data transfer object containing the item name, description, price, availability and restaurant.
     * @return MenuItemResponseDto containing the information of the newly created menu item.
     * @throws jakarta.persistence.EntityNotFoundException If the restaurant cannot be found.
     * @throws java.lang.IllegalArgumentException If a menu item with the same name already exists in the restaurant.
     */
    @Transactional
    public MenuItemResponseDto create(MenuItemAddDto menuItemAdd){
        Restaurant restaurant = restaurantRepository.findById(menuItemAdd.restaurantId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant doesnt exist"));

        if (menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(menuItemAdd.restaurantId(), menuItemAdd.name())) {
            throw new IllegalArgumentException("Menu item with the same name already exists in this restaurant");
        }

        MenuItem menuItem = menuItemAdd.toEntity();
        menuItem.setRestaurant(restaurant);
        return MenuItemResponseDto.fromEntity(menuItemRepository.save(menuItem));
    }

    /**
     * Updates the details of an existing menu item of a restaurant.
     *
     * @param restaurantId unique identifier of the restaurant that owns the menu item.
     * @param id unique identifier of the menu item to update.
     * @param menuItemUpdate MenuItemUpdateDto containing the new name, description, price and availability.
     * @return MenuItemResponseDto containing the updated menu item information.
     * @throws jakarta.persistence.EntityNotFoundException If the menu item does not exist within the given restaurant.
     */
    @Transactional
    public MenuItemResponseDto update(Long restaurantId, Long id, MenuItemUpdateDto menuItemUpdate){
        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item doesnt exist"));
        menuItem.setName(menuItemUpdate.name());
        menuItem.setDescription(menuItemUpdate.description());
        menuItem.setPrice(menuItemUpdate.price());
        menuItem.setAvailable(menuItemUpdate.available());
        return MenuItemResponseDto.fromEntity(menuItemRepository.save(menuItem));
    }

    /**
     * Removes a menu item from the menu of a restaurant.
     *
     * @param restaurantId unique identifier of the restaurant that owns the menu item.
     * @param id unique identifier of the menu item to be removed.
     * @throws jakarta.persistence.EntityNotFoundException If the menu item does not exist within the given restaurant.
     */
    @Transactional
    public void delete(Long restaurantId, Long id){
        MenuItem menuItem = menuItemRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item doesnt exist"));
        menuItemRepository.delete(menuItem);
    }

    /**
     * Retrieves all currently available menu items of a restaurant.
     *
     * @param restaurantId unique identifier of the restaurant whose menu is requested.
     * @return List of MenuItemResponseDto objects representing all available menu items of the restaurant.
     * @throws jakarta.persistence.EntityNotFoundException If the restaurant cannot be found.
     */
    @Transactional
    public List<MenuItemResponseDto> viewMenu(Long restaurantId){
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new EntityNotFoundException("Restaurant doesnt exist");
        }
        List<MenuItemResponseDto> responseList = new ArrayList<>();
        List<MenuItem> menuItems = menuItemRepository.findAllByRestaurantIdAndAvailableTrue(restaurantId);
        for (MenuItem menuItem : menuItems) {
            responseList.add(MenuItemResponseDto.fromEntity(menuItem));
        }
        return responseList;
    }
}
