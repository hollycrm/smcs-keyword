package weibo4j.model;

import weibo4j.http.Response;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author caozf
 */
public class Count extends WeiboResponse implements Serializable {

    private static final long serialVersionUID = 1l;

    private Long id;
    private Long comments;
    private Long reposts;

    public static List<Count> constructCounts(Response res) throws WeiboException {
        JSONArray jsonArray = res.asJSONArray();
        List<Count> counts = new ArrayList<Count>(jsonArray.length());
        try {
            for (int i = 0; i < counts.size(); i++) {
                Count count = new Count();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                count.setId(jsonObject.getLong("id"));
                count.setComments(jsonObject.getLong("comments"));
                count.setReposts(jsonObject.getLong("reposts"));
                counts.add(count);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getComments() {
        return comments;
    }

    public void setComments(Long comments) {
        this.comments = comments;
    }

    public Long getReposts() {
        return reposts;
    }

    public void setReposts(Long reposts) {
        this.reposts = reposts;
    }
}
