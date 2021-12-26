package com.IAppDevelopment.virtual_marathon.Object_classes;

public class data_run {

    /**
     * This class generates an object for the user's running data,
     * There are 3 String variables,
     * Username, rating / location, address for user image,
     * In addition, a long variable moth that is designed for the time of execution from the edge of the competition,
     * This variable contains the time in units of one millionth of a second.
     */
    public String data_name ,pic_url,place;
    public long time;

    public data_run(String data_name,String pic_url,String place,long time) {

        this.place = place;
        this.data_name = data_name;
        this.pic_url = pic_url;
        this.time = time;
    }

    public data_run(){}

    /**
     *This function returns the time
     * @return  returns the time
     */
    public long getTime() {
        return time;
    }

    /**
     * This function gets the time
     * @param time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * This function returns the url
     * @return returns the url
     */
    public String getPic_url() {
        return pic_url;
    }

    /**
     * This function accepts the url
     * @param pic_url
     */
    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }

    /**
     * This function returns the name
     * @return returns the name
     */
    public String getData_name() {
        return data_name;
    }

    /**
     * This function gets its name
     * @param data_name
     */
    public void setData_name(String data_name) {
        this.data_name = data_name;
    }

    /**
     *This function returns the rating
     * @return
     */
    public String getPlace() {
        return place;
    }

    /**
     * This function gets the rating
     * @param place
     */
    public void setPlace(String place) {
        this.place = place;
    }
}
