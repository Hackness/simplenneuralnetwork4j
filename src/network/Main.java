package network;

import network.model.LayerNetwork;
import network.model.Set;

import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.IntStream;

/**
 * Created by Hack
 * Date: 06.08.2017 4:40
 *
 * TODO: parallel calculating
 */
public class Main {
    public static void main(String[] args) {
//        test();
//        newNetworkTest();
        layerNetworkTest();
    }

    private static void layerNetworkTest() {
        LayerNetwork network = new LayerNetwork(2, 2, true);
//        xorTrain(network);
        nrkmnTrain2(network);
        network.train((i, err) -> err > 0.0001);
        consoleInputTest(network);
    }

    private static void nrkmnTrain2(LayerNetwork network) {
        network.addTrainSet(new Set(new Double[] { 0., 0., 0., 0. }, new Double[] { 0. }));
        network.addTrainSet(new Set(new Double[] { 1., 0., 0., 0. }, new Double[] { 1. }));
        network.addTrainSet(new Set(new Double[] { 0., 1., 0., 0. }, new Double[] { 0. }));
        network.addTrainSet(new Set(new Double[] { 0., 0., 1., 0. }, new Double[] { 1. }));
        network.addTrainSet(new Set(new Double[] { 0., 0., 0., 1. }, new Double[] { 0. }));
        network.addTrainSet(new Set(new Double[] { 1., 1., 0., 0. }, new Double[] { 0.5 }));
        network.addTrainSet(new Set(new Double[] { 0., 1., 1., 0. }, new Double[] { 0.5 }));
        network.addTrainSet(new Set(new Double[] { 0., 0., 1., 1. }, new Double[] { 0.5 }));
        network.addTrainSet(new Set(new Double[] { 1., 1., 1., 0. }, new Double[] { 0.75 }));
        network.addTrainSet(new Set(new Double[] { 0., 1., 1., 1. }, new Double[] { 0.25 }));

    }

    private static void xorTrain(LayerNetwork network) {
        network.addTrainSet(new Set(new Double[] { 0., 0. }, new Double[] { 0. }));
        network.addTrainSet(new Set(new Double[] { 1., 0. }, new Double[] { 1. }));
        network.addTrainSet(new Set(new Double[] { 0., 1. }, new Double[] { 1. }));
        network.addTrainSet(new Set(new Double[] { 1., 1. }, new Double[] { 0. }));
    }

    private static void train(LayerNetwork network) {
        for (int i = 0; i < 100000; i++)
            network.trainEpoch();
    }

    private static void trainParallel(LayerNetwork network) {
        IntStream.range(0, 100000).parallel().forEach(i -> network.trainEpoch());
    }

    private static void consoleInputTest(LayerNetwork network) {
        System.out.println("\nPlease input compatible values:");
        while (true) {
            StringTokenizer st = new StringTokenizer(new Scanner(System.in).nextLine(), ":");
            try {
                Double[] input = parseToDoubleArray(st.nextToken());
//                Double[] result = parseToDoubleArray(st.nextToken());
                System.out.println("Result: " + network.calc(new Set(input, null)));
            } catch (NumberFormatException e) {
                System.out.println("Incorrect data: " + e);
            }
        }
    }

    private static Double[] parseToDoubleArray(String str) {
        String[] strArr = str.split(",");
        Double[] arr = new Double[strArr.length];
        for (int i = 0; i < arr.length; i++)
            arr[i] = Double.parseDouble(strArr[i].trim());
        return arr;
    }
}
