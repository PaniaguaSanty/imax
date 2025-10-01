package com.cinema.imax_catalog_service.client;

public interface MapperMethods<DTO, Entity> {

    Entity convertToEntity(DTO dto);

    DTO convertToDto(Entity entity);
}
