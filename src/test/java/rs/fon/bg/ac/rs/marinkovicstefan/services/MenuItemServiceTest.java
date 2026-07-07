package rs.fon.bg.ac.rs.marinkovicstefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.MenuItem;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.Restaurant;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuItemDtos.MenuItemAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.menuItemDtos.MenuItemResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.MenuItemRepository;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;
    @Mock
    private RestaurantRepository restaurantRepository;

    @InjectMocks
    private MenuItemService menuItemService;

    private Long restaurantId;
    private Long menuItemId;
    private Restaurant sampleRestaurant;
    private MenuItem sampleMenuItem;

    @BeforeEach
    void setUp() {
        restaurantId = 1L;
        menuItemId = 2L;

        sampleRestaurant = Restaurant.builder()
                .id(restaurantId)
                .name("Burger House")
                .address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj")
                .rating(4.5)
                .email("kontakt@burgerhouse.rs")
                .build();

        sampleMenuItem = MenuItem.builder()
                .id(menuItemId)
                .name("Cheeseburger")
                .description("Juneca pljeskavica, cedar, kiseli krastavci")
                .price(new BigDecimal("590.00"))
                .available(true)
                .restaurant(sampleRestaurant)
                .build();
    }

    @AfterEach
    void tearDown() {
        // Nothing happens here
    }

    @Test
    @DisplayName("Should successfully create a menu item when restaurant exists and name is unique")
    void create_ValidMenuItem_SavesAndReturnsResponseDto() {
        MenuItemAddDto addDto = new MenuItemAddDto("Cheeseburger", "Juneca pljeskavica, cedar, kiseli krastavci",
                new BigDecimal("590.00"), true, restaurantId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId, "Cheeseburger")).thenReturn(false);
        when(menuItemRepository.save(any(MenuItem.class))).thenReturn(sampleMenuItem);

        MenuItemResponseDto result = menuItemService.create(addDto);

        assertNotNull(result);
        assertEquals("Cheeseburger", result.name());
        assertEquals("Burger House", result.restaurantName());
        assertEquals(new BigDecimal("590.00"), result.price());

        verify(menuItemRepository).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when restaurant does not exist")
    void create_RestaurantMissing_ThrowsEntityNotFoundException() {
        MenuItemAddDto addDto = new MenuItemAddDto("Cheeseburger", null, new BigDecimal("590.00"), true, restaurantId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> menuItemService.create(addDto));
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when menu item name already exists in the restaurant")
    void create_DuplicateName_ThrowsIllegalArgumentException() {
        MenuItemAddDto addDto = new MenuItemAddDto("Cheeseburger", null, new BigDecimal("590.00"), true, restaurantId);

        when(restaurantRepository.findById(restaurantId)).thenReturn(Optional.of(sampleRestaurant));
        when(menuItemRepository.existsByRestaurantIdAndNameIgnoreCase(restaurantId, "Cheeseburger")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                menuItemService.create(addDto)
        );

        assertEquals("Menu item with the same name already exists in this restaurant", exception.getMessage());
        verify(menuItemRepository, never()).save(any(MenuItem.class));
    }

    @Test
    @DisplayName("Should delete a menu item when it exists in the given restaurant")
    void delete_ExistingMenuItem_DeletesIt() {
        when(menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)).thenReturn(Optional.of(sampleMenuItem));

        menuItemService.delete(restaurantId, menuItemId);

        verify(menuItemRepository).delete(sampleMenuItem);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting a menu item that does not exist")
    void delete_MissingMenuItem_ThrowsEntityNotFoundException() {
        when(menuItemRepository.findByIdAndRestaurantId(menuItemId, restaurantId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> menuItemService.delete(restaurantId, menuItemId));
        verify(menuItemRepository, never()).delete(any(MenuItem.class));
    }

    @Test
    @DisplayName("Should return all available menu items for an existing restaurant")
    void viewMenu_ExistingRestaurant_ReturnsAvailableItems() {
        MenuItem secondItem = MenuItem.builder()
                .id(3L)
                .name("Pomfrit")
                .price(new BigDecimal("250.00"))
                .available(true)
                .restaurant(sampleRestaurant)
                .build();

        when(restaurantRepository.existsById(restaurantId)).thenReturn(true);
        when(menuItemRepository.findAllByRestaurantIdAndAvailableTrue(restaurantId))
                .thenReturn(List.of(sampleMenuItem, secondItem));

        List<MenuItemResponseDto> result = menuItemService.viewMenu(restaurantId);

        assertEquals(2, result.size());
        assertEquals("Cheeseburger", result.get(0).name());
        assertEquals("Pomfrit", result.get(1).name());
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when viewing the menu of a missing restaurant")
    void viewMenu_MissingRestaurant_ThrowsEntityNotFoundException() {
        when(restaurantRepository.existsById(restaurantId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> menuItemService.viewMenu(restaurantId));
        verifyNoInteractions(menuItemRepository);
    }
}
