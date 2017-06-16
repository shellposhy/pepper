package cn.com.pepper;

import cn.com.pepper.analyzer.PaodingAnalyzer;

/**
 * The <code>Configuration</code> class is the base configure declaration.
 * <p>
 * 
 * @author shellpo shih
 * @version 1.0
 */
public class Configuration {
	private PaodingAnalyzer analyzerFactory;

	public static enum DirectoryType {
		Simple_FS, NIO_FS, MMap, RAM;
	}

	private String hightLightPreTag = "<font color=\"red\">";
	private String hightLightPostTag = "</font>";
	private int fragmentSize = 100;
	private String writerAnalyzerMode = "most-words";
	private String hightLightAnalyzerMode = "most-words";
	private String parserAnalyzerMode = "most-words";
	private DirectoryType defaultDirectoryType;
	private int defaultNumHits = 10;
	private boolean runOnNFS = false;

	public Configuration() {
		setDefaultDirectoryType();
	}

	public PaodingAnalyzer getAnalyzerFactory() {
		return this.analyzerFactory;
	}

	public void setAnalyzerFactory(PaodingAnalyzer analyzerFactory) {
		this.analyzerFactory = analyzerFactory;
	}

	public String getHightLightPreTag() {
		return this.hightLightPreTag;
	}

	public void setHightLightPreTag(String hightLightPreTag) {
		this.hightLightPreTag = hightLightPreTag;
	}

	public String getHightLightPostTag() {
		return this.hightLightPostTag;
	}

	public void setHightLightPostTag(String hightLightPostTag) {
		this.hightLightPostTag = hightLightPostTag;
	}

	public int getFragmentSize() {
		return this.fragmentSize;
	}

	public void setFragmentSize(int fragmentSize) {
		this.fragmentSize = fragmentSize;
	}

	public String getWriterAnalyzerMode() {
		return this.writerAnalyzerMode;
	}

	public void setWriterAnalyzerMode(String writerAnalyzerMode) {
		this.writerAnalyzerMode = writerAnalyzerMode;
	}

	public String getHightLightAnalyzerMode() {
		return this.hightLightAnalyzerMode;
	}

	public void setHightLightAnalyzerMode(String hightLightAnalyzerMode) {
		this.hightLightAnalyzerMode = hightLightAnalyzerMode;
	}

	public String getParserAnalyzerMode() {
		return this.parserAnalyzerMode;
	}

	public void setParserAnalyzerMode(String parserAnalyzerMode) {
		this.parserAnalyzerMode = parserAnalyzerMode;
	}

	public DirectoryType getDefaultDirectoryType() {
		return this.defaultDirectoryType;
	}

	public void setDefaultDirectoryType(DirectoryType defaultDirectoryType) {
		this.defaultDirectoryType = defaultDirectoryType;
	}

	public int getDefaultNumHits() {
		return this.defaultNumHits;
	}

	public void setDefaultNumHits(int defaultNumHits) {
		this.defaultNumHits = defaultNumHits;
	}

	public boolean isRunOnNFS() {
		return this.runOnNFS;
	}

	public void setRunOnNFS(boolean runOnNFS) {
		this.runOnNFS = runOnNFS;
	}

	private void setDefaultDirectoryType() {
		if (System.getProperty("os.name").toLowerCase().indexOf("window") > -1) {
			this.defaultDirectoryType = DirectoryType.Simple_FS;
		} else {
			this.defaultDirectoryType = DirectoryType.NIO_FS;
		}
	}
}
