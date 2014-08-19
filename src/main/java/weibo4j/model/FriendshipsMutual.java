package weibo4j.model;

import java.io.Serializable;

import weibo4j.http.Response;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class FriendshipsMutual extends WeiboResponse
  implements Serializable
{
  private Long sourceId;
  private boolean sourceFollowed;
  private boolean sourceFollowing;
  private Long targetId;
  private boolean targetFollowed;
  private boolean targetFollowing;

  public FriendshipsMutual(Response res)
    throws WeiboException
  {
    super(res);
    try {
      JSONObject json = null;
      json = res.asJSONObject();
      JSONObject source = json.getJSONObject("source");
      JSONObject target = json.getJSONObject("target");

      this.sourceId = Long.valueOf(source.getLong("id"));
      this.sourceFollowed = source.getBoolean("followed_by");
      this.sourceFollowing = source.getBoolean("following");

      this.targetId = Long.valueOf(source.getLong("id"));
      this.targetFollowed = source.getBoolean("followed_by");
      this.targetFollowing = source.getBoolean("following");
    }
    catch (JSONException e) {
      e.printStackTrace();
    }
  }

  public Long getSourceId()
  {
    return this.sourceId;
  }

  public void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }

  public boolean isSourceFollowed() {
    return this.sourceFollowed;
  }

  public void setSourceFollowed(boolean sourceFollowed) {
    this.sourceFollowed = sourceFollowed;
  }

  public boolean isSourceFollowing() {
    return this.sourceFollowing;
  }

  public void setSourceFollowing(boolean sourceFollowing) {
    this.sourceFollowing = sourceFollowing;
  }

  public Long getTargetId() {
    return this.targetId;
  }

  public void setTargetId(Long targetId) {
    this.targetId = targetId;
  }

  public boolean isTargetFollowed() {
    return this.targetFollowed;
  }

  public void setTargetFollowed(boolean targetFollowed) {
    this.targetFollowed = targetFollowed;
  }

  public boolean isTargetFollowing() {
    return this.targetFollowing;
  }

  public void setTargetFollowing(boolean targetFollowing) {
    this.targetFollowing = targetFollowing;
  }

  @Override
	public String toString()
  {
    return "FriendshipsMutual{sourceId=" + this.sourceId + ", sourceFollowed=" + this.sourceFollowed + ", sourceFollowing=" + this.sourceFollowing + ", targetId=" + this.targetId + ", targetFollowed=" + this.targetFollowed + ", targetFollowing=" + this.targetFollowing + '}';
  }
}