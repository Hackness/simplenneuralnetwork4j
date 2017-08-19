package network.model;

import javafx.util.Pair;
import network.Functions;
import network.model.Layer.AbstractLayer;
import network.model.Layer.InputLayer;
import network.model.Layer.InternalLayer;
import network.model.Layer.OutputLayer;
import network.model.neuron.AbstractNeuron;
import network.model.neuron.BiasNeuron;
import network.model.neuron.InputNeuron;
import network.model.neuron.ResultNeuron;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Hack
 * Date: 16.08.2017 3:12
 */
public class LayerNetwork implements Serializable {
    private List<AbstractLayer> layers;
    private int internalNeuronCount;
    private boolean bias;
    private List<Set> trainSets = new ArrayList<>();
    private List<Pair<Double, Double>> resultAndExpectedPairs = new ArrayList<>();
    private volatile boolean initialized = false;
    private final double LEARN_SPEED = 0.9;
    private final double MOMENT = 0.3;
    private final double DEBUG_INFO_LEVEL = 3;

    public LayerNetwork(int internalLayers, int internalNeuronCount, boolean bias) {
        this.internalNeuronCount = internalNeuronCount + (bias ? 1 : 0);
        this.bias = bias;
        layers = new ArrayList<>(internalLayers + 2);
        layers.add(new InputLayer());
        IntStream.range(0, internalLayers).forEach(i -> layers.add(new InternalLayer(internalNeuronCount)));
        layers.add(new OutputLayer());
    }

    private void addBiasNeurons() {
        layers.stream()
                .filter(l -> l instanceof InputLayer || l instanceof InternalLayer)
                .forEach(l -> l.add(new BiasNeuron()));
    }

    public void forEach(Consumer<AbstractNeuron> action, boolean inputInclusive, boolean outputInclusive) {
        IntStream.range(inputInclusive ? 0 : 1, outputInclusive ? layers.size() : layers.size() - 1)
                .forEach(i -> layers.get(i).forEach(action));
    }

    public void forEach(Consumer<AbstractNeuron> action) {
        forEach(action, true, true);
    }

    public void forEachReversed(Consumer<AbstractNeuron> action) {
        for (int i = layers.size() - 1; i >= 0; i--)
            layers.get(i).forEach(action);
    }

    private void attachNeurons() {
        IntStream.range(0, layers.size() - 1).forEach(i -> layers.get(i)
                .forEach(firstNeuron -> layers.get(i + 1)
                        .forEach(secondNeuron -> createSynapse(firstNeuron, secondNeuron))));
    }

    private void printMe() {
        IntStream.range(0, internalNeuronCount).forEach(i -> {
            System.out.println(layers.stream()
                    .map(l -> Optional.ofNullable(l.get(i))
                            .map(AbstractNeuron::toString).orElse(AbstractNeuron.emptyNeuronName()))
                    .collect(Collectors.joining(" || ")));
        });
    }

    private Synapse createSynapse(AbstractNeuron startNeuron, AbstractNeuron destNeuron) {
        if (destNeuron instanceof BiasNeuron)
            return null;
        Synapse synapse = new Synapse();
        synapse.setWeight(ThreadLocalRandom.current().nextDouble(-1, 1));
        synapse.setStartNeuron(startNeuron);
        synapse.setDestinationNeuron(destNeuron);
        startNeuron.attachSynapse(synapse);
        destNeuron.attachSynapse(synapse);
        return synapse;
    }

    public void addTrainSet(Set set) {
        trainSets.add(set);
    }

    //TODO: multi-output support
    public void trainSet(Set set) {
        setTrainVariables(set);
        if (!initialized) {
            if (bias)
                addBiasNeurons();
            attachNeurons();
            initialized = true;
//            printMe();
            print("Network initialized. Start training...", 1);
        }
        forEach(AbstractNeuron::calcOutput);

        ResultNeuron rNeuron = (ResultNeuron) layers.get(layers.size() - 1).get(0); //TODO
        print("================================================================================", 4);
        print("Result: " + rNeuron.getOutput() + " Expected: " + rNeuron.getExpectedResult(), 2);
        resultAndExpectedPairs.add(new Pair<>(rNeuron.getOutput(), rNeuron.getExpectedResult()));

        forEachReversed(AbstractNeuron::calcDelta);
        forEach(n -> n.getOutputSynapses().forEach(s -> {
            print("--------------------------------------------------------------------------------", 4);
            double grad = s.getStartNeuron().getOutput() * s.getDestinationNeuron().getDelta();
            print("grad: " + grad + "\toutput: " + s.getStartNeuron().getOutput() + "\tdelta: " + s.getDestinationNeuron().getDelta(), 4);
            double dWeight = LEARN_SPEED * grad + MOMENT * s.getLastDWeight();
            print("dWeight: " + dWeight + "\tlastDWeight: " + s.getLastDWeight(), 4);
            s.setWeight(s.getWeight() + dWeight);
            s.setLastDWeight(dWeight);
        }));
    }

    public double calc(Set set) {
        setTrainVariables(set);
        forEach(AbstractNeuron::calcOutput);
        return layers.get(layers.size() - 1).get(0).getOutput(); //TODO
    }

    public double trainEpoch() {
        try {
            print("====================================[EPOCH]=====================================", 3);
            trainSets.forEach(this::trainSet);
            double error = Functions.errorMSE(resultAndExpectedPairs);
            print("Error:\t" + error, 3);
            return error;
        } finally {
            resultAndExpectedPairs.clear();
        }
    }

    public void setTrainVariables(Set set) {
        setTrainValues(layers.get(0), set.getInputValues(), InputNeuron::new, (n, d) -> {
            if (n instanceof InputNeuron)
                ((InputNeuron) n).setOutput(d);
        });
        if (set.getResultValues() != null)
            setTrainValues(layers.get(layers.size() - 1), set.getResultValues(), ResultNeuron::new, (n, d) -> {
                if (n instanceof ResultNeuron)
                    ((ResultNeuron) n).setExpectedResult(d);
            });
    }

    private synchronized void setTrainValues(AbstractLayer layer, List<Double> values, Supplier<AbstractNeuron> onInitAction,
                                BiConsumer<AbstractNeuron, Double> action) {
        if (layer.size() == 0)
            IntStream.range(0, values.size()).forEach(i -> layer.add(onInitAction.get()));
        if (layer.size() != values.size())
            throw new RuntimeException("Incorrect count of input values.");
        IntStream.range(0, layer.size()).forEach(i -> action.accept(layer.get(i), values.get(i)));
    }

    private void print(String msg, int infoLevel) {
        if (infoLevel <= DEBUG_INFO_LEVEL)
            System.out.println(msg);
    }

    public void train(BiFunction<Integer, Double, Boolean> remainCondition) {
        for (int i = 1 ;; i++)
            if (!remainCondition.apply(i, trainEpoch())) {
                print("\nLearning successfully finished after " + i + " epoch iterations.", 1);
                return;
            }
    }
}
