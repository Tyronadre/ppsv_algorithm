package de.henrik;

import scala.Int;

import java.util.Comparator;
import java.util.Queue;

public class test {

    public static Comparator<String> cardComparator = (o1, o2) -> {
        try {
            var i1 = Integer.parseInt(o1);
            var i2 = Integer.parseInt(o2);
            return Integer.compare(i1, i2);
        } catch (NumberFormatException e) {
            try {
                Integer.parseInt(o1);
                return 1;
            } catch (NumberFormatException ignored){}
            try {
                Integer.parseInt(o2);
                return -1;
            } catch (NumberFormatException ignored){}
        }

        switch (o1) {
            case "J" -> {
                if (o2.equals("J")) {
                    return 0;
                } else {
                    return -1;
                }
            }
            case "Q" -> {
                if (o2.equals("J")) {
                    return 1;
                } else if (o2.equals("Q")) {
                    return 0;
                } else {
                    return -1;
                }
            }
            case "K" -> {
                if (o2.equals("J") || o2.equals("Q")) {
                    return 1;
                } else if (o2.equals("K")) {
                    return 0;
                } else {
                    return -1;
                }
            }
            case "A" -> {
                if (o2.equals("J") || o2.equals("Q") || o2.equals("K")) {
                    return 1;
                } else if (o2.equals("A")) {
                    return 0;
                } else {
                    return -1;
                }
            }
            default -> throw new IllegalArgumentException("Something went wrong");
        }
    };


}
