package network.model.neuron;

/**
 * Created by Hack
 * Date: 16.08.2017 0:20
 */

public class BiasNeuron extends AbstractNeuron {
    @Override
    public double calcOutput() {
        return output = 1;
    }

    @Override
    public double calcDelta() {
        return delta = 0;
    }
}
