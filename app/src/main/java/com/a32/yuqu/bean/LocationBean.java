package com.a32.yuqu.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by 32 on 2017/5/7.
 */
public class LocationBean {

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable{
        /**
         * placeName : 渔场1
         * longitude : 106.461248
         * latitude : 29.567252
         * describes : 鲫鱼
         * remark : 自带渔场
         * filePath : 20170506222504.jpg
         */

        private String placeName;
        private String longitude;
        private String latitude;
        private String describes;
        private String remark;
        private String filePath;

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getDescribes() {
            return describes;
        }

        public void setDescribes(String describes) {
            this.describes = describes;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }
}
