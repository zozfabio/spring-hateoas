package br.com.example.hateoas.view;

import br.com.example.hateoas.domain.EntityNotFoundException;
import br.com.example.hateoas.domain.Estado;
import br.com.example.hateoas.domain.EstadoRepository;
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

import static br.com.example.hateoas.domain.Estado.*;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.Specifications.where;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@ExposesResourceFor(Estado.class)
@RequestMapping(value = "/estados")
public class EstadoController extends AbstractController<Long, Estado, EstadoRepository> {

    @Autowired
    public EstadoController(EstadoRepository repository, ConversionService conversionService, PagedResourcesAssembler<Estado> pagedResourcesAssembler, EstadoResourceAssembler assembler) {
        super(repository, conversionService, pagedResourcesAssembler, assembler);
    }

    @RequestMapping(method = GET, produces = HAL_JSON_VALUE)
    @Override
    public ResponseEntity<PagedResources<Resource<Estado>>> findAll(Pageable pageable) {
        return super.findAll(pageable);
    }

    @RequestMapping(method = GET, value = "/byUf/{uf:\\w{2}}", produces = HAL_JSON_VALUE)
    public ResponseEntity<Resource<Estado>> findByUf(@PathVariable String uf) {
        Estado estado = repository.findOne(where(ufIgual(uf)));
        if (nonNull(estado)) return ok(assembler.toResource(estado));
        throw new EntityNotFoundException(String.format("Estado com uf %s não encontrado!", uf));
    }

    @RequestMapping(method = GET, value = "/byNomeContendo/{nome:.+}", produces = HAL_JSON_VALUE)
    public ResponseEntity<Resources<Resource<Estado>>> findByNomeContendo(@PathVariable String nome) {
        return ok(new Resources<>(repository.findAll(where(nomeContem(nome))).stream()
                .map(assembler::toResource)
                .collect(toList())));
    }

    @RequestMapping(method = GET, value = "/byCidadeId/{cidadeId}", produces = HAL_JSON_VALUE)
    public ResponseEntity<Resource<Estado>> findByCidadeId(@PathVariable Long cidadeId) {
        Estado estado = repository.findOne(where(cidadeIdIgual(cidadeId)));
        if (nonNull(estado)) return ok(assembler.toResource(estado));
        throw new EntityNotFoundException(String.format("Estado com cidade %s não encontrado!", cidadeId));
    }

    @Component
    private static class EstadoResourceAssembler implements ResourceAssembler<Estado, Resource<Estado>> {

        @Autowired
        private EntityLinks links;

        @Override
        public Resource<Estado> toResource(Estado entity) {
            return new Resource<>(entity,
                    links.linkFor(Estado.class, entity.getId()).withSelfRel(),
                    linkTo(methodOn(CidadeController.class).findByEstadoId(entity.getId())).withRel("cidades"));
        }
    }
}
