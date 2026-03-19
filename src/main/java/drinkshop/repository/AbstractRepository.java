package drinkshop.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractRepository<I, E>
        implements Repository<I, E> {

    protected Map<I, E> entities = new HashMap<>();

    @Override
    public E findOne(I id) {
        return entities.get(id);
    }

    @Override
    public List<E> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public E save(E entity) {
        entities.put(getId(entity), entity);
        return entity;
    }

    @Override
    public E delete(I id) {
        return entities.remove(id);
    }

    @Override
    public E update(E entity) {
        return save(entity);
    }

    protected abstract I getId(E entity);
}
