package com.wuming.common.constant;

/**
 * 缓存的key 常量
 * 
 * @author wuming
 */
public class CacheConstants
{
    /**
     * 登录用户 redis key
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";

    /**
     * 字典管理 cache key
     */
    public static final String SYS_DICT_KEY = "sys_dict:";

    /**
     * 防重提交 redis key
     */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    /**
     * 限流 redis key
     */
    public static final String RATE_LIMIT_KEY = "rate_limit:";

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";

    /**
     * 默认红包奖励配置KEY
     */
    public static final String PRIZE_CONFIG_KEY = "sys_prize:default_config";
    /**
     * 默认红包奖励配置KEY
     */
    public static final String PRIZE_LEVEL_KEY = "sys_prize:level_config";
    /**
     * 10天联系打卡红包奖励配置KEY
     */
    public static final String PRIZE_TEN_CONFIG_KEY = "sys_prize:ten_config";
    /**
     * 25天联系打卡红包奖励配置KEY
     */
    public static final String PRIZE_25_CONFIG_KEY = "sys_prize:twenty_five_config";

    /**
     * 用户可以抽的奖金
     */
    public static final String PRIZE_USER_PRICE_ALL = "sys_prize:user_prize_all";
    /**
     * 支付配置
     */
    public static final String WX_PAY_CONFIG = "sys_config:wx_pay:config";
}
