package example.model;

import example.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDate;

public class PersonDTO extends BaseDTO {
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private AddressDTO homeAddress;
    @JsonIgnore
    private AddressDTO workAddress;

    public String getFirstName() {
        return firstName;
    }

    public PersonDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public PersonDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public PersonDTO setBirthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public AddressDTO getHomeAddress() {
        return homeAddress;
    }

    public PersonDTO setHomeAddress(AddressDTO homeAddress) {
        this.homeAddress = homeAddress;
        return this;
    }

    public AddressDTO getWorkAddress() {
        return workAddress;
    }

    public PersonDTO setWorkAddress(AddressDTO workAddress) {
        this.workAddress = workAddress;
        return this;
    }
}
