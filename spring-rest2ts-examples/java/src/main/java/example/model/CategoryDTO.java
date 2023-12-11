package example.model;

import example.biz.TestUser2;
import example.biz.TestUser3;
import example.biz.TbCond;
import example.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.*;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

public class CategoryDTO extends BaseDTO implements Named{

    private String name;
    private String categoryName;
    private String shortName;
    private CategoryDTO parentCategory;
    @JsonBackReference
    private List<ProductDTO> products = new ArrayList<>();

    @JsonProperty("creationDateProperty")
    private LocalDateTime creationDate;

    public CategoryDTO(String name) {
        this.name = name;
    }

    @JsonGetter("categoryName")
    public String name() {
        return name;
    }

    @JsonSetter("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("parentCategory")
    public CategoryDTO getParentCategory() {
        return parentCategory;
    }

    @JsonSetter("shortName")
    public  void setShortName(String shortName) {
        this.shortName = shortName;
    }

    @JsonProperty("creationDateGetter")
    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @Override
    public String getName() {
        return name;
    }


    //@JsonIgnoreProperties(value = {"barcode"}, allowGetters = true)
    @JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ProductDTO extends BaseDTO {

        public int name = 1;
        private BigInteger price;
        private String barcode;
        public URI websiteURI;

        @JsonFormat(shape = JsonFormat.Shape.STRING)
        public Date expirationDate;
        private ManufacturerDTO manufacturer;
        public Map<String, TestUser2<TestUser3<Integer>,TbCond>> tags;

    //    @JsonManagedReference
        public CategoryDTO categoryDTO;

        @JsonRawValue
        public Map<Integer, String> translationsMap;

        public String getBarcode() {
            return barcode;
        }


        public void setBarcode(String barcode) {
            this.barcode = barcode;
        }

        public int getName() {
            return name;
        }


        @JsonSetter(value = "secondName")
        public void name(@Nullable String name) {
        }

    }
}
