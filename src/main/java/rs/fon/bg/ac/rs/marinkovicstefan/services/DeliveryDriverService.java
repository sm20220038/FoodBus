package rs.fon.bg.ac.rs.marinkovicstefan.services;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.fon.bg.ac.rs.marinkovicstefan.domain.DeliveryDriver;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.deliverydriverdtos.DeliveryDriverAddDto;
import rs.fon.bg.ac.rs.marinkovicstefan.dtos.deliverydriverdtos.DeliveryDriverResponseDto;
import rs.fon.bg.ac.rs.marinkovicstefan.repositories.DeliveryDriverRepository;

/**
 * Service for managing delivery drivers.
 * Handles registering new drivers and updating or deleting existing ones.
 * @author Stefan Marinkovic
 */
@Service
public class DeliveryDriverService {
    private final DeliveryDriverRepository deliveryDriverRepository;

    public DeliveryDriverService(DeliveryDriverRepository deliveryDriverRepository) {
        this.deliveryDriverRepository = deliveryDriverRepository;
    }

    /**
     * Registers a new delivery driver.
     *
     * @param driverAdd DeliveryDriverAddDto data transfer object containing the driver details.
     * @return DeliveryDriverResponseDto containing the information of the newly created driver.
     */
    @Transactional
    public DeliveryDriverResponseDto create(DeliveryDriverAddDto driverAdd){
        return DeliveryDriverResponseDto.fromEntity(deliveryDriverRepository.save(driverAdd.toEntity()));
    }

    /**
     * Updates the details of an existing delivery driver.
     *
     * @param id unique identifier of the driver to update.
     * @param driverUpdate DeliveryDriverAddDto containing the new driver details.
     * @return DeliveryDriverResponseDto containing the updated driver information.
     * @throws jakarta.persistence.EntityNotFoundException If the driver cannot be found.
     */
    @Transactional
    public DeliveryDriverResponseDto update(Long id, DeliveryDriverAddDto driverUpdate){
        DeliveryDriver driver = deliveryDriverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver doesnt exist"));
        driver.setName(driverUpdate.name());
        driver.setPhone(driverUpdate.phone());
        driver.setVehicle(driverUpdate.vehicle());
        driver.setAvailable(driverUpdate.available());
        return DeliveryDriverResponseDto.fromEntity(deliveryDriverRepository.save(driver));
    }

    /**
     * Deletes an existing delivery driver.
     *
     * @param id unique identifier of the driver to delete.
     * @throws jakarta.persistence.EntityNotFoundException If the driver cannot be found.
     */
    @Transactional
    public void delete(Long id){
        DeliveryDriver driver = deliveryDriverRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Driver doesnt exist"));
        deliveryDriverRepository.delete(driver);
    }
}
