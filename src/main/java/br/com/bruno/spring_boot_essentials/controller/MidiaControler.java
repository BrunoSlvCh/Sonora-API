package br.com.bruno.spring_boot_essentials.controller;

import br.com.bruno.spring_boot_essentials.dto.FavoritasDto;
import br.com.bruno.spring_boot_essentials.dto.HistoricoDto;
import br.com.bruno.spring_boot_essentials.dto.MidiaDto;
import br.com.bruno.spring_boot_essentials.dto.PlayListDto;
import br.com.bruno.spring_boot_essentials.exception.EventNotFoundException;
import br.com.bruno.spring_boot_essentials.service.FavoritasService;
import br.com.bruno.spring_boot_essentials.service.HistoricoService;
import br.com.bruno.spring_boot_essentials.service.MidiaService;
import br.com.bruno.spring_boot_essentials.service.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/midia")
public class MidiaControler {

    private final MidiaService midiaService;
    private final PlaylistService playlistService;
    private final FavoritasService favoritasService;
    private final HistoricoService historicoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadArquivo(
            @RequestParam("file") MultipartFile arquivo,
            @RequestParam("musica") String musicaJson) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();
        MidiaDto musica = objectMapper.readValue(musicaJson, MidiaDto.class);

        String resultado = midiaService.novaMidia(musica, arquivo);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/play/{nomeArquivo}")
    public ResponseEntity<Resource> reproduzirMusica (@PathVariable String nomeArquivo) throws MalformedURLException, FileNotFoundException {
        return midiaService.reproduzirMidia(nomeArquivo);
    }

    @GetMapping("/download/{nomeArquivo}")
    public ResponseEntity<Resource> baixarMusica (@PathVariable String nomeArquivo) throws MalformedURLException, FileNotFoundException {
        return midiaService.baixarMidia(nomeArquivo);
    }

    @PostMapping("/playlists")
    @ResponseStatus(HttpStatus.CREATED)
    public PlayListDto newPlaylist (@RequestBody PlayListDto novaPlaylist){
        return  playlistService.criarPlaylist(novaPlaylist);
    }

   @PostMapping("/playlists/{idPlaylist}/musicas/{idMusica}")
   @ResponseStatus(HttpStatus.CREATED)
   public ResponseEntity<PlayListDto.MensagemSucessoResponse> addMusica (@PathVariable Long idPlaylist, @PathVariable Long idMusica) throws  EventNotFoundException{
        String mensagem = playlistService.addMusica(idPlaylist, idMusica);
        return ResponseEntity.ok(new PlayListDto.MensagemSucessoResponse(mensagem));
   }

   @PostMapping ("/favoritar/{idMidia}")
   @ResponseStatus (HttpStatus.CREATED)
   public ResponseEntity<FavoritasDto.MensagemSucessoResponse> favoritarMusica (@PathVariable Long idMidia) throws EventNotFoundException{
        String mensagem = favoritasService.favoritar(idMidia);
        return ResponseEntity.ok(new FavoritasDto.MensagemSucessoResponse(mensagem));
   }

   @GetMapping ("/favoritas/all")
   @ResponseStatus(HttpStatus.OK)
   public List <FavoritasDto> verFavoritas () throws EventNotFoundException{
        return favoritasService.verFavoritas();
   }

   @GetMapping("/favoritas/{idMidia}")
   @ResponseStatus(HttpStatus.OK)
   public MidiaDto bsucarFavoriataId (@PathVariable Long idMidia) throws EventNotFoundException{
        return favoritasService.favoritasId(idMidia);
   }

   @GetMapping ("/favoritas/nome/{titulo}")
   @ResponseStatus (HttpStatus.OK)
   public MidiaDto buscarFavoritaTitulo (@PathVariable String titulo) throws EventNotFoundException{
        return favoritasService.bsucarPorTitulo(titulo);
   }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<MidiaDto> verMidias (){
        return midiaService.verMidias();
    }

    @GetMapping ("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public MidiaDto buscarMidia (@PathVariable Long id) throws EventNotFoundException {
        return midiaService.buscarMidia(id);
    }

    @GetMapping ("/musicas/genero/{genero}")
    @ResponseStatus(HttpStatus.OK)
    public List<MidiaDto> buscarPorGenero (@PathVariable String genero) throws EventNotFoundException{
        return midiaService.buscarPorGenero(genero);
    }

    @GetMapping("/musicas/artista/{artista}")
    @ResponseStatus(HttpStatus.OK)
    public MidiaDto buscarPorArtista (@PathVariable String artista) throws EventNotFoundException{
        return midiaService.buscarPorArtista(artista);
    }

    @GetMapping("/playlists/all")
    @ResponseStatus(HttpStatus.OK)
    public List <PlayListDto> verPlaylists () throws EventNotFoundException {
       return playlistService.verPlaylsts();
    }

    @GetMapping("/playlists/id/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PlayListDto buscarPorId (@PathVariable Long id) throws EventNotFoundException{
       return playlistService.buscarPorId(id);
    }

    @GetMapping("/playlists/nome/{nome}")
    @ResponseStatus(HttpStatus.OK)
    public PlayListDto buscarPorNome (@PathVariable String nome) throws EventNotFoundException{
        return playlistService.buscarPlaylistNome(nome);
    }

    @GetMapping("/historico")
    @ResponseStatus(HttpStatus.OK)
    public List <HistoricoDto> verHistorico() throws EventNotFoundException{
        return historicoService.verHistorico();
    }

    @GetMapping("/historico/maistocadas")
    @ResponseStatus(HttpStatus.OK)
    public String maisTocada(){
        return historicoService.obterMaisTocada();
    }

    @PatchMapping("/musicas/modificar/{id}")
    public MidiaDto modificarMidia (@PathVariable Long id, @RequestBody MidiaDto midiaInfo){
        return midiaService.atualizarParcial(id, midiaInfo);
    }

    @PatchMapping("/playlists/novonome/{id}/{novoNome}")
    @ResponseStatus (HttpStatus.OK)
    public ResponseEntity<PlayListDto.MensagemSucessoResponse> modificarNomePlaylist (@PathVariable Long id, @PathVariable String novoNome) throws EventNotFoundException{
        String mensagem = playlistService.modificarNome(id, novoNome);
        return ResponseEntity.ok(new PlayListDto.MensagemSucessoResponse(mensagem));
    }

    @PutMapping("musicas/alterar/{id}")
    public MidiaDto alterarMidia (@PathVariable Long id, @RequestBody MidiaDto midiaInfo){
        return midiaService.alterarMidia(id, midiaInfo);
    }

    @DeleteMapping("/playlists/{idPlaylist}/musicas/{idMidia}")
    @ResponseStatus(HttpStatus.OK)
    public PlayListDto removerMusicaPlaylist (@PathVariable Long idPlaylist, @PathVariable Long idMidia) throws EventNotFoundException{
        return playlistService.removerMusicaPlaylist(idPlaylist, idMidia);
    }

    @DeleteMapping("/playlists/{idPlaylist}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<PlayListDto.MensagemSucessoResponse>
    deletarPlaylist (@PathVariable Long idPlaylist) throws EventNotFoundException {
        String mesagem = playlistService.deletarPlaylist(idPlaylist);
        return ResponseEntity.ok(new PlayListDto.MensagemSucessoResponse(mesagem));
    }

    @DeleteMapping("/musicas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MidiaDto.MensagemSucessoResponse>
    deletarMidia (@PathVariable Long id) throws EventNotFoundException{

        String mesagem = midiaService.deletarMidia(id);
        return ResponseEntity.ok(new MidiaDto.MensagemSucessoResponse(mesagem));
    }

    @DeleteMapping("/favoritas/remove/{idMidia}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<FavoritasDto.MensagemSucessoResponse>
    desfavoritar (@PathVariable Long idMidia) throws EventNotFoundException{
        String mensagem = favoritasService.desfavoritar(idMidia);
        return ResponseEntity.ok(new FavoritasDto.MensagemSucessoResponse(mensagem));
    }
}
