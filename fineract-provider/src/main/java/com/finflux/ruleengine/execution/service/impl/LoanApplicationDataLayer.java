package com.finflux.ruleengine.execution.service.impl;

import com.finflux.ruleengine.configuration.data.FieldData;
import com.finflux.ruleengine.configuration.service.RuleCacheService;
import com.finflux.ruleengine.execution.service.DataLayerReadPlatformService;
import com.finflux.ruleengine.execution.service.DataLayer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dhirendra on 22/09/16.
 */

public class LoanApplicationDataLayer implements DataLayer {

    private final RuleCacheService ruleCacheService;
    private final Map<String, Object> keyValueMap;
    private final DataLayerReadPlatformService dataLayerReadPlatformService;


    public LoanApplicationDataLayer(Long loanApplicationId, Long clientId,
                                    DataLayerReadPlatformService dataLayerReadPlatformService,
                                    RuleCacheService ruleCacheService){
        keyValueMap = dataLayerReadPlatformService.getAllMatrix(clientId);
        this.dataLayerReadPlatformService = dataLayerReadPlatformService;
        this.ruleCacheService = ruleCacheService;
//        keyValueMap.put("age", 25L);
////        keyValueMap.put("sex", "M");
////        keyValueMap.put("isMarried", true);
//        keyValueMap.put("name", "dhirendra");
    }

    @Override
    public Map<String, Object> getValues(List<String> keys) {
        Map<String,Object> myMap = new HashMap<>();
        for(String key: keys){
            myMap.put(key,keyValueMap.get(key));
        }
        return myMap;
    }

    @Override
    public Object getValue(String key) {
        return keyValueMap.get(key);
    }
}