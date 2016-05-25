package br.com.example.hateoas.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by zozfabio on 19/05/16.
 */
//@RepositoryRestResource(path = "/cidade", itemResourceRel = "cidade", collectionResourceRel = "cidades")
public interface CidadeRepository extends JpaRepository<Cidade, Long>, JpaSpecificationExecutor<Cidade> {
}
