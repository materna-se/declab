package de.materna.dmn.tester.helpers;

import java.util.*;

public class MergingHelper {
	public static Object merge(Object existing, Object template) {
		if (template instanceof Map) {
			Map<String, Object> existingMap = (Map<String, Object>) existing;
			Map<String, Object> templateMap = (Map<String, Object>) template;

			Map<String, Object> mergedMap = new LinkedHashMap<>(existingMap);
			for (String key : templateMap.keySet()) {
				mergedMap.put(key, merge(mergedMap.get(key), templateMap.get(key)));
			}

			return mergedMap;
		}

		if (template instanceof List) {
			List<Object> existingList = (List<Object>) existing;
			List<Object> templateList = (List<Object>) template;

			Iterator<Object> existingIterator = existingList.iterator();
			Iterator<Object> templateIterator = templateList.iterator();

			List<Object> mergedList = new LinkedList<>();

			while (existingIterator.hasNext() || templateIterator.hasNext()) {
				if (!existingIterator.hasNext()) {
					mergedList.add(templateIterator.next());
					continue;
				}
				if (!templateIterator.hasNext()) {
					mergedList.add(existingIterator.next());
					continue;
				}

				mergedList.add(merge(existingIterator.next(), templateIterator.next()));
			}

			return mergedList;
		}

		return template;
	}
}
