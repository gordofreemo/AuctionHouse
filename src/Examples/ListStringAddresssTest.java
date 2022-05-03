package Examples;

import java.util.ArrayList;
import java.util.List;

public class ListStringAddresssTest {
    public static void main(String[] args) {
        List<String> addresses = new ArrayList<>();
        String tmpAddresses = "";
        addresses.add("123.123.123");
        addresses.add("124.124.124");
        for (var address : addresses) {
            tmpAddresses += address + "\n";
        }
        System.out.println(12 + "\n" + tmpAddresses.trim());
    }
}
