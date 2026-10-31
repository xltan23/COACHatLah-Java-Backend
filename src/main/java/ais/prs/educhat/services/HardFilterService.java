package ais.prs.educhat.services;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ais.prs.educhat.dto.ModerationResponse;

// Rule-based check for local vulgarities (Singlish/Hokkien) that are rare in the models' English training data
@Service
public class HardFilterService {

    private static final Set<String> HARD_FILTER_WORDS = Set.of(
        "fuck",
        "fk",
        "fuk",
        "fug",
        "f*ck",
        "f**k",
        "fker",
        "fuker",
        "fking",
        "fuking",

        "jibai", 
        "chibai", 
        "cb",
        "ccb",

        "kanina",
        "knn", 
        "knnccb", 
        "knnbccb",

        "nabei",
        "nbcb",
        "nbccb",

        "lanjiao",
        "lj",

        "diu lei lo mo",
        "diuleilomo",
        "diu lei",
        "dllm"
    );

    // Words compared with spaces and symbols removed: "f*ck" -> "fck", "diu lei lo mo" -> "diuleilomo"
    private static final Set<String> NORMALIZED_WORDS = HARD_FILTER_WORDS.stream()
        .map(HardFilterService::normalize)
        .collect(Collectors.toSet());

    private static final int MAX_WORD_LENGTH = NORMALIZED_WORDS.stream().mapToInt(String::length).max().orElse(0);

    // Vulgarity is toxic and inappropriate, but not bullying by itself (0.50 stays below the 0.85 cyberbullying threshold)
    private static final ModerationResponse HARD_FILTER_SCORES = new ModerationResponse(0.99f, 0.99f, 0.50f);

    // Case-insensitive, on whole words or runs of consecutive pieces joined together, so "KNN!", "k n n", "lan-jiao"
    // and "f**k" match. Joined runs must equal a filter word exactly, so "knnow" and "public bus" (-> "publicbus") do not.
    public boolean matches(String content) {
        if (content == null) {
            return false;
        }
        List<String> pieces = Arrays.stream(content.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+"))
            .filter(p -> !p.isEmpty())
            .toList();
        for (int start = 0; start < pieces.size(); start++) {
            StringBuilder joined = new StringBuilder();
            for (int end = start; end < pieces.size(); end++) {
                joined.append(pieces.get(end));
                if (joined.length() > MAX_WORD_LENGTH) {
                    break;
                }
                if (NORMALIZED_WORDS.contains(joined.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Lowercase letters and digits only
    private static String normalize(String text) {
        return text.toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]", "");
    }

    // Higher of the model score and the hard-filter score per category, so a high model score (e.g. cyberbullying) is kept.
    // No model scores (moderation service unavailable) -> hard-filter scores alone.
    public ModerationResponse applyTo(ModerationResponse modelScores) {
        if (modelScores == null) {
            return HARD_FILTER_SCORES;
        }
        return new ModerationResponse(
            Math.max(modelScores.toxic(), HARD_FILTER_SCORES.toxic()),
            Math.max(modelScores.inappropriate(), HARD_FILTER_SCORES.inappropriate()),
            Math.max(modelScores.cyberbullying(), HARD_FILTER_SCORES.cyberbullying()));
    }
}
