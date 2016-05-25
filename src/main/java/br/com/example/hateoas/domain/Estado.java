package br.com.example.hateoas.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.core.Relation;

import javax.persistence.*;
import javax.persistence.criteria.Join;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;

/**
 * Created by zozfabio on 19/05/16.
 */
@Entity
@Value(staticConstructor = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Relation(value = "estado", collectionRelation = "estados")
public class Estado implements Serializable, Identifiable<Long> {

    private static final byte serialVersionUID = 1;

    @Id
    @GeneratedValue
    @NonFinal private Long id;

    @NotNull(message = "O campo UF é obrigatório!")
    @Size(min = 2, max = 2, message = "O campo UF deve ter {min} caracteres!")
    @Column(unique = true, nullable = false, length = 2)
    @NonFinal private String uf;

    @NotNull(message = "O campo nome é obrigatório!")
    @Size(min = 2, max = 100, message = "O campo nome deve ter entre {min} e {max} caracteres!")
    @Column(nullable = false, length = 100)
    @NonFinal private String nome;

    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    @NonFinal private List<Cidade> cidades = newLinkedList();

    public static Specification<Estado> nomeContem(String nome) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Estado_.nome).as(String.class)), '%'+nome.toLowerCase()+'%');
    }

    public static Specification<Estado> ufIgual(String uf) {
        return (root, query, cb) -> cb.equal(root.get(Estado_.uf), uf);
    }

    public static Specification<Estado> cidadeIdIgual(Long cidadeId) {
        return (root, query, cb) -> {
            Join<Estado, Cidade> cidadesJoin = root.join(Estado_.cidades);
            return cb.equal(cidadesJoin.get(Cidade_.id), cidadeId);
        };
    }
}
