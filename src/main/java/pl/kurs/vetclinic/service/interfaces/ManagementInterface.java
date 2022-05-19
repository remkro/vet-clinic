package pl.kurs.vetclinic.service.interfaces;

import pl.kurs.vetclinic.error.exception.NoEntityException;
import pl.kurs.vetclinic.error.exception.WrongIdException;

import java.util.List;

public interface ManagementInterface<T, S> {

    T add(T entity) throws NoEntityException, WrongIdException;

    void delete(S id) throws WrongIdException, NoEntityException;

    T edit(T entity) throws NoEntityException, WrongIdException;

    T show(S id) throws NoEntityException, WrongIdException;

    List<T> showAll();

}
