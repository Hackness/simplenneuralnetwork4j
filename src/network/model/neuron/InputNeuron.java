package network.model.neuron;

/**
 * Created by Hack
 * Date: 06.08.2017 6:33
 */
public class InputNeuron extends AbstractNeuron {
    public InputNeuron() {
    }

    @Override
    public double calcOutput() {
        return output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

//    @Override
//    public String toString() {
//        return "[External_" + myNum + "(output:" + output + ")]";
//    }

    @Override
    public double calcDelta() {
        return delta = 0;
    }
}
