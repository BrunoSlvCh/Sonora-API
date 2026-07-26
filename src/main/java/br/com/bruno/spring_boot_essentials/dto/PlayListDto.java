package br.com.bruno.spring_boot_essentials.dto;

import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class PlayListDto {

    private Long id;

    @NotBlank(message = "O nome da playlist é obrigatório.")
    private String nome;

    private List <MidiaEntity> musicas;

    private List <MidiaEntity> favoritas;

    public record MensagemSucessoResponse(String mensagem) {}
}
