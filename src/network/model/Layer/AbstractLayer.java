package network.model.Layer;

import network.model.neuron.AbstractNeuron;
import network.model.neuron.BiasNeuron;
import network.model.neuron.ResultNeuron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Created by Hack
 * Date: 16.08.2017 3:11
 */
public abstract class AbstractLayer implements Serializable {
    protected List<AbstractNeuron> neurons = new ArrayList<>();

    public void add(AbstractNeuron neuron) {
        neurons.add(neuron);
    }

    public AbstractNeuron get(int i) {
        try {
            return neurons.get(i);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public void forEach(Consumer<AbstractNeuron> action) {
        neurons.forEach(action);
    }

    public Stream<AbstractNeuron> stream() {
        return neurons.stream();
    }

    public int size() {
        return neurons.size();
    }
}
