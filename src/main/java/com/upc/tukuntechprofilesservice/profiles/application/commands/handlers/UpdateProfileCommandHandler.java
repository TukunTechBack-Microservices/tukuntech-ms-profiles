package com.upc.tukuntechprofilesservice.profiles.application.commands.handlers;


import com.upc.tukuntechprofilesservice.profiles.application.commands.UpdateProfileCommand;
import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.mapper.ProfileMapper;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.ProfileType;
import com.upc.tukuntechprofilesservice.profiles.domain.repositories.UserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class UpdateProfileCommandHandler {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;

    public UpdateProfileCommandHandler(UserProfileRepository repository,
                                       ProfileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public UserProfileResponse handle(UpdateProfileCommand cmd) {

        var existing = repository.findByUserId(cmd.userId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile not found"
                ));

        existing.setFirstName(cmd.firstName());
        existing.setLastName(cmd.lastName());
        existing.setDni(cmd.dni());
        existing.setAge(cmd.age());
        existing.setGender(cmd.gender());
        existing.setNationality(cmd.nationality());

        if ("PATIENT".equalsIgnoreCase(cmd.role())) {
            existing.setBloodGroup(cmd.bloodGroup());
            existing.setAllergy(cmd.allergy());
            existing.setProfileType(ProfileType.PATIENT);
        } else {
            existing.setBloodGroup(null);
            existing.setAllergy(null);
            existing.setProfileType(ProfileType.ATTENDANT);
        }

        var updated = repository.save(existing);
        return mapper.toResponse(updated);
    }
}