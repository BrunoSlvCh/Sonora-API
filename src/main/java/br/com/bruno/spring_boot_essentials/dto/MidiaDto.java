package br.com.bruno.spring_boot_essentials.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString

public class MidiaDto {

    private Long id;

    @NotBlank(message = "O titulo é obrigatótio.")
    private String titulo;

    @NotBlank (message = "O autor é obrigatório.")
    private String artista;

    @NotBlank(message = "O genero é obrigatório.")
    private String genero;

    // Nome do arquivo salvo em disco — usado pelo front para montar as URLs
    // de /v1/midia/play/{nomeArquivo} e /v1/midia/download/{nomeArquivo}.
    // Não precisa ser enviado no upload: é preenchido pelo backend na resposta.
    private String nomeArquivo;

    public record MensagemSucessoResponse(String mesagem){}
}
