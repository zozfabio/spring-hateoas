package br.com.example.hateoas.view;

import br.com.example.hateoas.domain.Cidade;
import br.com.example.hateoas.domain.CidadeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static br.com.example.hateoas.domain.Cidade.estadoIdIgual;
import static br.com.example.hateoas.domain.Cidade.nomeContem;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.Specifications.where;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@ExposesResourceFor(Cidade.class)
@RequestMapping(value = "/cidades", produces = HAL_JSON_VALUE)
public class CidadeController extends AbstractController<Long, Cidade, CidadeRepository> {

    @Autowired
    public CidadeController(CidadeRepository repository, ConversionService conversionService, PagedResourcesAssembler<Cidade> pagedResourcesAssembler, CidadeResourceAssembler assembler) {
        super(repository, conversionService, pagedResourcesAssembler, assembler);
    }

    @RequestMapping(method = GET, produces = HAL_JSON_VALUE)
    @Override
    public ResponseEntity<PagedResources<Resource<Cidade>>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @RequestMapping(method = GET, value = "/byNomeContendo/{nome:.+}")
    public ResponseEntity<Resources<Resource<Cidade>>> findByNomeContendo(@PathVariable String nome) {
        return ok(new Resources<>(repository.findAll(where(nomeContem(nome))).stream()
                .map(assembler::toResource)
                .collect(toList())));
    }

    @RequestMapping(method = GET, value = "/byEstadoId/{estadoId}")
    public ResponseEntity<Resources<Resource<Cidade>>> findByEstadoId(@PathVariable Long estadoId) {
        return ok(new Resources<>(repository.findAll(where(estadoIdIgual(estadoId))).stream()
                .map(assembler::toResource)
                .collect(toList())));
    }

    @Component
    private static class CidadeResourceAssembler implements ResourceAssembler<Cidade, Resource<Cidade>> {

        @Autowired
        private EntityLinks links;

        @Override
        public Resource<Cidade> toResource(Cidade entity) {
            return new Resource<>(entity,
                    links.linkFor(Cidade.class, entity.getId()).withSelfRel(),
                    linkTo(methodOn(EstadoController.class).findByCidadeId(entity.getId())).withRel("estado"));
        }
    }
}
