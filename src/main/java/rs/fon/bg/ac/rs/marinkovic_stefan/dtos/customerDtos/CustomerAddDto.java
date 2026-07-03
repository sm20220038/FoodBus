package rs.fon.bg.ac.rs.marinkovic_stefan.dtos.customerDtos;

import rs.fon.bg.ac.rs.marinkovic_stefan.domain.Customer;

public record CustomerAddDto(String name, String email, String phone, String address) {
    public Customer toEntity(){
        return Customer.builder().name(name).email(email).phone(phone).address(address).build();
    }
}
