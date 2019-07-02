package config;

import com.jfinal.ext.plugin.cron.Cron4jPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import model.Video;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Properties;

/**
 * @note 初始化程序的配置
 * @author sxy
 * @date 2016年7月5日
 */
public class Config {

	private Logger logger = Logger.getLogger(getClass());

	public void initAll() throws Exception {
		initPid();
		initDB();
		initMongo();
//		initTiming();
		
		logger.warn("finish initAll()");
	}

	// 初始化pid文件
	private void initPid() throws IOException {
		FileOutputStream out = new FileOutputStream(new File("/Cartip_catch/weibo_pid"));
		String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];

		out.write(pid.getBytes());
		out.close();
		logger.warn("finish initPid()");
	}

	// 初始化jfinal-ext的db连接
	public void initDB() throws IOException {
		Properties p = new Properties();
		p.load(getClass().getResourceAsStream("/db.properites"));
		System.out.println(p.getProperty("db")+"::::"+p.getProperty("user"));

		DruidPlugin dp = new DruidPlugin(p.getProperty("db"), p.getProperty("user"), p.getProperty("password"));
		dp.set(Integer.valueOf(p.getProperty("initialSize")), Integer.valueOf(p.getProperty("minIdle")),
				Integer.valueOf(p.getProperty("maxActive")));
		dp.setRemoveAbandonedTimeoutMillis(6 * 1000);
//		System.out.println(dp);
//		ActiveMqProvider ap = new ActiveMqProvider();
		ActiveRecordPlugin arp = new ActiveRecordPlugin(dp);

		arp.addMapping("video", Video.class);

//		AutoTableBindPlugin atbp = new AutoTableBindPlugin(dp);
//		atbp.autoScan(false);
//		atbp.addScanPackages("cn.rebind.model");
//		atbp.addMapping("Tieba", Tieba.class);
//		atbp.addMapping("Xcar", Xcar.class);
//		System.out.println(atbp);

//		atbp.setShowSql(false);

		dp.start();
//		atbp.start();
		arp.start();
		logger.warn("finish initDB()");
	}

	// 初始化mongodb
	private void initMongo() throws Exception {
//		MyMongodbPlugin mmp = new MyMongodbPlugin();
//		mmp.start();

		logger.warn("finish initMongo()");
	}

	// 初始化定时任务
	private void initTiming() {
		Cron4jPlugin c4p = new Cron4jPlugin();
		c4p.config(getClass().getResource("/job.properties").getFile());
		c4p.start();
		logger.warn("finish initTiming()");
	}

}
