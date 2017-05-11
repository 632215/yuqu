package com.a32.yuqu.bean;

import java.util.List;

/**
 * Created by 32 on 2017/5/9.
 */
public class DDyBean {
    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        /**
         * head : 20170506200514.jpg
         * tvName : 魏爽
         * tvContent : 五图片
         * img :
         */

        private String head;
        private String tvName;
        private String tvContent;
        private String img;

        public String getHead() {
            return head;
        }

        public void setHead(String head) {
            this.head = head;
        }

        public String getTvName() {
            return tvName;
        }

        public void setTvName(String tvName) {
            this.tvName = tvName;
        }

        public String getTvContent() {
            return tvContent;
        }

        public void setTvContent(String tvContent) {
            this.tvContent = tvContent;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        @Override
        public String toString() {
            return "ListBean{" +
                    "head='" + head + '\'' +
                    ", tvName='" + tvName + '\'' +
                    ", tvContent='" + tvContent + '\'' +
                    ", img='" + img + '\'' +
                    '}';
        }
    }
}
