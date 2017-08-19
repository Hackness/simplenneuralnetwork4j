package network.model.neuron;

import network.model.neuron.InternalNeuron;

/**
 * Created by Hack
 * Date: 12.08.2017 19:19
 */
public class ResultNeuron extends InternalNeuron {
    private double expectedResult;

    public void setExpectedResult(double expectedResult) {
        this.expectedResult = expectedResult;
    }

    public double getExpectedResult() {
        return expectedResult;
    }

    @Override
    public double calcDelta() {
        return delta = (expectedResult - output) * (1 - output) * output;
    }

    @Deprecated
    public double calcDelta(double expectedResult) {
        return delta = (expectedResult - output) * (1 - output) * output;
    }
}
