package br.com.bruno.spring_boot_essentials.dto;

import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class FavoritasDto {

    private Long id;

    private String titulo;

    private List<MidiaEntity> musicas;

    private List <MidiaEntity> favoritas;

    public record MensagemSucessoResponse(String mensagem) {}
}
