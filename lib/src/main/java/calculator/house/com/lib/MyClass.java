package calculator.house.com.lib;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyClass {

    public static void main(String[] args) {
        String PATTERN= "\\$GPGSV,\\d*,\\d*,\\d*,\\d*,\\d*,\\d*,(\\d*)\\*.*";
        String PATTERN1= "\\$GPGSV,\\d*,\\d*,\\d*,\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*)\\*.*";
        String PATTERN2= "\\$GPGSV,\\d*,\\d*,\\d*,\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*)\\*.*";
        String PATTERN3= "\\$GPGSV,\\d*,\\d*,\\d*,\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*),\\d*,\\d*,\\d*,(\\d*)\\*.*";
        String content="$GPGSV,1,1,02,12,21,059,15*7A";
        String content1="$GPGSV,1,1,02,12,21,059,15,20,72,086,22*7A";
        String content2="$GPGSV,1,1,02,12,21,059,15,20,72,086,22,20,72,086,25*7A";
        String content3="$GPGSV,1,1,02,12,21,059,15,20,72,086,22,20,72,086,25,20,72,086,22*7A";
//        Matcher matcher= Pattern.compile(PATTERN3).matcher(content3);
//        if(matcher.matches()){
//            for(int i=1;i<=matcher.groupCount();i++) {
//                System.out.println(matcher.group(i)); // Display the string.
//            }
//        }
//        else  System.out.println("not match"); // Display the string.
        String[] arr=content.split(",");
        if(arr.length==8)
            System.out.println(arr[7]); // Display the string.
        else if(arr.length==12)
            System.out.println(arr[7]+"-"+arr[11]); // Display the string.
        else if(arr.length==16)
            System.out.println(arr[7]+"-"+arr[11]+"-"+arr[15]); // Display the string.
        else if(arr.length==20)
            System.out.println(arr[7]+"-"+arr[11]+"-"+arr[15]+"-"+arr[19]); // Display the string.
    }
}
