package br.com.example.hateoas.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zozfabio on 19/05/16.
 */
//@RepositoryRestResource(path = "/estado", itemResourceRel = "estado", collectionResourceRel = "estados")
public interface EstadoRepository extends JpaRepository<Estado, Long>, JpaSpecificationExecutor<Estado> {
}
