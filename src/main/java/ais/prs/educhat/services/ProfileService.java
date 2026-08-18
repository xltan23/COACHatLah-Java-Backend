package ais.prs.educhat.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ais.prs.educhat.models.Profile;
import ais.prs.educhat.models.Role;
import ais.prs.educhat.repositories.ProfileRepository;

@Service
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepo;
    
    public boolean addProfile(String name, String gender, String role, String school, String classCohort, String cca) {
        // Create a new Profile object
        Profile profile = new Profile(name, gender, toRole(role), school, classCohort, cca); 
        return profileRepo.addProfile(profile);
    }

    public List<Profile> getProfilesByClass(String school, String classCohort) {
        return profileRepo.getProfilesByClass(school, classCohort);
    }

    public List<Profile> getClassProfilesById(String id, String search) {
        return profileRepo.getProfilesById(id, search);
    }

    public List<Profile> getCCAProfilesById(String id, String search) {
        return profileRepo.getProfilesById(id, search);
    }

    // To be used by method that analyzes message, and flags the sender's profile if necessary
    private boolean flagProfile(String profileId) {
        return profileRepo.incrementFlagCount(profileId);
    }

    private static Role toRole(String role) {
        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("\nInvalid role provided.");
            throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    // Additional methods for profile management (if time permits): Class Update, Flag Count Reset, Statuses Updates 

}
