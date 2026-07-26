package br.com.bruno.spring_boot_essentials.repository;

import br.com.bruno.spring_boot_essentials.model.PlaylistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlaylistRepository extends JpaRepository <PlaylistEntity, Long> {

    Optional<PlaylistEntity> findByNome (String nome);
}
