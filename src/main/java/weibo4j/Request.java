package weibo4j;

import weibo4j.util.WeiboConfig;

/**
 * @author caozf
 */
public abstract class Request {

    protected Weibo weibo;

    protected static final String baseURL = WeiboConfig.getValue("baseURL");

    public Request(Weibo weibo) {
        this.weibo = weibo;
    }

    protected String getBaseUrl() {
        return baseURL + getPrefix();
    }

    public abstract String getPrefix();

}
