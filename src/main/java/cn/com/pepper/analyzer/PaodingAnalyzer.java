package cn.com.pepper.analyzer;

import java.util.Properties;
import net.paoding.analysis.Constants;
import net.paoding.analysis.analyzer.PaodingAnalyzerBean;
import net.paoding.analysis.knife.Paoding;
import net.paoding.analysis.knife.PaodingMaker;
import org.apache.lucene.analysis.Analyzer;

/**
 * Chinese word segmentation machine
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class PaodingAnalyzer implements IAnalyzer {
	private String dicPath;

	public PaodingAnalyzer(String dicPath) {
		this.dicPath = dicPath;
	}

	public Analyzer getAnalyzer(String analyzerMode) {
		PaodingAnalyzerBean paodingAnalyzer = new PaodingAnalyzerBean();
		Properties properties = PaodingMaker.getProperties("classpath:paoding-analysis.properties", this.dicPath);
		Paoding paoding = PaodingMaker.make(properties, this.dicPath);
		paodingAnalyzer.setKnife(paoding);
		if (analyzerMode == null) {
			analyzerMode = Constants.getProperty(properties, "paoding.analyzer.mode");
		}
		paodingAnalyzer.setMode(analyzerMode);
		return paodingAnalyzer;
	}
}
