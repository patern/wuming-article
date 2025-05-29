package com.wuming.common.oss.exception;


/**
 * OSS异常类
 *
 * @author Lion Li
 */
public class OssException extends RuntimeException {

    private static final long serialVersionUID = 1333222232323231L;

    public OssException(String msg) {
        super(msg);
    }

}
