package pl.kurs.vetclinic.service;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;
import pl.kurs.vetclinic.model.interfaces.Identificationable;
import pl.kurs.vetclinic.service.interfaces.ManagementInterface;

import java.util.List;
import java.util.Optional;

public class GenericManagementService<T extends Identificationable<S>,
        U extends JpaRepository<T, S>, S> implements ManagementInterface<T, S> {

    protected U repository;

    public GenericManagementService(U repository) {
        this.repository = repository;
    }

    @Transactional
    @Override
    public T add(T entity) throws NoEntityException, WrongIdException {
        if (entity == null)
            throw new NoEntityException("NO_ENTITY_TO_ADD");
        if (entity.getId() != null)
            throw new WrongIdException("ID_MUST_BE_NULL");
        return repository.saveAndFlush(entity);
    }

    @Transactional
    @Override
    public void delete(S id) {
        if (id == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NoEntityException("NO_ENTITY_FOUND");
        }
    }

    @Transactional
    @Override
    public T edit(T entity) {
        if (entity == null)
            throw new NoEntityException("NO_ENTITY_FOUND");
        if (entity.getId() == null)
            throw new WrongIdException("ID_CANNOT_BE_NULL");
        return repository.saveAndFlush(entity);
    }

    @Transactional(readOnly = true)
    @Override
    public T show(S id) {
        return repository
                .findById(Optional.ofNullable(id).orElseThrow(() -> new WrongIdException("ID_CANNOT_BE_NULL")))
                .orElseThrow(() -> new NoEntityException("NO_ENTITY_FOUND"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<T> showAll() {
        return repository.findAll();
    }

}
