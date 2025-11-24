package com.upc.tukuntechprofilesservice.profiles.application.commands.handlers;


import com.upc.tukuntechprofilesservice.profiles.application.commands.CreateProfileCommand;
import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.mapper.ProfileMapper;
import com.upc.tukuntechprofilesservice.profiles.domain.entity.UserProfile;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.ProfileType;
import com.upc.tukuntechprofilesservice.profiles.domain.repositories.UserProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class CreateProfileCommandHandler {

    private final UserProfileRepository repository;
    private final ProfileMapper mapper;

    public CreateProfileCommandHandler(UserProfileRepository repository,
                                       ProfileMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public UserProfileResponse handle(CreateProfileCommand cmd) {

        repository.findByUserId(cmd.userId()).ifPresent(p -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Profile already exists for this user"
            );
        });

        repository.findByDni(cmd.dni()).ifPresent(p -> {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Profile already exists for this DNI"
            );
        });

        var profile = new UserProfile();
        profile.setUserId(cmd.userId());
        profile.setFirstName(cmd.firstName());
        profile.setLastName(cmd.lastName());
        profile.setDni(cmd.dni());
        profile.setAge(cmd.age());
        profile.setGender(cmd.gender());
        profile.setNationality(cmd.nationality());

        if ("PATIENT".equalsIgnoreCase(cmd.role())) {
            profile.setProfileType(ProfileType.PATIENT);
            profile.setBloodGroup(cmd.bloodGroup());
            profile.setAllergy(cmd.allergy());
        } else {
            profile.setProfileType(ProfileType.ATTENDANT);
            profile.setBloodGroup(null);
            profile.setAllergy(null);
        }

        var saved = repository.save(profile);
        return mapper.toResponse(saved);
    }
}
