package com.upc.tukuntechprofilesservice.profiles.application.facade;


import com.upc.tukuntechprofilesservice.profiles.application.commands.CreateProfileCommand;
import com.upc.tukuntechprofilesservice.profiles.application.commands.DeleteProfileByDniCommand;
import com.upc.tukuntechprofilesservice.profiles.application.commands.UpdateProfileCommand;
import com.upc.tukuntechprofilesservice.profiles.application.commands.handlers.CreateProfileCommandHandler;
import com.upc.tukuntechprofilesservice.profiles.application.commands.handlers.DeleteProfileByDniCommandHandler;
import com.upc.tukuntechprofilesservice.profiles.application.commands.handlers.UpdateProfileCommandHandler;
import com.upc.tukuntechprofilesservice.profiles.application.dto.CreateProfileRequest;
import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.queries.GetAttendantsByFiltersQuery;
import com.upc.tukuntechprofilesservice.profiles.application.queries.GetPatientsByFiltersQuery;
import com.upc.tukuntechprofilesservice.profiles.application.queries.handlers.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProfileApplicationFacade {

    private final CreateProfileCommandHandler createHandler;
    private final UpdateProfileCommandHandler updateHandler;
    private final GetProfileByUserIdQueryHandler getByUserIdHandler;
    private final GetAllPatientsQueryHandler getAllPatientsHandler;
    private final GetAllAttendantsQueryHandler getAllAttendantsHandler;
    private final GetProfileByIdQueryHandler getByIdHandler;
    private final GetProfileByDniQueryHandler getByDniHandler;
    private final DeleteProfileByDniCommandHandler deleteByDniHandler;
    private final GetPatientsByFiltersQueryHandler getPatientsByFiltersHandler;
    private final GetAttendantsByFiltersQueryHandler getAttendantsByFiltersHandler;

    public ProfileApplicationFacade(
            CreateProfileCommandHandler createHandler,
            UpdateProfileCommandHandler updateHandler,
            GetProfileByUserIdQueryHandler getByUserIdHandler,
            GetAllPatientsQueryHandler getAllPatientsHandler,
            GetAllAttendantsQueryHandler getAllAttendantsHandler,
            GetProfileByIdQueryHandler getByIdHandler,
            GetProfileByDniQueryHandler getByDniHandler,
            DeleteProfileByDniCommandHandler deleteByDniHandler,
            GetPatientsByFiltersQueryHandler getPatientsByFiltersHandler,
            GetAttendantsByFiltersQueryHandler getAttendantsByFiltersHandler
    ) {
        this.createHandler = createHandler;
        this.updateHandler = updateHandler;
        this.getByUserIdHandler = getByUserIdHandler;
        this.getAllPatientsHandler = getAllPatientsHandler;
        this.getAllAttendantsHandler = getAllAttendantsHandler;
        this.getByIdHandler = getByIdHandler;
        this.getByDniHandler = getByDniHandler;
        this.deleteByDniHandler = deleteByDniHandler;
        this.getPatientsByFiltersHandler = getPatientsByFiltersHandler;
        this.getAttendantsByFiltersHandler = getAttendantsByFiltersHandler;
    }

    public UserProfileResponse createProfile(Long userId, CreateProfileRequest request, String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown user role");
        }

        var cmd = new CreateProfileCommand(
                userId,
                role,
                request.firstName(),
                request.lastName(),
                request.dni(),
                request.age(),
                request.gender(),
                request.nationality(),
                request.bloodGroup(),
                request.allergy()
        );
        return createHandler.handle(cmd);
    }

    public UserProfileResponse updateProfile(Long userId, CreateProfileRequest request, String role) {
        if (role == null || role.isBlank()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Unknown user role");
        }

        var bloodGroup = "ATTENDANT".equalsIgnoreCase(role) ? null : request.bloodGroup();
        var allergy = "ATTENDANT".equalsIgnoreCase(role) ? null : request.allergy();

        var cmd = new UpdateProfileCommand(
                userId,
                role,
                request.firstName(),
                request.lastName(),
                request.dni(),
                request.age(),
                request.gender(),
                request.nationality(),
                bloodGroup,
                allergy
        );
        return updateHandler.handle(cmd);
    }

    public UserProfileResponse getProfileByUserId(Long userId) {
        return getByUserIdHandler.handle(userId);
    }

    public List<UserProfileResponse> getAllPatients() {
        return getAllPatientsHandler.handle();
    }

    public List<UserProfileResponse> getAllAttendants() {
        return getAllAttendantsHandler.handle();
    }

    public UserProfileResponse getProfileById(Long id) {
        return getByIdHandler.handle(id);
    }

    public UserProfileResponse getProfileByDni(String dni) {
        return getByDniHandler.handle(dni);
    }

    public void deleteProfileByDni(String dni) {
        deleteByDniHandler.handle(new DeleteProfileByDniCommand(dni));
    }

    public List<UserProfileResponse> searchPatientsByName(String name) {
        var query = new GetPatientsByFiltersQuery(name);
        return getPatientsByFiltersHandler.handle(query);
    }

    public List<UserProfileResponse> searchAttendantsByName(String name) {
        var query = new GetAttendantsByFiltersQuery(name);
        return getAttendantsByFiltersHandler.handle(query);
    }
}
