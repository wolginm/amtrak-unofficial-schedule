package com.markwolgin.amtrak.schedulegenerator.model.sets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A pair of two data elements.
 * @param <S>
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pair<S, T>{

    private S first;
    private T second;
}
