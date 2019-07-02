package processor;

import config.Parameters;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

abstract class BaseProcessor implements PageProcessor {


	protected Site site = Site.me().setTimeOut(Parameters.TIME_OUT).setRetryTimes(Parameters.RETRY_TIMES).setSleepTime(Parameters.SLEEP_TIME)
			.setCharset("utf-8").setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");

	public BaseProcessor(){

	}

	@Override
	public void process(Page page) {

	}

	public Site getSite() {
		return site;
	}

}
