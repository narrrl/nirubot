package nirusu.nirubot.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RandomHttpClient {

    public static URL getURL(final int max, final int min, final int num, int base) throws MalformedURLException {

        return new URL(String.format(
                "https://www.random.org/integers/?min=%d&max=%d&col=1&base=%d&format=plain&rnd=new&num=%d", min, max,
                base, num));
    }

    public static List<Integer> getRandomInt(final int min, final int max, final int amount) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(getURL(max, min, amount, 10).openStream()));
        String inputLine;

        ArrayList<Integer> nums = new ArrayList<>();

        while ((inputLine = in.readLine()) != null) {
            nums.add(Integer.parseInt(inputLine));
        }

        in.close();
        return nums;
    }

    public static List<Byte> getRandomBit(final int amount) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(getURL(1, 0, amount, 2).openStream()));
        String inputLine;

        ArrayList<Byte> nums = new ArrayList<>();

        while ((inputLine = in.readLine()) != null) {
            nums.add(Byte.parseByte(inputLine));
        }

        in.close();
        return nums;

    }
}
