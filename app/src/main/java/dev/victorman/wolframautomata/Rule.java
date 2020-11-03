package dev.victorman.wolframautomata;

public abstract class Rule {

    private byte output;

    // for rule 30 this would be 01111000
    protected Rule(byte orderedOutput) {
        output = orderedOutput;
    }

    public byte getOutcome(byte parents) {
        if (parents > 7 || parents < 0)
            throw new IllegalArgumentException("Arguments must be between 0 and 7 inclusive");

        int res = (output >> (7-parents)) & 0x1;
        return (byte) res;
    }
}
