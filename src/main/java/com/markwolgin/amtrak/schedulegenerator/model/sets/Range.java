package com.markwolgin.amtrak.schedulegenerator.model.sets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

/**
 * A pair of two comparable elements.
 * @param <T>   Comparable.
 */
@Data
@AllArgsConstructor
public class Range<T extends Comparable> extends Pair<T, T> {

    public Range(T first, T second) {
        super(first, second);
    }

    /**
     * Calculates if the element supplied is withing the domain.<br>
     * 0 if so, 1 if larger than the range, -1 if smaller.
     * @param element   A
     * @return
     */
    public int inDomain(final T element) {
        int lower = getFirst().compareTo(element);
        int upper = getSecond().compareTo(element);

        int ret = 0;
        if (lower >= 0 && upper <= 0) ret = 0;
        else if (lower >= 0 && upper > 0) ret = 1;
        else if (lower < 0 && upper <= 0) ret = -1;

        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class StopTimes {\n");
        sb.append("    first: ").append(toIndentedString(getFirst())).append("\n");
        sb.append("    second: ").append(toIndentedString(getSecond())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}
