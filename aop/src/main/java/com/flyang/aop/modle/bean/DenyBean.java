package com.flyang.aop.modle.bean;

import java.util.List;

/**
 * @author caoyangfei
 * @ClassName DenyBean
 * @date 2019/7/6
 * ------------- Description -------------
 * 权限返回信息
 */
public class DenyBean {

    private int requestCode;
    private List<String> denyList;

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public List<String> getDenyList() {
        return denyList;
    }

    public void setDenyList(List<String> denyList) {
        this.denyList = denyList;
    }
}
