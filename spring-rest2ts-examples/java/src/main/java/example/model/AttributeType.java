package example.model;


import example.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
public class AttributeType extends BaseDTO implements Named{

    public String name;

    @Override
    public String getName() {
        return name;
    }
}
