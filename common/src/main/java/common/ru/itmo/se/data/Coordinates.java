package common.ru.itmo.se.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * This class represents a composite data type which describes a coordinate pair value of X and Y.
 * -- CONSTRUCTOR --
 * Constructs a Coordinates with the specified X and Y.
 */
@AllArgsConstructor
public class Coordinates implements Serializable {

    private static final float EPSILON = 0.000000001F;
    /**
     * This field holds the value of coordinate X.
     */
    private float x;
    /**
     * This field holds the value of coordinate Y.
     * -- GETTER --
     * Getter method for coordinate Y. Since this field doesn't have initial constraints, it doesn't justify having a custom implementation.
     */
    @Getter
    private float y;

    /**
     * A custom version of a getter method for coordinate X. It's made custom in accordance to initial constraints.
     *
     * @return float value of X, MIN_X in case of a NullPointerException.
     */
    public float getX() {
        return x;
    }

    /**
     * A custom implementation of the hashCode() method in Coordinates.
     *
     * @return hash code of a Coordinates instance.
     */
    @Override
    public int hashCode() {
        return Math.round(x * y);
    }

    /**
     * A custom implementation of the toString() method in Coordinates.
     * @return values of X and Y parsed to String data type.
     */
    @Override
    public String toString() {
        return "X = " + x + "; " + "Y = " + y;
    }

    /**
     * A custom implementation of the equals(Object obj) method in Coordinates. It works by checking whether the differences of every field is less than EPSILON.
     * @param obj the object to be compared.
     * @return boolean value of whether the two coordinates were equal as defined in this method.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Coordinates coordinates) {
            return Math.abs(x - coordinates.getX()) < EPSILON && Math.abs(y - coordinates.getY()) < EPSILON;
        }
        return false;
    }
}
