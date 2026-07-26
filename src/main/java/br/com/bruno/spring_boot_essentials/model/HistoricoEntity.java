package br.com.bruno.spring_boot_essentials.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "histórico de reproduções")

@Entity
public class HistoricoEntity {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nomeMusica;

    private LocalDateTime dataReproduzida;

    public HistoricoEntity(String nomeMusica, LocalDateTime dataReproduzida) {
        this.nomeMusica = nomeMusica;
        this.dataReproduzida = dataReproduzida;
    }
}