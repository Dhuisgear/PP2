package com.example.student.pp2;

public class Constants {

    // serwer
//    private static final String ROOT_URL = "http://pp2.5v.pl/pp2/v1/";

    // localhost
//    private static final String ROOT_URL = "http://192.168.1.186:8080/pp2/v1/";     //home
    private static final String ROOT_URL = "http://87.246.222.33:8080/pp2/v1/";    //C421
//    private static final String ROOT_URL = "http://87.246.223.126:8080/pp2/v1/";    //czytelnia

    public static final String URL_REGISTER = ROOT_URL + "registerUser.php";
    public static final String URL_LOGIN = ROOT_URL + "userLogin.php";

    public static final String URL_CREATE_REPAIR = ROOT_URL + "createRepair.php";
    public static final String URL_GET_REPAIR_DETAILS = ROOT_URL + "getRepairDetails.php";
    public static final String URL_GET_USER_REPAIRS = ROOT_URL + "getUserRepairs.php";
    public static final String URL_GET_ALL_REPAIRS = ROOT_URL + "getAllRepairs.php";
    public static final String URL_GET_ALL_USERS = ROOT_URL + "getAllUsers.php";
    public static final String URL_UPDATE_REPAIR = ROOT_URL + "updateRepair.php";
    public static final String URL_DELETE_REPAIR = ROOT_URL + "deleteRepair.php";
}
