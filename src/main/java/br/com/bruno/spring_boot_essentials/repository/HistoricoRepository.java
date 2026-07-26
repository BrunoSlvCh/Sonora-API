package br.com.bruno.spring_boot_essentials.repository;

import br.com.bruno.spring_boot_essentials.model.HistoricoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface HistoricoRepository extends JpaRepository <HistoricoEntity, Long>{

    Long countByNomeMusicaIgnoreCase(String nomeMusica);

    @Query("SELECT h.nomeMusica FROM HistoricoEntity h GROUP BY h.nomeMusica ORDER BY COUNT(h.nomeMusica) DESC LIMIT 1")
    String findMusicaMaisTocada();
}
