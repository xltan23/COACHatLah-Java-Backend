package ais.prs.educhat.repositories;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import static ais.prs.educhat.repositories.Queries.*;

import ais.prs.educhat.models.Escalation;

@Repository 
public class EscalationRepository {
    
    @Autowired 
    private JdbcTemplate jdbcTemplate;

    public boolean addEscalation(Escalation escalation) {
        System.out.printf("\nInserting escalation into database: " + escalation);

        return jdbcTemplate.update(
            SQL_INSERT_ESCALATION_IN_ESCALATIONS,
            escalation.getStudentId(), 
            escalation.getClassCohort(), 
            escalation.getTriggerType().name(),
            escalation.getTriggerDetail(),
            escalation.getReviewStatus().name(),
            Timestamp.from(escalation.getCreatedAt())
        ) > 0;
    }

    public List<Escalation> getEscalations() {
        System.out.printf("\nRetrieving escalations from database...");

        SqlRowSet srs = jdbcTemplate.queryForRowSet(SQL_SELECT_ALL_ESCALATIONS);
        List<Escalation> escalations = new java.util.LinkedList<>();
        while (srs.next()) {
            escalations.add(Escalation.createEscalationFromRowSet(srs));
        }
        return escalations;
    }

}
