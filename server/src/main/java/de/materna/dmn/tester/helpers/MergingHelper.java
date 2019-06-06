package de.materna.dmn.tester.helpers;

import java.util.HashMap;
import java.util.Map;

public class MergingHelper {
	/**
	 * Deep merges two given maps into a new one
	 * The two given maps will not be changed
	 */
	public static Map<String, ?> mergeMaps(Map<String, ?> existingMap, Map<String, ?> newMap) {
		Map<String, Object> mergedMap = new HashMap<>(existingMap);

		for (String key : newMap.keySet()) {
			Object existingMapValue = existingMap.get(key);
			Object newMapValue = newMap.get(key);

			if (existingMapValue instanceof Map && newMapValue instanceof Map) {
				mergedMap.put(key, mergeMaps((Map<String, ?>) existingMapValue, (Map<String, ?>) newMapValue));
				continue;
			}

			mergedMap.put(key, newMapValue);
		}

		return mergedMap;
	}
}
