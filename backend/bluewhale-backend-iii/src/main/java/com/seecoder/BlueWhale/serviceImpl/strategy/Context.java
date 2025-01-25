package com.seecoder.BlueWhale.serviceImpl.strategy;

public class Context {
    public Context(CalculateStrategy strategy) {
        calculateStrategy = strategy;
    }


    public Double calculate(Double price) {
        return calculateStrategy.calculate(price);
    }


    private final CalculateStrategy calculateStrategy;
}
