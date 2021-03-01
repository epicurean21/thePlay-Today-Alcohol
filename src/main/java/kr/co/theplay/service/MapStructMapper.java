package kr.co.theplay.service;

public interface MapStructMapper <D, E> {
    E toEntity(D dto);
    D toDto(E entity);
}
