package com.matkon.gamelog.data.sync;

import java.time.LocalDate;

public class SyncUtils
{
    public static boolean areDatesDifferent(LocalDate oldDate, LocalDate newDate)
    {
        if (oldDate == null && newDate == null) return false;           // both null = no change
        if (oldDate == null || newDate == null) return true;            // one null, one not = change
        return !oldDate.equals(newDate);                                // both non-null compare values
    }

    public static boolean areStringsDifferent(String oldStr, String newStr)
    {
        if (oldStr == null && newStr == null) return false;          // both null = no change
        if (oldStr == null || newStr == null) return true;           // one null, one not = change
        return !oldStr.equals(newStr);                               // both non-null compare values
    }

    public static boolean areIntsDifferent(Integer oldInt, Integer newInt)
    {
        if (oldInt == null && newInt == null) return false;        // both null = no change
        if (oldInt == null || newInt == null) return true;         // one null, one not = change
        return !oldInt.equals(newInt);                             // both non-null compare values
    }

}
