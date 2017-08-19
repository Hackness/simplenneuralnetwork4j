package network.deprecated;

import javafx.util.Pair;
import network.model.Synapse;
import network.model.neuron.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Hack
 * Date: 09.08.2017 1:20
 *
 * Somewhere error :<
 */
@Deprecated
public class ArrayNetwork {
    private AbstractNeuron[][] neurons;
    private ResultNeuron resultNeuron;
    private List<TrainSet> trainSets = new ArrayList<>();
    private List<Pair<Double, Double>> resultAndExpectedPairs = new ArrayList<>();
    private double LEARN_SPEED = 0.7;
    private double MOMENT = 0.7;

    public ArrayNetwork(int internalWidth, int height) {
        neurons = new AbstractNeuron[1 + height][1 + internalWidth];
        initInternalCells();
        initExternalCells();
        initBiasCells();
        attachNeurons();
        attachResultNeuron();
        print();
    }

    private void initInternalCells() {
        forEachEdit(Scope.Internal, n -> new InternalNeuron());
    }

    private void initExternalCells() {
        forEachEdit(Scope.FirstLayer, n -> new InputNeuron());
    }

    // re-init
    private void initBiasCells() {
        forEachEdit(Scope.LastRow, n -> new BiasNeuron());
    }

    public void print() {
        System.out.println(Stream.of(neurons)
                .map(arr -> Stream.of(arr).map(n -> n + "").collect(Collectors.joining(" || ")) + "\n")
                .collect(Collectors.joining()));
    }

    private void attachNeurons() {
        forEach(Scope.Internal, (mainNeuron, pair) -> forEach(pair.getValue() - 1, n -> createSynapse(n, mainNeuron)));
    }

    private void attachResultNeuron() {
        resultNeuron = new ResultNeuron();
        forEach(Scope.LastLayer, n -> createSynapse(n, resultNeuron));
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

    /**
     * Produce an action for each element in scope borders
     * @param scope - The boundary within which each element will be taken
     * @param action - a consumer that will be applied
     */
    public void forEach(Scope scope, Consumer<AbstractNeuron> action) {
        for (int j = scope.minHeight(neurons); j <= scope.maxHeight(neurons); j++)
            for (int i = scope.minWidth(neurons); i <= scope.maxWidth(neurons); i++)
                action.accept(neurons[j][i]);
    }

    public void forEach(Scope scope, Consumer<AbstractNeuron> action, boolean reversedOrder) {
        if (reversedOrder)
            for (int j = scope.maxHeight(neurons); j >= scope.maxHeight(neurons); j--)
                for (int i = scope.maxWidth(neurons); i >= scope.maxWidth(neurons); i--)
                    action.accept(neurons[j][i]);
        else
            forEach(scope, action);
    }

    /**
     * Produce an action for each element in a layer
     * @param layer - The boundary within which each element will be taken
     * @param action - a consumer that will be applied
     */
    public void forEach(int layer, Consumer<AbstractNeuron> action) {
        for (int j = 0; j < neurons.length; j++)
            action.accept(neurons[j][layer]);
    }

    /**
     * Produce an action for each element in scope borders
     * @param scope - The boundary within which each element will be taken
     * @param action - a consumer that will take a neuron with pair(currentHeight, currentWidth) of his location in array
     */
    public void forEach(Scope scope, BiConsumer<AbstractNeuron, Pair<Integer, Integer>> action) {
        for (int j = scope.minHeight(neurons); j <= scope.maxHeight(neurons); j++)
            for (int i = scope.minWidth(neurons); i <= scope.maxWidth(neurons); i++)
                action.accept(neurons[j][i], new Pair<>(j, i));
    }

    /**
     * Produce an action for each element in scope borders, the result will be placed back.
     * @param scope - The boundary within which each element will be taken
     * @param action - a function, the result if which will be placed in network.
     */
    public void forEachEdit(Scope scope, UnaryOperator<AbstractNeuron> action) {
        for (int j = scope.minHeight(neurons); j <= scope.maxHeight(neurons); j++)
            for (int i = scope.minWidth(neurons); i <= scope.maxWidth(neurons); i++)
                neurons[j][i] = action.apply(neurons[j][i]);
    }



    public void addTrainSet(TrainSet set) {
        if (set.getValues().length != neurons.length)
            throw new ArrayIndexOutOfBoundsException("Number of required rows are not equal with " +
                    "number of train set rows");
        trainSets.add(set);
    }

    private void setTrainVariables(TrainSet set) {
        forEach(Scope.FirstLayer, (n, pair) -> {
            if (n instanceof InputNeuron)
                ((InputNeuron) n).setOutput(set.getValues()[pair.getKey()]);
        });
    }

    public void trainEpoch() {
        trainSets.forEach(this::trainSet);
//        System.out.println("Error:\t" + Functions.errorMSE(resultAndExpectedPairs));
//        resultAndExpectedPairs.clear();
    }

    private void trainSet(TrainSet set) {
        setTrainVariables(set);
        resultNeuron.calcOutput();
        resultAndExpectedPairs.add(new Pair<>(resultNeuron.getOutput(), set.getResult()));
        System.out.println("Result:\t" + resultNeuron.getOutput() + " Expected:\t" + set.getResult());
        resultNeuron.calcDelta(set.getResult());
        forEach(Scope.Each, AbstractNeuron::calcDelta, true);
//        System.out.println("res delta: " + resultNeuron.calcDelta(set.getResult()));
//        forEach(Scope.Each, n -> System.out.println("delta: " + n.calcDelta()), true);

        forEach(Scope.Each, n -> n.getOutputSynapses().forEach(s -> {
            double grad = s.getStartNeuron().getOutput() * s.getDestinationNeuron().getDelta();
            double dWeight = LEARN_SPEED * grad + MOMENT * s.getLastDWeight();
            s.setWeight(s.getWeight() + dWeight);
            s.setLastDWeight(dWeight);
        }));
    }
}
/*
    private static void arrayNetworkTest() {
        ArrayNetwork network = new ArrayNetwork(10, 1);

        network.addTrainSet(new TrainSet(new double[] { 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 1 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 1 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 1 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 1 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 1 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 1, 1 }, 0));


        /*
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 0, 1, 0, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 0, 0, 1, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 }, 0));
        */
        /*
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 1, 0, 0, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 1, 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 1, 0, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 1, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0, 0, 1 }, 1));
        */
//        network.addTrainSet(new TrainSet(new double[] { 1, 0, 0 }, 1));
//        network.addTrainSet(new TrainSet(new double[] { 1, 1, 0 }, 0));

        /*
        network.addTrainSet(new TrainSet(new double[] { 1, 1, 1 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 0, 0 }, 0));
        network.addTrainSet(new TrainSet(new double[] { 1, 1, 0 }, 1));
        network.addTrainSet(new TrainSet(new double[] { 0, 1, 0 }, 1));
        */
//        IntStream.iterate(0, i -> i + 2).parallel().forEach(i -> network.trainEpoch());
/*
        for(int i = 0; i < 100000; i++)
        network.trainEpoch();
}
 */