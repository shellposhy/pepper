package com.pepper.lucene.test;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.junit.Before;
import org.junit.Test;

import com.pepper.lucene.Configuration;
import com.pepper.lucene.PepperException;
import com.pepper.lucene.analyzer.IAnalyzer;
import com.pepper.lucene.analyzer.PaodingAnalyzer;
import com.pepper.lucene.common.PepperResult;
import com.pepper.lucene.comparator.base.PepperSortField;
import com.pepper.lucene.comparator.base.PepperSortField.FieldType;
import com.pepper.lucene.service.IndexDao;
import com.pepper.lucene.service.IndexService;

public class JunitIndexService {

	private IndexDao indexService;
	private static final String PATH = "D:/test";

	public String getIndexPath(Integer baseId) {
		return PATH + "/idx/" + baseId;
	}

	public String[] getIndexPaths(Integer[] baseIds) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < baseIds.length; i++) {
			list.add(getIndexPath(baseIds[i]));
		}
		return list.toArray(new String[list.size()]);
	}

	@Before
	public void setUp() throws Exception {
		String dicHome = PATH + "/dic";
		Configuration indexConfig = new Configuration();
		indexConfig.setAnalyzerFactory(new PaodingAnalyzer(dicHome));
		indexConfig.setHightLightPreTag("<span class=\"highLight\">");
		indexConfig.setHightLightPostTag("</span>");
		indexConfig.setHightLightAnalyzerMode(IAnalyzer.MAX_WORD_LENGTH_MODE);
		indexService = IndexService.getInstance("PDS3_DB_INDEX_SERVICE");
		indexService.setIndexConfig(indexConfig);
	}

	@Test
	public void testAddDocument() {
		Document document = new Document();
		Field field = new Field("Name", "网络", Store.YES, Index.ANALYZED);
		document.add(field);
		try {
			indexService.addDocument(getIndexPath(1), document);
		} catch (PepperException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSearchStringStringMdSortFieldArrayStringArrayIntInt() {
		try {
			String[] hightLightFields = { "Title" };
			PepperSortField dsSortField = new PepperSortField("Doc_Time", FieldType.Long, true);
			PepperSortField[] sortFields = { dsSortField };
			PepperResult result = indexService.search(getIndexPath(2), "Title:开启", sortFields, hightLightFields, 0, 10);
			if (null != result && result.totalHits > 0) {
				for (Document document : result.documents) {
					System.out.println(document.get("Title"));
				}
			}
		} catch (PepperException e) {
			e.printStackTrace();
		}
	}

}
