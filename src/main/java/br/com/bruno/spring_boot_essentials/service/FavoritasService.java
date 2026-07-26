package br.com.bruno.spring_boot_essentials.service;

import br.com.bruno.spring_boot_essentials.dto.FavoritasDto;
import br.com.bruno.spring_boot_essentials.dto.MidiaDto;
import br.com.bruno.spring_boot_essentials.exception.ElemetoDuplicadoException;
import br.com.bruno.spring_boot_essentials.exception.EventNotFoundException;
import br.com.bruno.spring_boot_essentials.model.FavoritasEntity;
import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import br.com.bruno.spring_boot_essentials.repository.FavoritasRepository;
import br.com.bruno.spring_boot_essentials.repository.MidiaRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor

public class FavoritasService {

    public final MidiaRepository midiaRepository;
    public final FavoritasRepository favoritasRepository;

    public List<FavoritasDto> favoritasEntityParaDto(List<FavoritasEntity> favoritas) {
        return favoritas.stream()
                .map(fav -> FavoritasDto.builder()
                        .id(fav.getId())
                        .titulo(fav.getTitulo())
                        .favoritas(fav.getFavoritas())
                        .build())
                .toList();
    }

    private boolean buscaEqualidade(FavoritasEntity playlist, Long idMidia) {

        if (playlist == null || playlist.getFavoritas() == null) {
            return false;
        }

        boolean jaExiste = playlist.getFavoritas().stream().anyMatch(m -> Objects.equals(m.getId(), idMidia));

        if (jaExiste) {
            throw new ElemetoDuplicadoException("'mensagem': Essa música já está favoritada.");
        }

        return false;
    }

    @Transactional
    public String favoritar(Long id) throws EventNotFoundException, ElemetoDuplicadoException {

        MidiaEntity midia = midiaRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Música não encontrada."));

        FavoritasEntity favorita = favoritasRepository.findAll().stream()
                .findFirst()
                .orElseGet(() -> FavoritasEntity.builder()
                        .titulo("Minhas Músicas Favoritas")
                        .favoritas(new ArrayList<>())
                        .build());

        buscaEqualidade(favorita, id);

        favorita.adcionarFavorita(midia);
        favoritasRepository.save(favorita);

        return "mensagem: " + midia.getTitulo() + " adcionada as favoritas.";
    }

    private static MidiaDto entityParaDto (MidiaEntity midia){

        return MidiaDto.builder()
                .id(midia.getId())
                .titulo(midia.getTitulo())
                .artista(midia.getArtista())
                .genero(midia.getGenero())
                .build();
    }

    public List<FavoritasDto> verFavoritas() throws EventNotFoundException {

        List<FavoritasEntity> favoritas = favoritasRepository.findAll();

        if (favoritas.isEmpty()) {
            throw new EventNotFoundException("Nenhuma musica favoritada.");
        }

        List<FavoritasDto> dtos = favoritasEntityParaDto(favoritas);
        return dtos;
    }

    public MidiaDto favoritasId(Long idMidia) throws EventNotFoundException {

        FavoritasEntity favorita = favoritasRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException("Nenhuma playlist de favoritas encontrada."));

        boolean favoritada = favorita.getFavoritas().stream()
                .anyMatch(m -> Objects.equals(m.getId(), idMidia));

        if (favoritada) {
            MidiaEntity musicaBuscada = midiaRepository.findById(idMidia)
                    .orElseThrow(() -> new EventNotFoundException("Mídia não encontrada no cadastro geral."));

            return MidiaDto.builder()
                    .id(musicaBuscada.getId())
                    .titulo(musicaBuscada.getTitulo())
                    .artista(musicaBuscada.getArtista())
                    .genero(musicaBuscada.getGenero())
                    .build();
        }

        else {
            throw new EventNotFoundException("Essa música não está na sua lista de favoritas.");
        }
    }

    @Transactional
    public String desfavoritar(Long id) throws EventNotFoundException {

        MidiaEntity midia = midiaRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Música não encontrada no cadastro."));

        FavoritasEntity favorita = favoritasRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new EventNotFoundException("Sua lista de favoritas ainda não foi criada."));

        boolean estaNaLista = favorita.getFavoritas().stream()
                .anyMatch(m -> Objects.equals(m.getId(), id));

        if (!estaNaLista) {
            throw new EventNotFoundException("Essa música não está na sua playlist de favoritas.");
        }

        favorita.removerFavoritas(midia);
        favoritasRepository.save(favorita);

        return "mensagem: A música " + midia.getTitulo() + " foi removida das suas favoritas.";
    }

    public MidiaDto bsucarPorTitulo (String titulo) throws EventNotFoundException{

        FavoritasEntity favoritas = favoritasRepository.findAll().stream()
                .findFirst().orElseThrow(() -> new EventNotFoundException("Playlist de favoritas não encontrada"));

        boolean existe = favoritas.getFavoritas().stream().anyMatch(m -> Objects.equals(m.getTitulo(), titulo));

        if (existe){
            MidiaEntity midia = midiaRepository.findByTitulo(titulo);
            return entityParaDto(midia);
        }
        
        else {
            throw new EventNotFoundException("Música não encontrada");
        }
    }
}
