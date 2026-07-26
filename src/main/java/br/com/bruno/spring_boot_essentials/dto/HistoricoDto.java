package br.com.bruno.spring_boot_essentials.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HistoricoDto {

    private Integer id;
    private String nomeMusica;
    private LocalDateTime dataReproduzida;
}