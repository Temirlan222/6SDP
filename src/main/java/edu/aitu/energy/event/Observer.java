package edu.aitu.energy.event;
@FunctionalInterface
public interface Observer<E> { void onEvent(E event); }
