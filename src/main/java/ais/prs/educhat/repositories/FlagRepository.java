package ais.prs.educhat.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static ais.prs.educhat.repositories.Queries.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import ais.prs.educhat.dto.SpikeStudentCountsDto;
import ais.prs.educhat.models.FlagRecord;
import ais.prs.educhat.models.enums.FlagCategory;
import jakarta.json.JsonObject;

@Repository
public class FlagRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean addFlagRecord(FlagRecord flagRecord) {
        System.out.println("\nInserting flag record into database: " + flagRecord);
        
        JsonObject scores = flagRecord.getConfidenceScores();

        return jdbcTemplate.update(SQL_INSERT_FLAGRECORD_IN_FLAGRECORDS,
            flagRecord.getSenderId(),
            flagRecord.getRecipientId(),
            flagRecord.getMessageId(),
            flagRecord.getOriginalText(),
            flagRecord.getRephraseSuggested(),
            flagRecord.getFlagCategories().contains(FlagCategory.TOXIC),
            flagRecord.getFlagCategories().contains(FlagCategory.INAPPROPRIATE),
            flagRecord.getFlagCategories().contains(FlagCategory.CYBERBULLYING),
            scores != null ? scores.toString() : null,
            Timestamp.from(flagRecord.getFlaggedAt())
        ) > 0;
    }

    // For disciplinary committee to view Flag Records
    public List<FlagRecord> getFlagRecords(String id) {
        System.out.printf("\nRetrieving flag records for profile %s from database ", id);

        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_FLAGRECORDS_BY_ID, id);
        List<FlagRecord> flagRecords = new LinkedList<>();
        while (srs.next()) {
            flagRecords.add(FlagRecord.createFlagRecord(srs));
        }

        return flagRecords;
    }

    // For the daily spike detection job: 24 hourly flag counts (index = hour) per student flagged on the given date
    public List<SpikeStudentCountsDto> getHourlyFlagCounts(LocalDate date) {
        System.out.printf("\nRetrieving hourly flag counts for %s from database ", date);

        ZoneId zone = ZoneId.of("Asia/Singapore");
        Timestamp start = Timestamp.from(date.atStartOfDay(zone).toInstant());
        Timestamp end = Timestamp.from(date.plusDays(1).atStartOfDay(zone).toInstant());

        Map<String, Integer[]> countsByStudent = new LinkedHashMap<>();
        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_HOURLY_FLAG_COUNTS, start, end);
        while (srs.next()) {
            Integer[] counts = countsByStudent.computeIfAbsent(srs.getString("sender_id"), k -> {
                Integer[] zeros = new Integer[24];
                Arrays.fill(zeros, 0);
                return zeros;
            });
            counts[srs.getInt("hour_of_day")] = srs.getInt("flag_count");
        }

        List<SpikeStudentCountsDto> students = new LinkedList<>();
        countsByStudent.forEach((id, counts) -> students.add(new SpikeStudentCountsDto(id, List.of(counts))));

        return students;
    }
}
