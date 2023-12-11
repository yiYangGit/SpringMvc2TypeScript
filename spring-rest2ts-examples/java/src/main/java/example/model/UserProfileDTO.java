package example.model;

import example.model.core.BaseDTO;
import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import java.time.LocalDateTime;

public class UserProfileDTO extends BaseDTO {

    private String userLogin;

    @JacksonInject
    private LocalDateTime modifyTimestamp;

    @JsonUnwrapped
    private PersonDTO userData;

}
