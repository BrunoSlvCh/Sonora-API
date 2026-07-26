package br.com.bruno.spring_boot_essentials.repository;

import br.com.bruno.spring_boot_essentials.model.FavoritasEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritasRepository extends JpaRepository <FavoritasEntity, Long> {
    Optional<FavoritasEntity> findByTitulo (String titulo);
}
