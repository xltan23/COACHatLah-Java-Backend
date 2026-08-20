package ais.prs.educhat.repositories;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static ais.prs.educhat.repositories.Queries.*;

import ais.prs.educhat.models.Profile;

@Repository
public class ProfileRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addProfile(Profile profile) {
        System.out.println("Inserting profile into database: " + profile);
        // Execute the update and return true if one row was affected
        return jdbcTemplate.update(SQL_INSERT_PROFILE_IN_PROFILES, 
            profile.getId(),
            profile.getName(), 
            profile.getGender(),
            profile.getRole().name(), 
            profile.getSchool(), 
            profile.getClassCohort(), 
            profile.getCca(),
            profile.getStatus().name(),
            profile.getDisciplinaryStatus().name(),
            profile.getCurrentSemesterFlagCount(),
            Timestamp.from(profile.getCreatedAt()),
            Timestamp.from(profile.getUpdatedAt())
        ) > 0;
    }

    public List<Profile> getProfilesByClass(String school, String classCohort) {
        System.out.printf("\nFetching %s %s information from database... ", school, classCohort);
        
        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_PROFILES_BY_SCHOOL_AND_CLASS, school, classCohort);
        List<Profile> profiles = new LinkedList<>();
        while (srs.next()) {
            profiles.add(Profile.createProfile(srs));
        }
        System.out.println("\nRetrieval complete");
        return profiles;
    }

    public List<Profile> getProfilesByCCA(String school, String cca) {
        System.out.printf("\nFetching %s %s information from database... ", school, cca);
        
        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_PROFILES_BY_SCHOOL_AND_CCA, school, cca);
        List<Profile> profiles = new LinkedList<>();
        while (srs.next()) {
            profiles.add(Profile.createProfile(srs));
        }
        System.out.println("\nRetrieval complete");
        return profiles;
    }

    public List<Profile> getProfilesById(String id, String search) {
        System.out.printf("\nFetching school and class cohort for ID %s from database: ", id);
        
        String school = "";
        String classCohort = "";
        String cca = "";

        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_PROFILES_BY_ID, id);
        while (srs.next()) {
            school = srs.getString("school");
            classCohort = srs.getString("class_cohort");
            cca = srs.getString("cca");
        }

        if (search.equals("CLASS")) {
            System.out.println("\nIdentified CLASS");
            return getProfilesByClass(school, classCohort);

        } else if (search.equals("CCA")) {
            System.out.println("\nIdentified CCA");
            return getProfilesByCCA(school, cca);

        } else {
            throw new IllegalArgumentException("Invalid search parameter: " + search);
        }
    }

    public boolean incrementFlagCount(String id) {
        System.out.println("Incrementing flag count for profile: " + id);
        return jdbcTemplate.update(SQL_INCREMENT_FLAG_COUNT, Timestamp.from(Instant.now()), id) > 0;
    }

    public int getFlagCount(String id) {
        System.out.println("Retrieving flag count for profile: " + id);
        Integer count = jdbcTemplate.queryForObject(SQL_GET_FLAG_COUNT, Integer.class, id);
        System.out.printf("Profile % flag count: %d", id, count);
        return count != null ? count : 0;
    }
}