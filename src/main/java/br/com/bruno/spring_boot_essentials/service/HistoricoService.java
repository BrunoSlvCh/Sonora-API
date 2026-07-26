package br.com.bruno.spring_boot_essentials.service;

import br.com.bruno.spring_boot_essentials.dto.HistoricoDto;
import br.com.bruno.spring_boot_essentials.exception.EventNotFoundException;
import br.com.bruno.spring_boot_essentials.model.HistoricoEntity;
import br.com.bruno.spring_boot_essentials.repository.HistoricoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class HistoricoService {

    private final HistoricoRepository historicoRepository;

    private static List<HistoricoDto> listEntityParaDto(List<HistoricoEntity> listEntity) {
        return listEntity.stream()
                .map(entity -> HistoricoDto.builder()
                        .id(entity.getId())
                        .nomeMusica(entity.getNomeMusica())
                        .dataReproduzida(entity.getDataReproduzida())
                        .build())
                .toList();
    }

    public void contReproducao(String nomeArquivo) {
        HistoricoEntity historico = new HistoricoEntity(nomeArquivo, LocalDateTime.now());
        historicoRepository.save(historico);
    }

    public List<HistoricoDto> verHistorico() throws EventNotFoundException {
        List<HistoricoEntity> historico = historicoRepository.findAll();

        if (historico.isEmpty()) {
            throw new EventNotFoundException("Seu histórico está vazio.");
        }

        return listEntityParaDto(historico);
    }

    public String obterMaisTocada() {
        String maisTocada = historicoRepository.findMusicaMaisTocada();
        if (maisTocada == null) {
            return "Nenhuma música foi reproduzida ainda.";
        }
        Long qtd = historicoRepository.countByNomeMusicaIgnoreCase(maisTocada);
        return "A música mais tocada foi: " + maisTocada + " com " + qtd + " reprodução(ões).";
    }
}