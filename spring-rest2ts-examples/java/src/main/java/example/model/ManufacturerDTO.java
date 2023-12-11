package example.model;

import com.blueveery.springrest2ts.angular2jsonapi.JsonApiModelConfig;
import example.model.core.BaseDTO;

@JsonApiModelConfig(type="manufactures")
public class ManufacturerDTO extends BaseDTO {

    private String name;
    private String shortName;
    private AddressDTO headquartersAddress;

    public String getName() {
        return name;
    }

    public ManufacturerDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getShortName() {
        return shortName;
    }

    public ManufacturerDTO setShortName(String shortName) {
        this.shortName = shortName;
        return this;
    }

    public AddressDTO getHeadquartersAddress() {
        return headquartersAddress;
    }

    public ManufacturerDTO setHeadquartersAddress(AddressDTO headquartersAddress) {
        this.headquartersAddress = headquartersAddress;
        return this;
    }
}
