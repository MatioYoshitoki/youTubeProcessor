package pipeline;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.whos.sa.analysis.Analysis;
import model.Video;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by matioyoshitoki on 2017/12/18.
 */
public class NormalPipeline implements Pipeline {

    private String tag ;

    public NormalPipeline(String tag){
        this.tag = tag;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        if (resultItems.get("video") != null) {
//            System.out.println(resultItems.get("reviews").toString());
            save(resultItems.get("video"));
        }
    }

    private void save(JSONArray array){
//        System.out.println("从这来:"+data.toJSONString());
        Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {
                for (Object object:array){
                    JSONObject data = (JSONObject)object;
                    if (Video.dao.find("select * from Video where videoID='"+data.getString("videoID")+"'").size()==0){
                        Video video = new Video();
                        video.set("videoID",data.getString("videoID"));
                        video.set("finishFlag",0);
                        video.set("videoTag",tag);
                        video.save();
                    }
                }

                return true;
            }
        });
    }


}
