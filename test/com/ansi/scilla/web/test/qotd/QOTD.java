package com.ansi.scilla.web.test.qotd;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.qotd.servlet.MotdServlet;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class QOTD {

	
	public static void main(String[] args) {
		try {
//			new QOTD().go();
//			new QOTD().regex();
			new QOTD().testServlet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void testServlet() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			MotdServlet servlet = new MotdServlet();
			String message = servlet.processGet(conn);
			System.out.println(message);
		} finally {
			conn.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void go() throws Exception {
		final URL feedUrl= new URL("http://feeds.feedburner.com/QuotesDay?format=xml");
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(feedUrl));
		List<SyndEntry> x = feed.getEntries();
		SyndEntry y = x.get(0);
		List<SyndContent> z = y.getContents();
		SyndContent aa = z.get(0);
		System.out.println(aa.getValue());
		Pattern numberPattern = Pattern.compile("(^.*<h2>Funny Quote of the Day</h2><p><em>)(.*)(<br />- <a href=http://www.quotes-day.com/by/love.*$)", Pattern.CASE_INSENSITIVE);
		Matcher hasNumbers = numberPattern.matcher(aa.getValue());
		System.out.println(hasNumbers.matches());
		System.out.println(hasNumbers.group(2));
	}
	
	
	public void regex() {
		String password = "<h1><a href=\"http://www.quotes-day.com/\">Quote of the Day</a> for Wednesday December 14, 2016</h1><br /><h2>Positive Quote of the Day</h2><p><em>The winds of grace are always blowing;<br />all we need to do is raise our sails.<br />- <a href=http://www.quotes-day.com/by/anonymous/>Anonymous</a></em></p><h2>Funny Quote of the Day</h2><p><em>I got a perfect build for clothes.<br />I'm a twenty-eight dwarf.<br />- <a href=http://www.quotes-day.com/by/love-and-death/>Love and Death</a></em></p><h2>Love Quote of the Day</h2><p><em>As we grow old, the beauty steals inward.<br />- <a href=http://www.quotes-day.com/by/ralph-waldo-emerson/>Ralph Waldo Emerson</a></em></p><p>Also see <a href=\"http://www.lifesayingsquotes.com/proverbs.php\">Proverbs</a></p>";
		Pattern numberPattern = Pattern.compile("(^.*<h2>Funny Quote of the Day</h2><p><em>)(.*)(<br />- <a href=http://www.quotes-day.com/by/love.*$)", Pattern.CASE_INSENSITIVE);
		Matcher hasNumbers = numberPattern.matcher(password);
		System.out.println(hasNumbers.matches());
		for ( int i = 0; i <= hasNumbers.groupCount(); i++ ) {
			System.out.println(i + "\t" + hasNumbers.group(i));
		}
	}

}
