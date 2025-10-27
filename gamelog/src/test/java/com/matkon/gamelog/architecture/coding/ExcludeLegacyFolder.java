package com.matkon.gamelog.architecture.coding;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;

public class ExcludeLegacyFolder implements ImportOption {

    @Override
    public boolean includes(Location location) {
        return !location.contains("com/matkon/gamelog/legacy");
    }

}
