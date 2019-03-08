package com.example.user.service;

import com.example.common.entity.Region;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zhy
 * @date 2019-01-17 23:07
 **/
@Component
public class GetRegionService {

	@Autowired
	private RegionService regionService;

	private static List<Region> Regions;

	protected List<Region> getRegions() {
		if(Regions==null){
			createRegion();
		}
		return Regions;
	}

	private synchronized void createRegion(){
		if (Regions == null) {
			Regions = regionService.getAll();
		}
	}
}
