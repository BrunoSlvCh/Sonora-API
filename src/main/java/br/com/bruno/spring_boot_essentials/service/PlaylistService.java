package br.com.bruno.spring_boot_essentials.service;

import br.com.bruno.spring_boot_essentials.dto.MidiaDto;
import br.com.bruno.spring_boot_essentials.dto.PlayListDto;
import br.com.bruno.spring_boot_essentials.exception.EventNotFoundException;
import br.com.bruno.spring_boot_essentials.exception.RequisicaoInvalidaException;
import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import br.com.bruno.spring_boot_essentials.model.PlaylistEntity;
import br.com.bruno.spring_boot_essentials.repository.MidiaRepository;
import br.com.bruno.spring_boot_essentials.repository.PlaylistRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PlaylistService {

    private final MidiaRepository midiaRepository;
    private final PlaylistRepository playlistRepository;

    private static PlaylistEntity playlistDtoParaEntity (PlayListDto playList){

        return PlaylistEntity.builder()
                .id(playList.getId())
                .nome(playList.getNome())
                .build();
    }

    private PlayListDto playListEnityparaDto (PlaylistEntity playlist){

        return PlayListDto.builder()
                .id(playlist.getId())
                .nome(playlist.getNome())
                .musicas(playlist.getMusicas())
                .build();
    }

    private static MidiaDto entityParaDto (MidiaEntity midia){

        return MidiaDto.builder()
                .id(midia.getId())
                .titulo(midia.getTitulo())
                .artista(midia.getArtista())
                .genero(midia.getGenero())
                .build();
    }

    @Transactional
    public PlayListDto criarPlaylist (PlayListDto novaPlaylist){
        PlaylistEntity playlist = playlistRepository.save(playlistDtoParaEntity(novaPlaylist));
        return PlayListDto.builder()
                .id(playlist.getId())
                .nome(playlist.getNome())
                .build();
    }

    @Transactional
    public String deletarPlaylist (Long idPlaylist) throws EventNotFoundException{
        PlaylistEntity playlist = playlistRepository.findById(idPlaylist).orElseThrow(() -> new EventNotFoundException("Playlist não encontrada."));
        playlistRepository.delete(playlist);
        return "mensagem: A Playlist foi deletada com sucesso.";
    }

    private boolean buscaExistencia (PlaylistEntity playlist, Long idMidia){
        return playlist.getMusicas().stream()
                .anyMatch(m -> m.getId().equals(idMidia));
    }

    @Transactional
    public String addMusica (Long idPlaylist, Long idMidia) throws EventNotFoundException, RequisicaoInvalidaException {

        PlaylistEntity playlist = playlistRepository.findById(idPlaylist).orElseThrow(() -> new EventNotFoundException("Playlist não encontrada."));

        MidiaEntity midia = midiaRepository.findById(idMidia).orElseThrow(() -> new EventNotFoundException("Música não econtrada."));

        MidiaDto midiaInfo = entityParaDto(midia);
        PlayListDto playListInfo = playListEnityparaDto(playlist);

      boolean busca = buscaExistencia(playlist, idMidia);

      if (busca){
          throw new RequisicaoInvalidaException("erro: Essa música já está nessa playlist.");
      }

      else {

          playlist.adcionarMidia(midia);
          playlistRepository.save(playlist);
      }
        return " mensagem: A Música "+ midiaInfo.getTitulo() + " adicionada à playlist " + playListInfo.getNome();
    }

    public List <PlayListDto> verPlaylsts () throws EventNotFoundException{

        List<PlaylistEntity> playlist = playlistRepository.findAll();

        if (playlist.isEmpty()){
            throw new EventNotFoundException("Nenhuma playlist foi criada.");
        }

        return playlist.stream()
                .map(this::playListEnityparaDto)
                .collect(Collectors.toList());
    }

    public PlayListDto buscarPlaylistNome (String nome) throws EventNotFoundException{

      PlaylistEntity playlist = playlistRepository.findByNome(nome).stream().findFirst().orElseThrow(() -> new EventNotFoundException ("Playlist não encontrada."));

      return playListEnityparaDto(playlist);
    }

    public PlayListDto buscarPorId (Long id) throws EventNotFoundException{

        PlaylistEntity playlist = playlistRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Playlist não encontrada."));

        return playListEnityparaDto(playlist);
    }

    @Transactional
    public String modificarNome (Long id, String nome) throws EventNotFoundException{
        PlaylistEntity playlist = playlistRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Playlist não encontrada."));

        playlist.setNome(nome);
        playlistRepository.save(playlist);
        return "'mensagem': O nome da playlist foi alterado para " + playlist.getNome() + ".";
    }

    @Transactional
    public PlayListDto removerMusicaPlaylist (Long idPlaylist, Long idMida) throws EventNotFoundException{

        PlaylistEntity playlist = playlistRepository.findById(idPlaylist).orElseThrow(() -> new EventNotFoundException("Playlist não encontrada."));

        MidiaEntity midia = midiaRepository.findById(idMida).orElseThrow(() -> new EventNotFoundException("Música não encontrada."));

        playlist.removerMidia(midia);
        playlistRepository.save(playlist);

        if (playlist.getMusicas().isEmpty()){
            throw new EventNotFoundException("Essa playlist está vazia.");
        }

        else {
            return playListEnityparaDto(playlist);
        }
    }
}
