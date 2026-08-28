package ais.prs.educhat.dto;

import ais.prs.educhat.models.Profile;

public record ProfileDto(
    String id,
    String name,
    String gender,
    String role,
    String school,
    String classCohort,
    String cca
) {
    public static ProfileDto from(Profile profile) {
        return new ProfileDto(
            profile.getId(), 
            profile.getName(), 
            profile.getGender(), 
            profile.getRole().name(), 
            profile.getSchool(),
            profile.getClassCohort(),
            profile.getCca()
        );
    }
}
