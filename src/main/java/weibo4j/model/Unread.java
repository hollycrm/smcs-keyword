package weibo4j.model;

import weibo4j.http.Response;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;


public class Unread {

    private Long status;
    private Long follower;
    private Long cmt;
    private Long dm;
    private Long mentionStatus;
    private Long mentionCmt;
    private Long group;
    private Long privateGroup;
    private Long notice;
    private Long invite;
    private Long badge;
    private Long photo;

    public Unread(Response response) throws WeiboException {
        JSONObject jsonObject = response.asJSONObject();
        try {
            this.status = jsonObject.getLong("status");
            this.follower = jsonObject.getLong("follower");
            this.cmt = jsonObject.getLong("cmt");
            this.dm = jsonObject.getLong("dm");
            this.mentionStatus = jsonObject.getLong("mention_status");
            this.mentionCmt = jsonObject.getLong("mention_cmt");
            this.group = jsonObject.getLong("group");
            this.privateGroup = jsonObject.getLong("private_group");
            this.notice = jsonObject.getLong("notice");
            this.invite = jsonObject.getLong("invite");
            this.badge = jsonObject.getLong("badge");
            this.photo = jsonObject.getLong("photo");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public Long getFollower() {
        return follower;
    }

    public void setFollower(Long follower) {
        this.follower = follower;
    }

    public Long getCmt() {
        return cmt;
    }

    public void setCmt(Long cmt) {
        this.cmt = cmt;
    }

    public Long getDm() {
        return dm;
    }

    public void setDm(Long dm) {
        this.dm = dm;
    }

    public Long getMentionStatus() {
        return mentionStatus;
    }

    public void setMentionStatus(Long mentionStatus) {
        this.mentionStatus = mentionStatus;
    }

    public Long getMentionCmt() {
        return mentionCmt;
    }

    public void setMentionCmt(Long mentionCmt) {
        this.mentionCmt = mentionCmt;
    }

    public Long getGroup() {
        return group;
    }

    public void setGroup(Long group) {
        this.group = group;
    }

    public Long getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(Long privateGroup) {
        this.privateGroup = privateGroup;
    }

    public Long getNotice() {
        return notice;
    }

    public void setNotice(Long notice) {
        this.notice = notice;
    }

    public Long getInvite() {
        return invite;
    }

    public void setInvite(Long invite) {
        this.invite = invite;
    }

    public Long getBadge() {
        return badge;
    }

    public void setBadge(Long badge) {
        this.badge = badge;
    }

    public Long getPhoto() {
        return photo;
    }

    public void setPhoto(Long photo) {
        this.photo = photo;
    }
}
