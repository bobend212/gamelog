package com.matkon.gamelog.domain.sync;

import com.matkon.gamelog.domain.common.sync.SyncUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncUtilsTest {

    @Test
    void dates_bothDatesNull_shouldReturnFalse() {
        assertFalse(SyncUtils.areDatesDifferent(null, null));
    }

    @Test
    void dates_oneDateNull_shouldReturnTrue() {
        assertTrue(SyncUtils.areDatesDifferent(null, LocalDate.now()));
        assertTrue(SyncUtils.areDatesDifferent(LocalDate.now(), null));
    }

    @Test
    void dates_sameDates_shouldReturnFalse() {
        LocalDate date = LocalDate.of(2021, 1, 1);
        assertFalse(SyncUtils.areDatesDifferent(date, date));
    }

    @Test
    void dates_differentDates_shouldReturnTrue() {
        LocalDate d1 = LocalDate.of(2021, 1, 1);
        LocalDate d2 = LocalDate.of(2021, 1, 2);
        assertTrue(SyncUtils.areDatesDifferent(d1, d2));
    }

    @Test
    void strings_bothStringsNull_shouldReturnFalse() {
        assertFalse(SyncUtils.areStringsDifferent(null, null));
    }

    @Test
    void strings_oneStringNull_shouldReturnTrue() {
        assertTrue(SyncUtils.areStringsDifferent(null, "test"));
        assertTrue(SyncUtils.areStringsDifferent("test", null));
    }

    @Test
    void strings_sameStrings_shouldReturnFalse() {
        String str = "abc";
        assertFalse(SyncUtils.areStringsDifferent(str, str));
    }

    @Test
    void strings_differentStrings_shouldReturnTrue() {
        assertTrue(SyncUtils.areStringsDifferent("abc", "def"));
    }

    @Test
    void ints_bothIntsNull_shouldReturnFalse() {
        assertFalse(SyncUtils.areIntsDifferent(null, null));
    }

    @Test
    void ints_oneIntNull_shouldReturnTrue() {
        assertTrue(SyncUtils.areIntsDifferent(null, 5));
        assertTrue(SyncUtils.areIntsDifferent(5, null));
    }

    @Test
    void ints_sameInts_shouldReturnFalse() {
        Integer i = 10;
        assertFalse(SyncUtils.areIntsDifferent(i, i));
    }

    @Test
    void ints_differentInts_shouldReturnTrue() {
        assertTrue(SyncUtils.areIntsDifferent(10, 20));
    }

    @Test
    void string_lists_bothListsNull_shouldReturnFalse() {
        assertFalse(SyncUtils.areStringListsDifferent(null, null));
    }

    @Test
    void string_lists_oneListNull_shouldReturnTrue() {
        assertTrue(SyncUtils.areStringListsDifferent(null, Set.of("a")));
        assertTrue(SyncUtils.areStringListsDifferent(Set.of("a"), null));
    }

    @Test
    void string_lists_differentSizeLists_shouldReturnTrue() {
        assertTrue(SyncUtils.areStringListsDifferent(Set.of("a", "b"), Set.of("a")));
    }

    @Test
    void string_lists_sameLists_shouldReturnFalse() {
        assertFalse(SyncUtils.areStringListsDifferent(Set.of("a", "b"), Set.of("b", "a")));
    }

    @Test
    void string_lists_differentLists_shouldReturnTrue() {
        assertTrue(SyncUtils.areStringListsDifferent(Set.of("a", "b"), Set.of("b", "c")));
    }
}
