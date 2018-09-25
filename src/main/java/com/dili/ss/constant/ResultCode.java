package com.dili.ss.constant;

/**
 *
 */
public class ResultCode {

    /**200:成功
     *  <li>前端是否可直接显示后台返回的message:是</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String OK="200";

    /**
     * 500:数据参数异常，业务系统可以直接展示提示信息
     */
    public static final String DATA_PARAM_ERROR = "500";


    /**1000:输入参数错误(输入参数类型、值、null等错误) <br/>
     *  <li>前端是否可直接显示后台返回的message:是</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String PARAMS_ERROR="1000";


    /**2000：权限错误(未登录，数据权限不满足，功能权限不满足等错误)<br/>
     *  <li>前端是否可直接显示后台返回的message:是</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String NOT_AUTH_ERROR="2000";

    /**3000:业务逻辑或数据错误(未查询到数据，数据验证不通过，数据发生变化等错误)<br/>
     *  <li>前端是否可直接显示后台返回的message:是</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String DATA_ERROR="3000";

    /** 4000:支付异常
     *  <li>前端是否可直接显示后台返回的message:是</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String PAYMENT_ERROR="4000";

    /**5000:服务器内部错误(系统错误，代码BUG,系统间调用超时等错误)
     *  <li>前端是否可直接显示后台返回的message:是。此消息内部已做过处理</li>
     *  <li>是否有出现错误的数据返回给前端：无</li>
     */
    public static final String APP_ERROR="5000";

    /**
     * 101:无效的token，或者token过期
     */
    public static final String CSRF_ERROR = "101";
}
