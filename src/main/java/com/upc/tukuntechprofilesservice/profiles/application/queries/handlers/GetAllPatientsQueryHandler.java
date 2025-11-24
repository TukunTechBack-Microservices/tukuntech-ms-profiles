package com.upc.tukuntechprofilesservice.profiles.application.queries.handlers;


import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.mapper.ProfileMapper;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.ProfileType;
import com.upc.tukuntechprofilesservice.profiles.domain.repositories.UserProfileRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllPatientsQueryHandler {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;

    public GetAllPatientsQueryHandler(UserProfileRepository repository,
                                      ProfileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<UserProfileResponse> handle() {
        return repository.findByProfileType(ProfileType.PATIENT)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }
}

