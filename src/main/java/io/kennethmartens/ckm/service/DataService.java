package io.kennethmartens.ckm.service;

import io.smallrye.mutiny.Uni;

import java.util.List;

public interface DataService<TObjectType> {
    public Uni<List<TObjectType>> get();
    public Uni<TObjectType> findById(String id);
    public Uni<TObjectType> persist(TObjectType gallery);
    public Uni<TObjectType> update(String id, TObjectType gallery);
    public Uni<Object> delete(String id);
}
