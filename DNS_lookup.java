import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

public class DNSLookup {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Choose an option:");
            System.out.println("1. IP to URL");
            System.out.println("2. URL to IP");
            System.out.println("3. Exit");

            String choice = scanner.nextLine();

            if (choice.equals("1")) {
                System.out.print("Enter an IP address: ");
                String ip = scanner.nextLine();
                String url = ipToUrl(ip);
                System.out.println("URL: " + url);
            } else if (choice.equals("2")) {
                System.out.print("Enter a URL: ");
                String url = scanner.nextLine();
                String ip = urlToIp(url);
                System.out.println("IP address: " + ip);
            } else if (choice.equals("3")) {
                break;
            } else {
                System.out.println("Invalid choice. Please select 1, 2, or 3.");
            }
        }
    }

    public static String ipToUrl(String ip) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            return address.getHostName();
        } catch (UnknownHostException e) {
            return "Unable to resolve IP address to a URL";
        }
    }

    public static String urlToIp(String url) {
        try {
            InetAddress address = InetAddress.getByName(url);
            return address.getHostAddress();
        } catch (UnknownHostException e) {
            return "Unable to resolve URL to an IP address";
        }
    }
}
