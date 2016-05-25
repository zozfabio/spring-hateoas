package br.com.example.hateoas.view;

import br.com.example.hateoas.domain.EntityNotFoundException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.nonNull;
import static org.springframework.hateoas.MediaTypes.HAL_JSON_VALUE;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.http.ResponseEntity.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

public abstract class AbstractController<ID extends Serializable, T extends Identifiable<ID>, R extends JpaRepository<T, ID>> {

    protected final R repository;

    protected final ConversionService conversionService;

    protected final PagedResourcesAssembler<T> pagedResourcesAssembler;

    protected final ResourceAssembler<T, Resource<T>> assembler;

    AbstractController(R repository, ConversionService conversionService, PagedResourcesAssembler<T> pagedResourcesAssembler, ResourceAssembler<T, Resource<T>> assembler) {
        this.repository = repository;
        this.conversionService = conversionService;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.assembler = assembler;
    }

    @RequestMapping(method = GET, produces = HAL_JSON_VALUE)
    public ResponseEntity<PagedResources<Resource<T>>> findAll(Pageable pageable) {
        return ok(pagedResourcesAssembler.toResource(repository.findAll(pageable), assembler));
    }

    @RequestMapping(method = GET, value = "/{id}", produces = HAL_JSON_VALUE)
    public ResponseEntity<Resource<T>> findOne(@PathVariable ID id) {
        T entity = repository.findOne(id);
        if (nonNull(entity)) return ok(assembler.toResource(entity));
        throw new EntityNotFoundException(String.format("Entidade com id %s não encontrada!", id));
    }

    @RequestMapping(method = POST, consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> insert(@RequestBody T entity) {
        entity = repository.save(entity);
        return created(linkTo(methodOn(getClass()).findOne(entity.getId())).toUri()).build();
    }

    @RequestMapping(method = PUT, value = "/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> edit(@PathVariable ID id, @RequestBody T newEntity) {
        T oldEntity = repository.findOne(id);

        BeanWrapper target = new BeanWrapperImpl(oldEntity);
        target.setConversionService(conversionService);

        BeanWrapper source = new BeanWrapperImpl(newEntity);
        source.setConversionService(conversionService);

        for (PropertyDescriptor pd : source.getPropertyDescriptors()) {
            String name = pd.getName();
            if (!"id".equals(name))
                target.setPropertyValue(name, source.getPropertyValue(name));
        }

        repository.save(oldEntity);

        return noContent().build();
    }

    @RequestMapping(method = PATCH, value = "/{id}", consumes = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> editPartial(@PathVariable ID id, @RequestBody Map<String, String> newEntity) {
        T oldEntity = repository.findOne(id);

        BeanWrapper wrapper = new BeanWrapperImpl(oldEntity);
        wrapper.setConversionService(conversionService);

        newEntity.forEach(wrapper::setPropertyValue);

        repository.save(oldEntity);

        return noContent().build();
    }

    @RequestMapping(method = DELETE, value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id) {
        repository.delete(id);
        return noContent().build();
    }
}
