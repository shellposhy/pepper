package cn.com.pepper.parser;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.util.Version;

/**
 * A QueryParser which constructs queries to search multiple fields.
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class MdQueryParser extends MultiFieldQueryParser {
	public MdQueryParser(Version matchVersion, String[] fields, Analyzer analyzer) {
		super(matchVersion, fields, analyzer);
	}

	/**
	 * The Range Query
	 * 
	 * @param field
	 * @param part1
	 * @param part2
	 * @param inclusive
	 * @return
	 */
	protected Query getRangeQuery(String field, String part1, String part2, boolean inclusive) throws ParseException {
		TermRangeQuery query = (TermRangeQuery) super.getRangeQuery(field, part1, part2, inclusive);
		if (field.startsWith("#int#")) {
			return NumericRangeQuery.newIntRange(field.substring(5),
					Integer.valueOf(Integer.parseInt(query.getLowerTerm())),
					Integer.valueOf(Integer.parseInt(query.getUpperTerm())), query.includesLower(),
					query.includesUpper());
		}
		if (field.startsWith("#long#")) {
			return NumericRangeQuery.newLongRange(field.substring(6),
					Long.valueOf(Long.parseLong(query.getLowerTerm())),
					Long.valueOf(Long.parseLong(query.getUpperTerm())), query.includesLower(), query.includesUpper());
		}
		if (field.startsWith("#float#")) {
			return NumericRangeQuery.newFloatRange(field.substring(7),
					Float.valueOf(Float.parseFloat(query.getLowerTerm())),
					Float.valueOf(Float.parseFloat(query.getUpperTerm())), query.includesLower(),
					query.includesUpper());
		}
		if (field.startsWith("#double#")) {
			return NumericRangeQuery.newDoubleRange(field.substring(8),
					Double.valueOf(Double.parseDouble(query.getLowerTerm())),
					Double.valueOf(Double.parseDouble(query.getUpperTerm())), query.includesLower(),
					query.includesUpper());
		}
		return query;
	}

	/**
	 * The Field Query
	 * 
	 * @param field
	 * @param queryText
	 * @param quoted
	 * @return
	 */
	protected Query getFieldQuery(String field, String queryText, boolean quoted) throws ParseException {
		if (queryText.startsWith("#")) {
			return newTermQuery(new Term(field, queryText.substring(1, queryText.lastIndexOf("#"))));
		}
		if (field.startsWith("#int#")) {
			return NumericRangeQuery.newIntRange(field.substring(5), Integer.valueOf(Integer.parseInt(queryText)),
					Integer.valueOf(Integer.parseInt(queryText)), true, true);
		}
		if (field.startsWith("#long#")) {
			return NumericRangeQuery.newLongRange(field.substring(6), Long.valueOf(Long.parseLong(queryText)),
					Long.valueOf(Long.parseLong(queryText)), true, true);
		}
		if (field.startsWith("#float#")) {
			return NumericRangeQuery.newFloatRange(field.substring(7), Float.valueOf(Float.parseFloat(queryText)),
					Float.valueOf(Float.parseFloat(queryText)), true, true);
		}
		if (field.startsWith("#double#")) {
			return NumericRangeQuery.newDoubleRange(field.substring(8), Double.valueOf(Double.parseDouble(queryText)),
					Double.valueOf(Double.parseDouble(queryText)), true, true);
		}
		return super.getFieldQuery(field, queryText, quoted);
	}
}
