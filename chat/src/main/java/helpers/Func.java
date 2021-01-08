package helpers;

public class Func {
    public static boolean isNull(String str){
        if(str == null) return true ;
        if(str.trim().equals("")) return true ;
        return str.length() == 0;
    }
}
