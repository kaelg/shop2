package pl.waw.great.shop.model.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDto {

    @NotBlank(message = "{titleNotBlank}")
    private String title;
    @NotBlank(message = "{textNotBlank}")
    private String body;
    @NotBlank(message = "{cityNotBlank}")
    private String city;
    @NotBlank(message = "{emailNotBlank}")
    private String email;



}
