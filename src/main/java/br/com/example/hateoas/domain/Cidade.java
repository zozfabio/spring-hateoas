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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by zozfabio on 19/05/16.
 */
@Entity
@Value(staticConstructor = "of")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Relation(value = "cidade", collectionRelation = "cidades")
public class Cidade implements Serializable, Identifiable<Long> {

    private static final byte serialVersionUID = 1;

    @Id
    @GeneratedValue
    @NonFinal private Long id;

    @NotNull(message = "O campo nome é obrigatório!")
    @Size(min = 2, max = 100, message = "O campo nome deve ter entre {min} e {max} caracteres!")
    @Column(nullable = false, length = 100)
    @NonFinal private String nome;

    @JsonIgnore
    @NotNull(message = "O campo estado é obrigatório!")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @NonFinal private Estado estado;

    public static Specification<Cidade> nomeContem(String nome) {
        return (root, query, cb) -> cb.like(cb.lower(root.get(Cidade_.nome).as(String.class)), '%'+nome+'%');
    }

    public static Specification<Cidade> estadoIdIgual(Long estadoId) {
        return (root, query, cb) -> cb.equal(root.get(Cidade_.estado).get(Estado_.id), estadoId);
    }
}
