package network.deprecated;

/**
 * Created by Hack
 * Date: 09.08.2017 7:11
 */
@Deprecated
public class TrainSet {
    private final double[] values;
    private final double result;

    public TrainSet(double[] values, double result) {
        this.values = values;
        this.result = result;
    }

    public double[] getValues() {
        return values;
    }

    public double getResult() {
        return result;
    }
}
