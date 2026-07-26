package br.com.bruno.spring_boot_essentials.repository;

import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MidiaRepository extends JpaRepository <MidiaEntity, Long> {

    List <MidiaEntity> findByGeneroIgnoreCase (String genero);
     MidiaEntity findByArtistaIgnoreCase (String artista);
     MidiaEntity findByTitulo (String titulo);
}

