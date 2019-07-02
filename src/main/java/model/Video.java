package model;

import com.jfinal.ext.plugin.tablebind.TableBind;
import com.jfinal.plugin.activerecord.Model;

/**
 * Created by matioyoshitoki on 2019/4/15.
 */
@TableBind(tableName = "Video")
public class Video extends Model<Video> {

    public static final Video dao = new Video();

}
