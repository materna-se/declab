package de.materna.dmn.tester.helpers;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MergingHelper {
	@SuppressWarnings("unchecked")
	public static Object merge(Object existing, Object update) {
		if (existing instanceof Map && update instanceof Map) {
			Map<String, Object> existingMap = (Map<String, Object>) existing;
			Map<String, Object> updateMap = (Map<String, Object>) update;

			Map<String, Object> mergedMap = new LinkedHashMap<>(existingMap);
			for (String key : updateMap.keySet()) {
				mergedMap.put(key, merge(mergedMap.get(key), updateMap.get(key)));
			}

			return mergedMap;
		}

		if (existing instanceof List && update instanceof List) {
			List<Object> existingList = (List<Object>) existing;
			List<Object> updateList = (List<Object>) update;

			Iterator<Object> existingIterator = existingList.iterator();
			Iterator<Object> updateIterator = updateList.iterator();

			List<Object> mergedList = new LinkedList<>();

			while (existingIterator.hasNext() || updateIterator.hasNext()) {
				if (!existingIterator.hasNext()) {
					mergedList.add(updateIterator.next());
					continue;
				}
				if (!updateIterator.hasNext()) {
					mergedList.add(existingIterator.next());
					continue;
				}

				mergedList.add(merge(existingIterator.next(), updateIterator.next()));
			}

			return mergedList;
		}

		return update;
	}
}