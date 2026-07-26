package br.com.bruno.spring_boot_essentials.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table (name = "midia")

public class MidiaEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String artista;

    @Column (nullable = false)
    private String genero;

    @Column
    private String caminhoArquivo;
}
