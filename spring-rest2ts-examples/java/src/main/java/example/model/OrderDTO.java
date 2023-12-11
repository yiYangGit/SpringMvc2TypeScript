package example.model;

import example.model.core.BaseDTO;
import example.model.enums.OrderDeliveryStatus;
import example.model.enums.OrderPaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDTO extends BaseDTO {

    private final   int x1 = 1;

    private PersonDTO buyer;
    private AddressDTO deliveryAddressDTO;
    private List<CategoryDTO.ProductDTO> productList = new ArrayList<>();
    private OrderPaymentStatus orderPaymentStatus;
    private OrderDeliveryStatus orderDeliveryStatus;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "dd-MM-yyyy hh:mm:ss"
    )
    private LocalDateTime orderTimestamp;

    public PersonDTO getBuyer() {
        return buyer;
    }

    public OrderDTO setBuyer(PersonDTO buyer) {
        this.buyer = buyer;
        return this;
    }

    public AddressDTO getDeliveryAddressDTO() {
        return deliveryAddressDTO;
    }

    public OrderDTO setDeliveryAddressDTO(AddressDTO deliveryAddressDTO) {
        this.deliveryAddressDTO = deliveryAddressDTO;
        return this;
    }

    public List<CategoryDTO.ProductDTO> getProductList() {
        return productList;
    }

    public OrderDTO setProductList(List<CategoryDTO.ProductDTO> productList) {
        this.productList = productList;
        return this;
    }

    public OrderPaymentStatus getOrderPaymentStatus() {
        return orderPaymentStatus;
    }

    public OrderDTO setOrderPaymentStatus(OrderPaymentStatus orderPaymentStatus) {
        this.orderPaymentStatus = orderPaymentStatus;
        return this;
    }

    public OrderDeliveryStatus getOrderDeliveryStatus() {
        return orderDeliveryStatus;
    }

    public OrderDTO setOrderDeliveryStatus(OrderDeliveryStatus orderDeliveryStatus) {
        this.orderDeliveryStatus = orderDeliveryStatus;
        return this;
    }

    public LocalDateTime getOrderTimestamp() {
        return orderTimestamp;
    }

    public OrderDTO setOrderTimestamp(LocalDateTime orderTimestamp) {
        this.orderTimestamp = orderTimestamp;
        return this;
    }
}
