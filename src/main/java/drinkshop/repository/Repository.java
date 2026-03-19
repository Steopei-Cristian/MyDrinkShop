package drinkshop.repository;

import java.util.List;

public interface Repository<I, E> {

    E findOne(I id);

    List<E> findAll();

    E save(E entity);

    E delete(I id);

    E update(E entity);
}
