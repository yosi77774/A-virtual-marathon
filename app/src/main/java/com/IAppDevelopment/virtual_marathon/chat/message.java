package com.IAppDevelopment.virtual_marathon.chat;

/**
 * This class produces an object for messages,
 * There are 4 String variables in this class,
 * Username, message content, date / time, and address for user image.
 */
public class message {

    private String data;
    private String my_name;
    private String message;
    public String pic_url;

    public message() {
    }

    public message(String data, String my_name, String message,String pic_url) {
        this.data = data;
        this.my_name = my_name;
        this.message = message;
        this.pic_url = pic_url;
    }

    /**
     * This function returns the date data
     * @return returns the date data
     */
    public String getData() {
        return data;
    }

    /**
     * This function returns the content of the message
     * @return Returns a message
     */
    public String getMessage() {
        return message;
    }

    /**
     * This function returns the name
     * @return returns the name
     */
    public String getMy_name() {
        return my_name;
    }

    /**
     * This function returns the image url in Firebase
     * @return Returns the image url
     */
    public String getPic_url() { return pic_url; }
}


