package ais.prs.educhat.dto;

import java.util.List;

/**
 * Hour bins detected as anomalous for a student, as "HH:00" (e.g. "16:00").
 */
public record SpikeStudentResultDto(
    String id,
    List<String> timestamps
) {
}
