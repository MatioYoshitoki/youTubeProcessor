
import com.jfinal.ext.plugin.cron.Cron4jPlugin;
import config.Config;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;
import pipeline.NormalPipeline;
import processor.YouTubeProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 程序入口
 */
public class Entry {

	private Logger logger = Logger.getLogger(getClass());

	public static void main(String[] args) throws UnsupportedEncodingException {
		// 初始化配置
		try {
			new Config().initAll();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		initService();
	}

	/*启动服务*/
	private static void initService() throws UnsupportedEncodingException {

		String[] tags = "autism, ASD, autism spectrum disorder, Asperger’s, autistic child, autistic kid, autistic behavior, PDD-NOS, son, daughter, child, birthday party".split(",");
		for (String tag:tags) {
			Spider spider = Spider.create(new YouTubeProcessor(tag.trim()));
			Request request = new Request("https://www.youtube.com/results?search_query="+ URLEncoder.encode(tag.trim(),"utf-8") +"&page=1");
			spider.addRequest(request);
			HttpClientDownloader downloader = new HttpClientDownloader();
			downloader.setProxyProvider(SimpleProxyProvider.from(new Proxy("127.0.0.1", 57686)));
			spider.setDownloader(downloader);
			spider.addPipeline(new NormalPipeline(tag.trim()));
			spider.thread(1).start();
			System.out.println(tag+" 启动");
		}

//		Cron4jPlugin c4p = new Cron4jPlugin();
//		c4p.config("job.properties");
//		c4p.start();

	}

}
