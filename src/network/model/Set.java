package network.model;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hack
 * Date: 16.08.2017 7:37
 */
public class Set {
    private final List<Double> inputValues;
    private final List<Double> resultValues;

    public Set(@NotNull Double[] input, @Nullable Double[] result) {
        inputValues = new ArrayList<>(Arrays.asList(input));
        resultValues = result != null ? new ArrayList<>(Arrays.asList(result)) : null;
    }

    public List<Double> getInputValues() {
        return inputValues;
    }

    public List<Double> getResultValues() {
        return resultValues;
    }
}
