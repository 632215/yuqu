package com.a32.yuqu.bean;

import java.util.List;

/**
 * Created by 32 on 2017/5/8.
 */
public class DXWbean {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * title : 男子钓上长毛“怪鱼”，瞬间傻眼
         * herf : http://www.diaoyur.com/a/2017/27863.html
         */

        private String title;
        private String herf;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getHerf() {
            return herf;
        }

        public void setHerf(String herf) {
            this.herf = herf;
        }
    }
}
