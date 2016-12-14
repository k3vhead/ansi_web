package com.ansi.scilla.web.test.qotd;

import java.net.URL;
import java.util.List;

import com.ansi.scilla.web.test.TesterUtils;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class QOTD {

	
	public static void main(String[] args) {
		try {
			TesterUtils.makeLoggers();
			new QOTD().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		final URL feedUrl= new URL("http://feeds.feedburner.com/QuotesDay?format=xml");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedUrl));
		List<SyndEntry> x = feed.getEntries();
		SyndEntry y = x.get(0);
		List<SyndContent> z = y.getContents();
		SyndContent aa = z.get(0);
		System.out.println(aa.getValue());
	}
	
	
	

}
