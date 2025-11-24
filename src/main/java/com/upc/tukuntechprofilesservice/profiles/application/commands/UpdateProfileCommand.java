package com.upc.tukuntechprofilesservice.profiles.application.commands;


import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Allergy;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.BloodGroup;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Gender;
import com.upc.tukuntechprofilesservice.profiles.domain.model.valueobjects.Nationality;

public record UpdateProfileCommand(
        Long userId,
        String role,
        String firstName,
        String lastName,
        String dni,
        Integer age,
        Gender gender,
        Nationality nationality,
        BloodGroup bloodGroup, // null si ATTENDANT
        Allergy allergy        // null si ATTENDANT
) {}
