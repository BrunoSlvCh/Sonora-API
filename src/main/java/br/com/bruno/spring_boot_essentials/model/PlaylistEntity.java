package br.com.bruno.spring_boot_essentials.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity

@Table (name = "playlist")

public class PlaylistEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String nome;

    @ElementCollection
    private List <MidiaEntity> musicas;

    @ElementCollection
    private List <MidiaEntity> favoritas;

    public void adcionarMidia (MidiaEntity midia){
        this.musicas.add(midia);
    }

    public void removerMidia (MidiaEntity midia){
        this.musicas.remove(midia);
    }
}
