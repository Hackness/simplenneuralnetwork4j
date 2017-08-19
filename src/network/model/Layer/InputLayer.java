package network.model.Layer;

import network.model.neuron.BiasNeuron;

/**
 * Created by Hack
 * Date: 16.08.2017 3:15
 */
public class InputLayer extends AbstractLayer {
    @Override
    public int size() {
        return super.size() - (int) neurons.stream().filter(n -> n instanceof BiasNeuron).count();
    }
}
