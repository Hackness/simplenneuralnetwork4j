package network;

import javafx.util.Pair;

import java.util.List;

/**
 * Created by Hack
 * Date: 06.08.2017 5:42
 */
public class Functions {
    public static double normalizeSigmoid(double num) {
        return 1 / (1 + Math.exp(-num));
    }

    public static double normalizeHyperbola(double num) {
        return (Math.exp(2 * num) - 1) / (Math.exp(2 * num) + 1);
    }

    public static double errorMSE(List<Pair<Double, Double>> results) {
        return results.stream().mapToDouble(pair -> Math.pow(pair.getValue() - pair.getKey(), 2)).sum() / results.size();
    }

    public static double deltaResult(double result, double expectedResult) {
        return (expectedResult - result) * ((1 - result) * result);
    }
}
