package br.com.bruno.spring_boot_essentials.service;

import br.com.bruno.spring_boot_essentials.dto.MidiaDto;
import br.com.bruno.spring_boot_essentials.exception.EventNotFoundException;
import br.com.bruno.spring_boot_essentials.exception.RequisicaoInvalidaException;
import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import br.com.bruno.spring_boot_essentials.repository.MidiaRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class MidiaService {

    private final MidiaRepository midiaRepository;
    private final HistoricoService historicoService;
    private final Validator validator;

    private static final String DIRETORIO_UPLOAD = System.getProperty("user.dir") + "/uploads/";
    private static final List<String> MIME_TYPES_MP3 = List.of("audio/mpeg", "audio/mp3");

    private static MidiaDto entityParaDto(MidiaEntity midia) {

        return MidiaDto.builder()
                .id(midia.getId())
                .titulo(midia.getTitulo())
                .artista(midia.getArtista())
                .genero(midia.getGenero())
                .nomeArquivo(extrairNomeArquivo(midia.getCaminhoArquivo()))
                .build();
    }

    private static String extrairNomeArquivo(String caminhoArquivo) {
        if (caminhoArquivo == null) return null;
        return Paths.get(caminhoArquivo).getFileName().toString();
    }

    private static List<MidiaDto> listEntityParaDto(List<MidiaEntity> listEntity) {

        return listEntity.stream()
                .map(entity -> MidiaDto.builder()
                        .id(entity.getId())
                        .titulo(entity.getTitulo())
                        .artista(entity.getArtista())
                        .genero(entity.getGenero())
                        .nomeArquivo(extrairNomeArquivo(entity.getCaminhoArquivo()))
                        .build())
                .toList();
    }

    public List<MidiaDto> verMidias() {
        List<MidiaEntity> midias = midiaRepository.findAll();

        return listEntityParaDto(midias);
    }

    public MidiaDto buscarMidia(Long id) throws EventNotFoundException {
        MidiaEntity midia = midiaRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Música não encontrada."));

        return entityParaDto(midia);
    }

    public List<MidiaDto> buscarPorGenero(String genero) throws EventNotFoundException {

        List<MidiaEntity> list = midiaRepository.findByGeneroIgnoreCase(genero);

        if (list.isEmpty()) {
            throw new EventNotFoundException("Nenhuma correspondência encontrada para esse gênero.");
        } else {
            return listEntityParaDto(list);
        }
    }

    public MidiaDto buscarPorArtista(String nomeArtista) throws EventNotFoundException {

        MidiaEntity midia = midiaRepository.findByArtistaIgnoreCase(nomeArtista);

        if (midia != null) {
            return entityParaDto(midia);
        } else {
            throw new EventNotFoundException("Música não encontrada.");
        }
    }

    @Transactional
    public String deletarMidia(Long id) throws EventNotFoundException {
        MidiaEntity midia = midiaRepository.findById(id).orElseThrow(() -> new EventNotFoundException("Música não encontrada."));
        midiaRepository.delete(midia);
        return "Midia deletada com sucesso.";
    }

    @Transactional
    public String novaMidia(MidiaDto midia, MultipartFile arquivo) throws EventNotFoundException {

        Set<ConstraintViolation<MidiaDto>> violacoes = validator.validate(midia);
        if (!violacoes.isEmpty()) {
            throw new RequisicaoInvalidaException(violacoes.iterator().next().getMessage());
        }

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RequisicaoInvalidaException("Por favor, selecione um arquivo para enviar.");
        }

        String nomeArquivo = arquivo.getOriginalFilename();
        String contentType = arquivo.getContentType();

        boolean extensaoValida = nomeArquivo != null && nomeArquivo.toLowerCase().endsWith(".mp3");
        boolean tipoValido = contentType == null || MIME_TYPES_MP3.contains(contentType.toLowerCase());

        if (!extensaoValida || !tipoValido) {
            throw new RequisicaoInvalidaException("Apenas arquivos no formato MP3 são permitidos.");
        }

        try {

            File diretorio = new File(DIRETORIO_UPLOAD);
            if (!diretorio.exists()) {
                diretorio.mkdirs();
            }

            Path caminhoCompleto = Paths.get(DIRETORIO_UPLOAD, nomeArquivo);
            Files.write(caminhoCompleto, arquivo.getBytes());

            String caminhoString = caminhoCompleto.toString();

            MidiaEntity novaMidia = new MidiaEntity(
                    midia.getId(),
                    midia.getTitulo(),
                    midia.getArtista(),
                    midia.getGenero(),
                    caminhoString
            );

            midiaRepository.save(novaMidia);

            return "Arquivo enviado com sucesso: " + nomeArquivo;

        } catch (IOException e) {
            e.printStackTrace();
            return "Erro ao salvar o arquivo: " + e.getMessage();
        }
    }

    public ResponseEntity<Resource> reproduzirMidia(String nomeArquivo) throws FileNotFoundException, MalformedURLException {
        Path caminhoArquivo = Paths.get(DIRETORIO_UPLOAD).resolve(nomeArquivo).normalize();
        Resource resource = new UrlResource(caminhoArquivo.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + nomeArquivo);
        }

        historicoService.contReproducao(nomeArquivo);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/mpeg"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<Resource> baixarMidia(String nomeArquivo) throws FileNotFoundException, MalformedURLException {
        Path caminhoArquivo = Paths.get(DIRETORIO_UPLOAD).resolve(nomeArquivo).normalize();
        Resource resource = new UrlResource(caminhoArquivo.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new FileNotFoundException("Arquivo não encontrado: " + nomeArquivo);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @Transactional
    public MidiaDto atualizarParcial(Long id, MidiaDto midiaInfo) {

        MidiaEntity midia = midiaRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Mídia não encontrada"));

        if (midiaInfo.getTitulo() != null) {
            midia.setTitulo(midiaInfo.getTitulo());
        }

        if (midiaInfo.getArtista() != null) {
            midia.setArtista(midiaInfo.getArtista());
        }

        if (midiaInfo.getGenero() != null) {
            midia.setGenero(midiaInfo.getGenero());
        }
        return entityParaDto(midia);
    }

    @Transactional
    public MidiaDto alterarMidia(Long id, MidiaDto midiaInfo) {

        MidiaEntity midia = midiaRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException("Mídia não encontrada com o ID: " + id));

        if (midiaInfo.getId() == null){
            midiaInfo.setId(midia.getId());
        }

        midia.setTitulo(midiaInfo.getTitulo());
        midia.setArtista(midiaInfo.getArtista());
        midia.setGenero(midiaInfo.getGenero());

        return entityParaDto(midia);
    }
}
