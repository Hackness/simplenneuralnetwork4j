package network.model.neuron;

import network.model.Synapse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Hack
 * Date: 15.08.2017 0:29
 */
public abstract class AbstractNeuron implements Serializable {
    protected double output;
    protected double delta;
    protected List<Synapse> synapses = new ArrayList<>(0); //TODO: перепилить все на синапсы

    //optional values for toString()
    private static int num = 0;
    protected int myNum = num++;
    private static int maxNameLength = 45;

    abstract public double calcOutput();
    abstract public double calcDelta();

    public void attachSynapse(Synapse synapse) {
        synapses.add(synapse);
    }

    public List<Synapse> getOutputSynapses() {
        return synapses.stream().filter(s -> s.getStartNeuron() == this).collect(Collectors.toList());
    }

    public List<Synapse> getInputSynapses() {
        return synapses.stream().filter(s -> s.getDestinationNeuron() == this).collect(Collectors.toList());
    }

    public double getOutput() {
        return output;
    }

    public double getDelta() {
        return delta;
    }

    protected String name() {
        return getClass().getSimpleName().replace("Neuron", "");
    }

    @Override
    public String toString() {
        String name = "[" + name() + "_" + myNum
                + "(input:" + getInputSynapses().stream().map(s -> s.getStartNeuron().myNum + "")
                .collect(Collectors.joining(",")) + ","
                + "output:" + getOutputSynapses().stream().map(s -> s.getDestinationNeuron().myNum + "")
                .collect(Collectors.joining(",")) + ")]";
        if (maxNameLength > name.length()) {
            name += IntStream.range(0, maxNameLength - name.length()).mapToObj(i -> " ").collect(Collectors.joining());
        } else
            maxNameLength = name.length();
        return name;
    }

    public static String emptyNeuronName() {
        return IntStream.range(0, maxNameLength).mapToObj(i -> " ").collect(Collectors.joining());
    }
}
