package com.markwolgin.amtrak.schedulegenerator.model.sets;

import lombok.Data;

/**
 * A single value.
 * @param <T>   Something.
 */
@Data
public class Unit<T> {

    /**
     * The value of the unit.
     */
    private T value;
}
