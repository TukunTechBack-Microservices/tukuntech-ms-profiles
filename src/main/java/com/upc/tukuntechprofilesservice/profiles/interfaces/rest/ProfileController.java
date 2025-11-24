package com.upc.tukuntechprofilesservice.profiles.interfaces.rest;

import com.upc.tukuntechprofilesservice.profiles.application.dto.CreateProfileRequest;
import com.upc.tukuntechprofilesservice.profiles.application.dto.UserProfileResponse;
import com.upc.tukuntechprofilesservice.profiles.application.facade.ProfileApplicationFacade;
import com.upc.tukuntechprofilesservice.shared.security.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/profiles")
@Tag(name = "Profiles", description = "Endpoints for managing user profiles (patients and attendants)")
public class ProfileController {

    private final ProfileApplicationFacade profileApp;
    private final CurrentUserService currentUserService;

    public ProfileController(ProfileApplicationFacade profileApp,
                             CurrentUserService currentUserService) {
        this.profileApp = profileApp;
        this.currentUserService = currentUserService;
    }


    @PostMapping
    @Operation(
            summary = "Create user profile automatically",
            description = "Creates a personal profile for the authenticated user. Detects if they are a patient or attendant based on their IAM role.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile created successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid or duplicate data"),
                    @ApiResponse(responseCode = "403", description = "Unauthorized role")
            })
    public ResponseEntity<UserProfileResponse> createProfile(@RequestBody @Valid CreateProfileRequest request) {

        var identity = currentUserService.getCurrentUser();
        var primaryRole = identity.roles().stream().findFirst().orElse("UNKNOWN");

        return ResponseEntity.ok(
                profileApp.createProfile(identity.id(), request, primaryRole)
        );
    }


    @GetMapping("/me")
    @Operation(
            summary = "Get authenticated user's profile",
            description = "Returns the profile of the currently authenticated user.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found")
            })
    public ResponseEntity<UserProfileResponse> getAuthenticatedProfile() {
        var identity = currentUserService.getCurrentUser();
        return ResponseEntity.ok(profileApp.getProfileByUserId(identity.id()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyRole('PATIENT','ATTENDANT')")
    @Operation(
            summary = "Update authenticated user's profile",
            description = "Allows patients and attendants to update their personal information. " +
                    "Patients can update clinical data (blood group, allergy); attendants cannot.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "403", description = "Forbidden role or invalid modification")
            })
    public ResponseEntity<UserProfileResponse> updateAuthenticatedProfile(@RequestBody @Valid CreateProfileRequest request) {

        var identity = currentUserService.getCurrentUser();
        var primaryRole = identity.roles().stream().findFirst().orElse("UNKNOWN");

        return ResponseEntity.ok(
                profileApp.updateProfile(identity.id(), request, primaryRole)
        );
    }


    @DeleteMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Delete profile by DNI",
            description = "Deletes a patient or attendant profile by DNI. Accessible only to administrators.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Profile deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Profile not found")
            })
    public ResponseEntity<Void> deleteProfileByDni(@PathVariable String dni) {
        profileApp.deleteProfileByDni(dni);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "List all patients",
            description = "Returns all profiles with type PATIENT (admin only).",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserProfileResponse>> listAllPatients() {
        return ResponseEntity.ok(profileApp.getAllPatients());
    }

    @GetMapping("/attendants")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "List all attendants",
            description = "Returns all profiles with type ATTENDANT (admin only).",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<UserProfileResponse>> listAllAttendants() {
        return ResponseEntity.ok(profileApp.getAllAttendants());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Find profile by ID",
            description = "Retrieves a user profile (patient or attendant) by its database ID. Only accessible to administrators.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile found",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found")
            })
    public ResponseEntity<UserProfileResponse> getProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(profileApp.getProfileById(id));
    }

    @GetMapping("/dni/{dni}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Find profile by DNI",
            description = "Retrieves a user profile by its DNI. Only accessible to administrators.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile found",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Profile not found")
            })
    public ResponseEntity<UserProfileResponse> getProfileByDni(@PathVariable String dni) {
        return ResponseEntity.ok(profileApp.getProfileByDni(dni));
    }

    @GetMapping("/patients/search")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Search patients by partial name or surname",
            description = "Returns all patient profiles whose first or last name contains the given keyword. Accessible only to administrators.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of matching patients",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "No matching patients found")
            })
    public ResponseEntity<List<UserProfileResponse>> searchPatientsByName(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(profileApp.searchPatientsByName(name));
    }

    @GetMapping("/attendants/search")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @Operation(
            summary = "Search attendants by partial name or surname",
            description = "Returns all attendant profiles whose first or last name contains the given keyword. Accessible only to administrators.",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of matching attendants",
                            content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
                    @ApiResponse(responseCode = "404", description = "No matching attendants found")
            })
    public ResponseEntity<List<UserProfileResponse>> searchAttendantsByName(@RequestParam(required = false) String name) {
        return ResponseEntity.ok(profileApp.searchAttendantsByName(name));
    }
}
