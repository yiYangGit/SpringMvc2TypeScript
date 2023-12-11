package example.biz;

import example.dev.anno.MetaFiledInfo;
import example.model.PersonDTO;
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

    public UserProfileDTO() {
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public LocalDateTime getModifyTimestamp() {
        return modifyTimestamp;
    }

    public void setModifyTimestamp(LocalDateTime modifyTimestamp) {
        this.modifyTimestamp = modifyTimestamp;
    }

    public PersonDTO getUserData() {
        return userData;
    }

    public void setUserData(PersonDTO userData) {
        this.userData = userData;
    }
}
