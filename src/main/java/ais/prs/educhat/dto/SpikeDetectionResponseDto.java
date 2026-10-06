package ais.prs.educhat.dto;

import java.time.LocalDate;
import java.util.List;

public record SpikeDetectionResponseDto(
    LocalDate date,
    List<SpikeStudentResultDto> results
) {
}
