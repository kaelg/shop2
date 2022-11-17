package pl.waw.great.shop.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "Comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String text;

    private int index;

    private LocalDateTime created;

    private LocalDateTime updated;

    @ManyToOne
    private Product product;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return Objects.equals(name, comment.name) && Objects.equals(email, comment.email) && Objects.equals(text, comment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, text);
    }
}
