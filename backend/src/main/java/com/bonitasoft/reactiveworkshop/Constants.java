package com.bonitasoft.reactiveworkshop;

public class Constants {

	public static int TIMEOUT_STREAM = Integer.parseInt(System.getProperty("stream-timeout","60000"));
	public static String COMMENT_URI = System.getProperty("external-service","http://localhost:3004");
	public static String COMMENTS_ARTIST_LAST10 = "/comments/{id}/last10";
	public static String COMMENTS_STREAM = "/comments/stream";	

}
