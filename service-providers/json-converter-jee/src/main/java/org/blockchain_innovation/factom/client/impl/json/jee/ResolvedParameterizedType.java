package org.blockchain_innovation.factom.client.impl.json.jee;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;

/**
 * {@link ParameterizedType} implementation containing array of resolved TypeVariable type args.
 *
 * @author Roman Grigoriadi
 */
@SuppressWarnings("PMD.LawOfDemeter")
public class ResolvedParameterizedType implements ParameterizedType {

    /**
     * Original parameterized type.
     */
    private final Type original;

    /**
     * Resolved args by runtime type.
     */
    private final Type[] resolvedTypeArgs;

    /**
     * Creates a new instance.
     *
     * @param original         Original type.
     * @param resolvedTypeArgs Resolved type arguments.
     */
    public ResolvedParameterizedType(Type original, Type... resolvedTypeArgs) {
        this.original = original;
        this.resolvedTypeArgs = resolvedTypeArgs.clone();
    }

    /**
     * Type arguments with resolved TypeVariables.
     *
     * @return type args
     */
    @Override
    public Type[] getActualTypeArguments() {
        return resolvedTypeArgs.clone();
    }

    @Override
    public Type getRawType() {
        return original;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(original.toString());
        if (resolvedTypeArgs != null && resolvedTypeArgs.length > 0) {
            sb.append(" resolved arguments: [");
            for (Type typeArg : resolvedTypeArgs) {
                sb.append(String.valueOf(typeArg));
            }
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParameterizedType)) {
            return false;
        }
        final ParameterizedType that = (ParameterizedType) o;
        return this.getRawType().equals(that.getRawType())
                && Objects.equals(this.getOwnerType(), that.getOwnerType())
                && Arrays.equals(resolvedTypeArgs, that.getActualTypeArguments());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(resolvedTypeArgs) ^
                (getOwnerType() == null ? 0 : getOwnerType().hashCode()) ^
                (getRawType() == null ? 0 : getRawType().hashCode());
    }
}
