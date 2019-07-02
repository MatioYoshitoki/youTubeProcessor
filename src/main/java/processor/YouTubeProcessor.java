package processor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;
import us.codecraft.webmagic.selector.Html;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matioyoshitoki on 2019/4/15.
 */
public class YouTubeProcessor extends BaseProcessor {


    int index = 1 ;
    String videoTag ;

    public YouTubeProcessor(String videoTag) {
        super();
        this.videoTag = videoTag;
    }

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
//        System.out.println(html);
        List<String> videoImageUrls = html.xpath("//div[@id='img-preload']/img/@src").all();
        if (videoImageUrls.size()>0) {
            JSONArray array = new JSONArray();
            for (String videoImageUrl : videoImageUrls) {
                String videoID = videoImageUrl.split("/")[Arrays.asList(videoImageUrl.split("/")).indexOf("vi") + 1];
                JSONObject data = new JSONObject();
                data.put("videoID", videoID);
                array.add(data);
                System.out.println(videoID);
            }
            page.putField("video", array);
            index++;
            try {
                page.addTargetRequest("https://www.youtube.com/results?search_query=" + URLEncoder.encode(videoTag.trim(),"utf-8") + "&page=" + index);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }
}
