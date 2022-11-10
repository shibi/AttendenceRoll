package com.example.attendenceroll;


import com.example.attendenceroll.utils.Utility;

public interface Config {

    /**
     * App version .  ( NB )
     *  WHEN CHANGING VERSION, NEED TO PROVIDE
     *  SAME VERSION IN ADMIN PANEL
     * */
    String APP_VERSION = "1.0";


    /**
     * url prefix
     * */
    String BASE_URL_PREFIX = "http://anncrm.dxgsofts.in/";


    /**
     * base url for api calls
     * */
    String BASE_URL = BASE_URL_PREFIX+"api/";


    /**
     * API key normal, edit api key here.
     * */
    String API_KEY = "@Appname_ApiKey0123#";


    /**
     * DO NOT EDIT.....
     * Encrypted API key, used in network request for security
     * Do not edit this ..  edit only API_KEY variable above.
     * */
    String ENCRYPTED_API_KEY = Utility.base64Encrypt(Config.API_KEY);


    /**
     * To enable or disable log
     * */
    boolean IS_DEVELOPMENT = true;

    /**
     * Enable log for tracing request links
     * */
    boolean LOG = Config.IS_DEVELOPMENT;

    /**
     * Enable log for tracing request links
     * */
    String DB_NAME = "AppLocalDB#AppName";


    /**
     * shared preference local storage name
     * */
    String SHARED_PREF_NAME ="AppName#012";


    /**
     * http client connection read , write  and time out in seconds
     * */
    int HTTP_CONNECTION_TIMEOUT_IN_SEC = 60;
    int HTTP_CONNECTION_READ_TIMEOUT_IN_SEC = 60;
    int HTTP_CONNECTION_WRITE_TIMEOUT_IN_SEC = 60;


    //splash timeout
    int SPLASH_TIME_OUT = 1000;


    String EMPLOYEE_SCAN_PREFIX = "ANG";



}


