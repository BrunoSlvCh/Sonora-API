package br.com.bruno.spring_boot_essentials.repository;

import br.com.bruno.spring_boot_essentials.model.MidiaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArquivoRepository extends JpaRepository <MidiaEntity, Long> {

}
