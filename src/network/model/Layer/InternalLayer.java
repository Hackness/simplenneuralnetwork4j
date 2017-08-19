package network.model.Layer;

import network.model.neuron.BiasNeuron;
import network.model.neuron.InternalNeuron;

import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 * Created by Hack
 * Date: 16.08.2017 3:16
 */
public class InternalLayer extends AbstractLayer {

    public InternalLayer(int size) {
        IntStream.range(0, size).forEach(i -> neurons.add(new InternalNeuron()));
    }
}
