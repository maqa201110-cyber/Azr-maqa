package com.azr.client.module;

public class Setting<T> {
    private final String name;
    private T value;
    private final T min;
    private final T max;

    public Setting(String name, T value) { this(name, value, null, null); }

    public Setting(String name, T value, T min, T max) {
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public T getMin() { return min; }
    public T getMax() { return max; }

    public void setValue(T value) {
        if (min instanceof Number && max instanceof Number && value instanceof Number) {
            double v = ((Number) value).doubleValue();
            double lo = ((Number) min).doubleValue();
            double hi = ((Number) max).doubleValue();
            if (v < lo) v = lo;
            if (v > hi) v = hi;
            @SuppressWarnings("unchecked")
            T clamped = (T) (Object) Double.valueOf(v);
            this.value = clamped;
        } else {
            this.value = value;
        }
    }

    public boolean asBool() { return (Boolean) value; }
    public double asDouble() { return ((Number) value).doubleValue(); }
    public int asInt() { return ((Number) value).intValue(); }
    public float asFloat() { return ((Number) value).floatValue(); }
    public String asString() { return (String) value; }
}
