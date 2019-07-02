package kit;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import config.Config;
import model.Video;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class JavaYoutubeDownloader {

    public static String newline = System.getProperty("line.separator");
    private static final Logger log = Logger.getLogger(JavaYoutubeDownloader.class.getCanonicalName());
    private static final Level defaultLogLevelSelf = Level.FINER;
    private static final Level defaultLogLevel = Level.WARNING;
    private static final Logger rootlog = Logger.getLogger("");
    private static final String scheme = "https";
    private static final String host = "www.youtube.com";
    private static final Pattern commaPattern = Pattern.compile(",");
    private static final Pattern pipePattern = Pattern.compile("\\/");
    private static final char[] ILLEGAL_FILENAME_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    private static void usage(String error) {
        if (error != null) {
            System.err.println("Error: " + error);
        }
        System.err.println("usage: JavaYoutubeDownload VIDEO_ID DESTINATION_DIRECTORY");
        System.exit(-1);
    }

    public static void youTubeDownload(String videoId, String tag) throws UnsupportedEncodingException {

//        ]
        try {
//            String videoUrl = "https://www.youtube.com/watch?v=f53_dDMCLw4";
            setupLogging();

            log.fine("Starting");
//            String videoId = "f53_dDMCLw4";
            String outdir = "/Users/sunying/Desktop/video/"+tag+"/";
            // TODO Ghetto command line parsing
            int format = 18; // http://en.wikipedia.org/wiki/YouTube#Quality_and_codecs
            String encoding = "UTF-8";
            String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13";
            File outputDir = new File(outdir);
            String extension = getExtension(format);

            play(videoId, format, encoding, userAgent, outputDir, extension);

        } catch (Throwable t) {
            t.printStackTrace();
        }
        log.fine("Finished");
    }

    private static String getExtension(int format) {
        // TODO
        return "mp4";
    }

    private static void play(String videoId, int format, String encoding, String userAgent, File outputdir, String extension) throws Throwable {
        log.fine("Retrieving " + videoId);
        List<NameValuePair> qparams = new ArrayList<NameValuePair>();
        qparams.add(new BasicNameValuePair("video_id", videoId));
        qparams.add(new BasicNameValuePair("fmt", "" + format));
        //cplayer=UNIPLAYER&cbr=Chrome&cos=Macintosh
        qparams.add(new BasicNameValuePair("html5","1"));
        qparams.add(new BasicNameValuePair("ps","desktop-polymer"));
        qparams.add(new BasicNameValuePair("el","adunit"));
        qparams.add(new BasicNameValuePair("hl","zh_CN"));
        qparams.add(new BasicNameValuePair("c","WEB"));
        qparams.add(new BasicNameValuePair("cplayer","UNIPLAYER"));
        qparams.add(new BasicNameValuePair("cbr","Chrome"));
        qparams.add(new BasicNameValuePair("cos","Macintosh"));

        URI uri = getUri("get_video_info", qparams);

        CookieStore cookieStore = new BasicCookieStore();
        HttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        HttpHost proxy = new HttpHost("127.0.0.1",50341);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();

        HttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(uri);
        httpget.setConfig(requestConfig);
        httpget.setHeader("User-Agent", userAgent);

        log.finer("Executing " + uri);
        HttpResponse response = httpclient.execute(httpget, localContext);
        HttpEntity entity = response.getEntity();
        if (entity != null && response.getStatusLine().getStatusCode() == 200) {
            InputStream instream = entity.getContent();
            String videoInfo = getStringFromInputStream(encoding, instream);
            if (videoInfo != null && videoInfo.length() > 0) {
                List<NameValuePair> infoMap = new ArrayList<NameValuePair>();
                URLEncodedUtils.parse(infoMap, new Scanner(videoInfo), encoding);
                String token = null;
                String downloadUrl = null;
                String filename = videoId;
//                System.out.println(infoMap);

                for (NameValuePair pair : infoMap) {
                    String key = pair.getName();
                    String val = pair.getValue();
                    log.finest(key + "=" + val);
                    System.out.println(key + "=" + val);
                    if (key.equals("token")) {
                        token = val;
                    } else if (key.equals("title")) {
                        filename = val;
//                        Db.update("update Video set title='"+val+"' where videoID='"+videoId+"'");
                    } else if (key.equals("fmt_list!!")) {
//                        String[] formats = commaPattern.split(val);
//                        for (String fmt : formats) {
//                            String[] fmtPieces = pipePattern.split(fmt);
//                            System.out.println(fmtPieces.length);
//                            if (fmtPieces.length == 2) {
//                                // in the end, download somethin!
//                                downloadUrl = fmtPieces[1];
//                                int pieceFormat = Integer.parseInt(fmtPieces[0]);
//                                if (pieceFormat == format) {
//                                    // found what we want
//                                    downloadUrl = fmtPieces[1];
//                                    break;
//                                }
//                            }
//                        }
                    }else if (key.equals("player_response")){
                        JSONObject obj = JSONObject.parseObject(val);
////                        System.out.println(videoId+":"+obj);
                        if (obj.containsKey("streamingData")){
                            if (obj.getJSONObject("streamingData").containsKey("formats")) {
                                downloadUrl = obj.getJSONObject("streamingData").getJSONArray("formats").getJSONObject(0).getString("url");
                            }
                        }
//                        System.out.println(obj.getJSONObject("streamingData").getJSONArray("formats"));
                    }else if (key.equals("url_encoded_fmt_stream_map")){
                        List<NameValuePair> urlStremMap = new ArrayList<NameValuePair>();
                        URLEncodedUtils.parse(urlStremMap, new Scanner(val), encoding);
                        for (NameValuePair p : urlStremMap) {

                            if (p.getName().equals("url")){
                                downloadUrl = p.getValue();
                            }

                        }
                    }
                }
                System.out.println(outputdir+"===>"+outputdir.exists());
                System.out.println();

                if (!outputdir.exists()){
                    outputdir.mkdir();
                }

                filename = cleanFilename(filename);
                if (filename.length() == 0) {
                    filename = videoId;
                } else {
                    filename += "_" + videoId;
                }
                filename += "." + extension;
//                File outputfile = new File(outputdir, filename);

                if (downloadUrl != null) {
                    //downloadWithHttpClient(userAgent, downloadUrl, outputfile);
                    System.out.println(outputdir+filename);
                    downloadNet(downloadUrl, outputdir+"/"+filename, videoId);
                }
            }
        }
    }

    private static void downloadWithHttpClient(String userAgent, String downloadUrl, File outputfile) throws Throwable {
//        https://r2---sn-jxnj5-cjoe.googlevideo.com/videoplayback?id=o-APt793w78RXmOECTmGov_qSrqH0h0Kl0PFTTm2k-MZGZ&itag=18&source=youtube&requiressl=yes&mm=31%2C29&mn=sn-jxnj5-cjoe%2Csn-oguelney&ms=au%2Crdu&mv=m&pl=17&ei=c-CzXPmpOYy_gQPxvJCIBA&initcwndbps=492500&mime=video%2Fmp4&gir=yes&clen=265145713&ratebypass=yes&dur=3231.428&lmt=1549684530446251&mt=1555292143&fvip=3&c=WEB&txp=5531432&ip=52.231.201.49&ipbits=0&expire=1555313876&sparams=ip%2Cipbits%2Cexpire%2Cid%2Citag%2Csource%2Crequiressl%2Cmm%2Cmn%2Cms%2Cmv%2Cpl%2Cei%2Cinitcwndbps%2Cmime%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&signature=60934C04691DEAAA16AA79F6680753A7A4538A8D.4CBFE15D80F77BA1EC277C5A6B4F9F5B3057E4F6&key=yt8

        HttpHost proxy = new HttpHost("127.0.0.1",50341);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();

        HttpGet httpget2 = new HttpGet(downloadUrl);
        httpget2.setHeader("User-Agent", userAgent);
        httpget2.setConfig(requestConfig);

        log.finer("Executing " + httpget2.getURI());
        HttpClient httpclient2 = HttpClients.createDefault();;
        HttpResponse response2 = httpclient2.execute(httpget2);
        HttpEntity entity2 = response2.getEntity();
        if (entity2 != null && response2.getStatusLine().getStatusCode() == 200) {
            long length = entity2.getContentLength();
            InputStream instream2 = entity2.getContent();
            log.finer("Writing " + length + " bytes to " + outputfile);
            if (outputfile.exists()) {
                outputfile.delete();
            }
            FileOutputStream outstream = new FileOutputStream(outputfile);
            try {
                byte[] buffer = new byte[2048];
                int count = -1;
                while ((count = instream2.read(buffer)) != -1) {
                    outstream.write(buffer, 0, count);
                }
                outstream.flush();
            } finally {
                outstream.close();
            }
        }
    }

    public static void downloadNet(String downloadUrl, String outDir, String videoID) throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL(downloadUrl);

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(outDir);

            byte[] buffer = new byte[1024];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
//                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
                bytesum += byteread;
            }
            Db.update("update Video set finishFlag=1 where videoID='"+videoID+"'");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String cleanFilename(String filename) {
        for (char c : ILLEGAL_FILENAME_CHARACTERS) {
            filename = filename.replace(c, '_');
        }
        return filename;
    }

    private static URI getUri(String path, List<NameValuePair> qparams) throws URISyntaxException {
        URI uri = URIUtils.createURI(scheme, host, -1, "/" + path, URLEncodedUtils.format(qparams, "UTF-8"), null);
        System.out.println(uri);
        return uri;
    }

    private static void setupLogging() {
        changeFormatter(new Formatter() {
            @Override
            public String format(LogRecord arg0) {
                return arg0.getMessage() + newline;
            }
        });
        explicitlySetAllLogging(Level.FINER);
    }

    private static void changeFormatter(Formatter formatter) {
        Handler[] handlers = rootlog.getHandlers();
        for (Handler handler : handlers) {
            handler.setFormatter(formatter);
        }
    }

    private static void explicitlySetAllLogging(Level level) {
        rootlog.setLevel(Level.ALL);
        for (Handler handler : rootlog.getHandlers()) {
            handler.setLevel(defaultLogLevelSelf);
        }
        log.setLevel(level);
        rootlog.setLevel(defaultLogLevel);
    }

    private static String getStringFromInputStream(String encoding, InputStream instream) throws UnsupportedEncodingException, IOException {
        Writer writer = new StringWriter();

        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(instream, encoding));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            instream.close();
        }
        String result = writer.toString();
        return result;
    }

    public static void main(String[] args) {
        try {
            new Config().initDB();
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<Video> videos = Video.dao.find("select * from Video where finishFlag=0");

        for (Video v0:videos){
            String vid =v0.getStr("videoID");
            String tag = v0.getStr("videoTag");
            try {
                youTubeDownload(vid, tag);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

}