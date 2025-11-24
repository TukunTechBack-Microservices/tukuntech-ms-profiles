package com.upc.tukuntechprofilesservice.profiles.application.queries.handlers;


import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.mapper.ProfileMapper;
import com.upc.tukuntechprofilesservice.profiles.application.queries.GetPatientsByFiltersQuery;
import com.upc.tukuntechprofilesservice.profiles.domain.entity.UserProfile;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.ProfileType;
import com.upc.tukuntechprofilesservice.profiles.domain.repositories.UserProfileRepository;
import com.upc.tukuntechprofilesservice.profiles.domain.specifications.UserProfileSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetPatientsByFiltersQueryHandler {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;

    public GetPatientsByFiltersQueryHandler(UserProfileRepository repository, ProfileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserProfileResponse> handle(GetPatientsByFiltersQuery query) {

        // 🔹 Iniciar filtro base: solo perfiles de tipo PACIENTE
        Specification<UserProfile> spec = (root, q, cb) ->
                cb.equal(root.get("profileType"), ProfileType.PATIENT);

        // 🔹 Si hay nombre, aplicar búsqueda parcial en nombre o apellido
        if (query.name() != null && !query.name().isBlank()) {
            spec = spec.and(
                    UserProfileSpecifications.hasFirstNameContaining(query.name())
                            .or(UserProfileSpecifications.hasLastNameContaining(query.name()))
            );
        }

        // 🔹 Ejecutar búsqueda
        return repository.findAll(spec)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}
