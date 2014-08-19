package weibo4j;

import weibo4j.model.PostParameter;
import weibo4j.model.Unread;
import weibo4j.model.WeiboException;


public class Remind extends Request {

    public Remind(Weibo weibo) {
        super(weibo);
    }

    public Unread getUnread(String uid) throws WeiboException {
        return new Unread(weibo.getClient().get(
                "https://rm.api.weibo.com/2/remind/unread_count.json",
                new PostParameter[]{new PostParameter("uid", uid)}
        ));
    }

    @Override
    public String getPrefix() {
        return null;
    }
}
