package ais.prs.educhat.dto;

import java.util.List;

/**
 * One student's flag counts for the day. counts has 24 entries; index = hour of day
 * (index 15 = 15:00-15:59).
 */
public record SpikeStudentCountsDto(
    String id,
    List<Integer> counts
) {
}
