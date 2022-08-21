package me.goldze.mvvmhabit.utils.constant;

/**
 * 正则相关常量
 */
public class RegexConstants {

    /**
     * 本地的正则:匹配价格
     */
    private static final String REGEX_PRICE_LOCAL = "(\\$|¥|P|p|特价|批发价|批|批发|批价|￥|🅿|💱|🉐|💰|[红包]|售价|包邮|价格|价|代.价|团购价|让利价|放货价|加盟价|拿货价|工厂价|档口价|特惠价|清仓价|清仓)(\\s)*(\\s)*[0-9\\.]+|[0-9\\.]+(包邮|￥|¥|\\$|P|p|特价|批发价|批|批发|批价|块|元|人民币|🅿|💱|🉐|💰|[红包]|售价|价格|价|代.价|团购价|让利价|放货价|加盟价|拿货价|工厂价|档口价|特惠价|清仓价|清仓)";

    /**
     * 匹配数字
     */
    public static String REGEX_GET_NUME = "[0-9\\.]+";

    /**
     * 服务器的正则:匹配价格(需要通过接口获取)
     */
    public static String REGEX_PRICE_ONLINE = "";

    /**
     * 获取匹配价格的正则表达式
     *
     * @return
     */
    public static String getPriceRegex() {
        return RegexConstants.REGEX_PRICE_ONLINE.isEmpty() ? RegexConstants.REGEX_PRICE_LOCAL : RegexConstants.REGEX_PRICE_ONLINE;
    }

}
