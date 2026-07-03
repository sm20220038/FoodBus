package rs.fon.bg.ac.rs.marinkovic_stefan.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rs.fon.bg.ac.rs.marinkovic_stefan.domain.*;
import rs.fon.bg.ac.rs.marinkovic_stefan.repositories.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a small set of sample data into an empty database on application startup.
 * Runs only when there are no restaurants yet, so restarting the application does
 * not create duplicate records. Uses the repositories directly so that Long keys
 * and entity relationships are populated by the persistence provider.
 * @author Stefan Marinkovic
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerRepository customerRepository;
    private final DeliveryDriverRepository deliveryDriverRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    public DataSeeder(RestaurantRepository restaurantRepository, MenuItemRepository menuItemRepository,
                      CustomerRepository customerRepository, DeliveryDriverRepository deliveryDriverRepository,
                      OrderRepository orderRepository, PaymentRepository paymentRepository,
                      ReviewRepository reviewRepository) {
        this.restaurantRepository = restaurantRepository;
        this.menuItemRepository = menuItemRepository;
        this.customerRepository = customerRepository;
        this.deliveryDriverRepository = deliveryDriverRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (restaurantRepository.count() > 0) {
            log.info("Database already contains data, skipping seed.");
            return;
        }

        // ----- Restaurants -----
        Restaurant burgerHouse = restaurantRepository.save(Restaurant.builder()
                .name("Burger House").address("Bulevar kralja Aleksandra 73, Beograd")
                .cuisine("Roostilj").rating(4.5).email("kontakt@burgerhouse.rs").build());
        Restaurant pizzaBar = restaurantRepository.save(Restaurant.builder()
                .name("Pizza Bar").address("Knez Mihailova 5, Beograd")
                .cuisine("Italijanska").rating(4.2).email("info@pizzabar.rs").build());
        Restaurant wokIn = restaurantRepository.save(Restaurant.builder()
                .name("Wok In").address("Cara Dusana 21, Beograd")
                .cuisine("Kineska").rating(4.7).email("hello@wokin.rs").build());

        // ----- Menu items -----
        MenuItem cheeseburger = menuItemRepository.save(MenuItem.builder()
                .name("Cheeseburger").description("Juneca pljeskavica, cedar, kiseli krastavci")
                .price(new BigDecimal("590.00")).available(true).restaurant(burgerHouse).build());
        MenuItem pomfrit = menuItemRepository.save(MenuItem.builder()
                .name("Pomfrit").description("Krompir, morska so")
                .price(new BigDecimal("250.00")).available(true).restaurant(burgerHouse).build());
        menuItemRepository.save(MenuItem.builder()
                .name("Cola").description("0.33l")
                .price(new BigDecimal("150.00")).available(true).restaurant(burgerHouse).build());
        menuItemRepository.save(MenuItem.builder()
                .name("Margarita").description("Paradajz, mocarela, bosiljak")
                .price(new BigDecimal("750.00")).available(true).restaurant(pizzaBar).build());
        menuItemRepository.save(MenuItem.builder()
                .name("Capricciosa").description("Sunka, pecurke, mocarela")
                .price(new BigDecimal("890.00")).available(true).restaurant(pizzaBar).build());
        menuItemRepository.save(MenuItem.builder()
                .name("Piletina Bali").description("Piletina u slatko-ljutom sosu")
                .price(new BigDecimal("690.00")).available(true).restaurant(wokIn).build());
        menuItemRepository.save(MenuItem.builder()
                .name("Prolecne rolnice").description("Povrce, 4 komada")
                .price(new BigDecimal("320.00")).available(false).restaurant(wokIn).build());

        // ----- Customers -----
        Customer marko = customerRepository.save(Customer.builder()
                .name("Marko Markovic").email("marko.markovic@gmail.com")
                .phone("0641234567").address("Jove Ilica 154, Beograd").build());
        Customer jovana = customerRepository.save(Customer.builder()
                .name("Jovana Jovanovic").email("jovana.jovanovic@gmail.com")
                .phone("0629876543").address("Nemanjina 4, Beograd").build());
        customerRepository.save(Customer.builder()
                .name("Petar Petrovic").email("petar.petrovic@gmail.com")
                .phone("0655554433").address("Takovska 2, Beograd").build());

        // ----- Delivery drivers -----
        DeliveryDriver nikola = deliveryDriverRepository.save(DeliveryDriver.builder()
                .name("Nikola Nikolic").phone("0651112233").vehicle("Motor").available(true).build());
        deliveryDriverRepository.save(DeliveryDriver.builder()
                .name("Ana Anic").phone("0644445566").vehicle("Bicikl").available(true).build());

        // ----- Order 1: delivered, paid, reviewed -----
        Order deliveredOrder = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(1))
                .status(OrderStatus.DELIVERED)
                .customer(marko).restaurant(burgerHouse).driver(nikola)
                .build();
        List<OrderItem> items1 = new ArrayList<>();
        items1.add(OrderItem.builder().quantity(2).unitPrice(cheeseburger.getPrice())
                .subtotal(cheeseburger.getPrice().multiply(BigDecimal.valueOf(2)))
                .menuItem(cheeseburger).order(deliveredOrder).build());
        items1.add(OrderItem.builder().quantity(1).unitPrice(pomfrit.getPrice())
                .subtotal(pomfrit.getPrice())
                .menuItem(pomfrit).order(deliveredOrder).build());
        deliveredOrder.setOrderItems(items1);
        deliveredOrder.setTotal(new BigDecimal("1430.00"));
        orderRepository.save(deliveredOrder);

        paymentRepository.save(Payment.builder()
                .amount(deliveredOrder.getTotal()).method(PayMethod.CARD)
                .paidAt(LocalDateTime.now().minusDays(1).plusMinutes(35))
                .order(deliveredOrder).build());

        reviewRepository.save(Review.builder()
                .rating(5).comment("Odlicna hrana, brza dostava!")
                .createdAt(LocalDateTime.now().minusHours(20))
                .customer(marko).restaurant(burgerHouse).build());

        // ----- Order 2: still being prepared -----
        Order preparingOrder = Order.builder()
                .orderDate(LocalDateTime.now().minusMinutes(15))
                .status(OrderStatus.PREPARING)
                .customer(jovana).restaurant(pizzaBar)
                .build();
        List<OrderItem> items2 = new ArrayList<>();
        MenuItem margarita = menuItemRepository.findAllByRestaurantIdAndAvailableTrue(pizzaBar.getId()).get(0);
        items2.add(OrderItem.builder().quantity(1).unitPrice(margarita.getPrice())
                .subtotal(margarita.getPrice())
                .menuItem(margarita).order(preparingOrder).build());
        preparingOrder.setOrderItems(items2);
        preparingOrder.setTotal(margarita.getPrice());
        orderRepository.save(preparingOrder);

        log.info("FoodBus seed complete: {} restaurants, {} menu items, {} customers, {} drivers, {} orders, {} payments, {} reviews",
                restaurantRepository.count(), menuItemRepository.count(), customerRepository.count(),
                deliveryDriverRepository.count(), orderRepository.count(), paymentRepository.count(),
                reviewRepository.count());
    }
}
