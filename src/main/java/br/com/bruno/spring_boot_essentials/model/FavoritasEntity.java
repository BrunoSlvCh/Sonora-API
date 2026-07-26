package br.com.bruno.spring_boot_essentials.model;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity

@Table (name = "favoritas")

public class FavoritasEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @ElementCollection
    private List <MidiaEntity> favoritas;

    public void adcionarFavorita (MidiaEntity favorita){
        this.favoritas.add(favorita);
    }

    public void removerFavoritas (MidiaEntity favorita){
        this.favoritas.remove(favorita);
    }
}
