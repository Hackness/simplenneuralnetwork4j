package network.deprecated;

import network.model.neuron.AbstractNeuron;

import java.util.function.Function;

/**
 * Created by Hack
 * Date: 09.08.2017 7:09
 */
@Deprecated
public enum Scope {
    FirstLayer(
            arr -> 0,
            arr -> arr.length - 1,
            arr -> 0,
            arr -> 0
    ),
    LastLayer(
            arr -> 0,
            arr -> arr.length - 1,
            arr -> arr[0].length - 1,
            arr -> arr[0].length - 1

    ),
    FirstRow(
            arr -> 0,
            arr -> 0,
            arr -> 0,
            arr -> arr[0].length - 1

    ),
    LastRow(
            arr -> arr.length - 1,
            arr -> arr.length - 1,
            arr -> 0,
            arr -> arr[0].length - 1

    ),
    Each(
            arr -> 0,
            arr -> arr.length - 1,
            arr -> 0,
            arr -> arr[0].length - 1
    ),
    Internal(
            arr -> 0,
            arr -> arr.length - 1,
            arr -> 1,
            arr -> arr[0].length - 1
    );

    private final Function<AbstractNeuron[][], Integer> minHeight;
    private final Function<AbstractNeuron[][], Integer> maxHeight;
    private final Function<AbstractNeuron[][], Integer> minWidth;
    private final Function<AbstractNeuron[][], Integer> maxWidth;

    Scope(Function<AbstractNeuron[][], Integer> minHeight, Function<AbstractNeuron[][], Integer> maxHeight,
          Function<AbstractNeuron[][], Integer> minWidth, Function<AbstractNeuron[][], Integer> maxWidth) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
    }

    public int minHeight(AbstractNeuron[][] array) {
        return minHeight.apply(array);
    }

    public int maxHeight(AbstractNeuron[][] array) {
        return maxHeight.apply(array);
    }

    public int minWidth(AbstractNeuron[][] array) {
        return minWidth.apply(array);
    }

    public int maxWidth(AbstractNeuron[][] array) {
        return maxWidth.apply(array);
    }

    public int minWidth(AbstractNeuron[][] array, int layer) {
        return minWidth.apply(array);
    }

    public int maxWidth(AbstractNeuron[][] array, int layer) {
        return maxWidth.apply(array);
    }
}
