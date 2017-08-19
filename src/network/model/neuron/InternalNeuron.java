package network.model.neuron;

import network.Functions;
import network.model.neuron.AbstractNeuron;

import java.util.stream.Collectors;

/**
 * Created by Hack
 * Date: 06.08.2017 4:42
 */
public class InternalNeuron extends AbstractNeuron {
    @Override
    public double calcOutput() {
        return output = Functions.normalizeSigmoid(getInputSynapses().stream()
                .mapToDouble(s -> s.getStartNeuron().getOutput() * s.getWeight()).sum());
    }

    @Override
    public double calcDelta() {
        return delta = (1 - output) * output * (getOutputSynapses().stream()
                .mapToDouble(s -> s.getDestinationNeuron().getDelta() * s.getWeight()).sum());
    }
}
