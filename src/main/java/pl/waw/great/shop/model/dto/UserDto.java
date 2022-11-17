package pl.waw.great.shop.model.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "{nameNotBlank}")
    private String name;

    public UserDto() {
    }

    public UserDto(String name) {
        this.name = name;
    }

}
