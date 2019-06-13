package de.materna.dmn.tester;

import de.materna.dmn.tester.helpers.MergingHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class MergingHelperTest {
	@Test
	void mergePrimitives() {
		Assertions.assertEquals("b", MergingHelper.merge("a", "b"));
		Assertions.assertEquals(2, MergingHelper.merge(1, 2));
		Assertions.assertEquals(false, MergingHelper.merge(true, false));
	}

	@Test
	void mergeFlatMap() {
		Map<String, String> existingMap = new HashMap<>();
		existingMap.put("a", "a");
		existingMap.put("b", "b");

		Map<String, String> templateMap = new HashMap<>();
		templateMap.put("b", "b");

		Assertions.assertEquals("{a=a, b=b}", MergingHelper.merge(existingMap, templateMap).toString());
	}

	@Test
	void mergeDeepMap() {
		Map<String, Object> existingMap = new HashMap<>();
		existingMap.put("a", "a");
		Map<String, Object> childExistingMap = new HashMap<>();
		childExistingMap.put("a", "a");
		childExistingMap.put("b", "b");
		existingMap.put("b", childExistingMap);

		Map<String, Object> templateMap = new HashMap<>();
		Map<String, Object> childTemplateMap = new HashMap<>();
		childTemplateMap.put("b", "c");
		childTemplateMap.put("c", "c");
		templateMap.put("b", childTemplateMap);

		Assertions.assertEquals("{a=a, b={a=a, b=c, c=c}}", MergingHelper.merge(existingMap, templateMap).toString());
	}

	@Test
	void mergeListWithMoreElements() {
		Map<String, Object> existingMap = new HashMap<>();
		existingMap.put("a", "a");
		List<Object> childExistingList = new LinkedList<>();
		childExistingList.add(1);
		childExistingList.add("a");
		existingMap.put("b", childExistingList);

		Map<String, Object> templateMap = new HashMap<>();
		List<Object> childTemplateList = new LinkedList<>();
		childTemplateList.add(2);
		childTemplateList.add("b");
		childTemplateList.add("c");
		templateMap.put("b", childTemplateList);

		Assertions.assertEquals("{a=a, b=[2, b, c]}", MergingHelper.merge(existingMap, templateMap).toString());
	}

	@Test
	void mergeListWithLessElements() {
		Map<String, Object> existingMap = new HashMap<>();
		existingMap.put("a", "a");
		List<Object> childExistingList = new LinkedList<>();
		childExistingList.add(1);
		childExistingList.add("a");
		existingMap.put("b", childExistingList);

		Map<String, Object> templateMap = new HashMap<>();
		List<Object> childTemplateList = new LinkedList<>();
		childTemplateList.add(2);
		templateMap.put("b", childTemplateList);

		Assertions.assertEquals("{a=a, b=[2, a]}", MergingHelper.merge(existingMap, templateMap).toString());
	}
}