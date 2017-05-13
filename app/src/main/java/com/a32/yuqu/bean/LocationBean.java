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
         * phone : 15223084076
         * placeName : 沙坪坝桥下钓鱼场
         * longitude : 106.45981
         * latitude : 29.559806
         * describes : 环境比较幽静，鱼的种类较多
         * remark : 自带鱼竿
         * filePath : 20170507085915.jpg
         * max : 20
         * booked : 0
         */

        private String phone;
        private String placeName;
        private String longitude;
        private String latitude;
        private String describes;
        private String remark;
        private String filePath;
        private String max;
        private String booked;

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

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

        public String getMax() {
            return max;
        }

        public void setMax(String max) {
            this.max = max;
        }

        public String getBooked() {
            return booked;
        }

        public void setBooked(String booked) {
            this.booked = booked;
        }
    }
}
