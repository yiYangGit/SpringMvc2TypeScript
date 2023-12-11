package example.model;

import example.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY)
public class AddressDTO extends BaseDTO {

    public String street;
    public String zipCode;
    public Integer buildingNumber;
    private String buildingNumberExtension;
    private String t2;
    private boolean t3;

    public boolean getT3() {
        return t3;
    }

    public AddressDTO setT3(boolean t3) {
        this.t3 = t3;
        return this;
    }

    public String getT2() {
        return t2;
    }

    public AddressDTO setT2(String t2) {
        this.t2 = t2;
        return this;
    }

}