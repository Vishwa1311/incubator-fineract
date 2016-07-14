package com.finflux.infrastructure.gis.district.service;

import java.util.Collection;
import java.util.List;

import com.finflux.infrastructure.gis.district.data.DistrictData;

public interface DistrictReadPlatformService {

    DistrictData retrieveOne(final Long districtId);

    Collection<DistrictData> retrieveAllDistrictDataByDistrictIds(final List<Long> districtIds);
    
    Collection<DistrictData> retrieveAllDistrictDataByStateId(final Long stateId);

    Collection<DistrictData> retrieveAllDistrictDataByStateIds(final List<Long> stateIds);
}