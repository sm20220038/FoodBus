package rs.fon.bg.ac.rs.marinkovicstefan.dtos.customerdtos;

import rs.fon.bg.ac.rs.marinkovicstefan.domain.Customer;

public record CustomerAddDto(String name, String email, String phone, String address) {
    public Customer toEntity(){
        return Customer.builder().name(name).email(email).phone(phone).address(address).build();
    }
}
