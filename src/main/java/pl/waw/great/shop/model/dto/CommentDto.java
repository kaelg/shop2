package pl.waw.great.shop.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int index;
    @NotBlank(message = "{nameNotBlank}")
    private String name;

    @NotBlank(message = "{emailNotBlank}")
    private String email;

    @NotBlank(message = "{textNotBlank}")
    private String text;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CommentDto)) return false;
        CommentDto that = (CommentDto) o;
        return index == that.index && Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, name, email, text);
    }
}
