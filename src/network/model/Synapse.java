package network.model;

import network.model.neuron.AbstractNeuron;

/**
 * Created by Hack
 * Date: 15.08.2017 6:00
 */
public class Synapse {
    private double weight;
    private double lastDWeight;
    private AbstractNeuron startNeuron;
    private AbstractNeuron destinationNeuron;

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public AbstractNeuron getStartNeuron() {
        return startNeuron;
    }

    public void setStartNeuron(AbstractNeuron startNeuron) {
        this.startNeuron = startNeuron;
    }

    public AbstractNeuron getDestinationNeuron() {
        return destinationNeuron;
    }

    public void setDestinationNeuron(AbstractNeuron destinationNeuron) {
        this.destinationNeuron = destinationNeuron;
    }

    public double getLastDWeight() {
        return lastDWeight;
    }

    public void setLastDWeight(double lastDWeight) {
        this.lastDWeight = lastDWeight;
    }
}
