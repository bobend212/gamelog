package com.matkon.gamelog.domain.common.sync;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SyncUtils {
    public static boolean areDatesDifferent(LocalDate oldDate, LocalDate newDate) {
        if (oldDate == null && newDate == null) return false;           // both null = no change
        if (oldDate == null || newDate == null) return true;            // one null, one not = change
        return !oldDate.equals(newDate);                                // both non-null compare values
    }

    public static boolean areStringsDifferent(String oldStr, String newStr) {
        if (oldStr == null && newStr == null) return false;          // both null = no change
        if (oldStr == null || newStr == null) return true;           // one null, one not = change
        return !oldStr.equals(newStr);                               // both non-null compare values
    }

    public static boolean areIntsDifferent(Integer oldInt, Integer newInt) {
        if (oldInt == null && newInt == null) return false;        // both null = no change
        if (oldInt == null || newInt == null) return true;         // one null, one not = change
        return !oldInt.equals(newInt);                             // both non-null compare values
    }

    public static boolean areStringListsDifferent(Set<String> oldList, Set<String> newList) {
        if (oldList == null && newList == null) return false;            // both null = no change
        if (oldList == null || newList == null) return true;             // one null, one not = change
        if (oldList.size() != newList.size()) return true;               // different size = change
        return !new HashSet<>(oldList).containsAll(newList) || !new HashSet<>(newList).containsAll(oldList); // differ
    }
}
