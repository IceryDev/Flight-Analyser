package com.still_processing.FlightData.Filters;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FuzzySearch {
    public static int partialLevenshtein(String query, String candidate) {
        int queryLength = query.length();
        int candidateLength = candidate.length();
        int[][] dp = new int[queryLength + 1][candidateLength + 1];

        for (int i = 0; i <= queryLength; i++)
            dp[i][0] = i;
        for (int j = 0; j <= candidateLength; j++)
            dp[0][j] = 0;

        for (int i = 1; i <= queryLength; i++) {
            for (int j = 1; j <= candidateLength; j++) {
                int cost = query.charAt(i - 1) == candidate.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost);
            }
        }

        int min = Integer.MAX_VALUE;
        for (int j = 0; j <= candidateLength; j++) {
            min = Math.min(min, dp[queryLength][j]);
        }
        return min;
    }

    public static List<String> fuzzySearch(String query, List<String> candidates) {
        return candidates.stream()
                .sorted(Comparator.comparingInt(s -> partialLevenshtein(query, s)))
                .collect(Collectors.toList());
    }
}
