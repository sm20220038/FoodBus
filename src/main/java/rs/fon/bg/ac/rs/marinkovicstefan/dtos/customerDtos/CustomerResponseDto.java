package rs.fon.bg.ac.rs.marinkovicstefan.dtos.customerDtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Customer;
public record CustomerResponseDto(Long id, String name, String email, String phone, String address) {

    public static CustomerResponseDto fromEntity(Customer customer){
        return new CustomerResponseDto(customer.getId(), customer.getName(), customer.getEmail(),
                customer.getPhone(), customer.getAddress());
    }
}
