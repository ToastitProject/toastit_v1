package alcoholboot.toastit.feature.calendarcocktail.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
@Getter
public class RecommendByDate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}