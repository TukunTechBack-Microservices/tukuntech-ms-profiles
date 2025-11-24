package com.upc.tukuntechprofilesservice.profiles.application.dto;


import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Allergy;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.BloodGroup;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Gender;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Nationality;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request body for creating or updating a profile. " +
        "For PATIENT users, all fields apply; for ATTENDANT users, clinical fields (bloodGroup, allergy) are ignored.")
public record CreateProfileRequest(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Size(min = 8, max = 20) String dni,
        @NotNull @Min(0) @Max(120) Integer age,

        // Campos solo relevantes para pacientes
        Gender gender,
        BloodGroup bloodGroup,
        Nationality nationality,
        Allergy allergy
) {}
