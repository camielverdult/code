/*
 * GROOVE: GRaphs for Object Oriented VErification Copyright 2003--2007
 * University of Twente
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * $Id$
 */
package groove.algebra;

import groove.algebra.syntax.CallExpr;
import groove.algebra.syntax.Expression;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Register for the currently used algebras.
 * @author Arend Rensink
 * @version $Revision $
 */
public enum AlgebraFamily {
    /** Default algebra family:
     * {@link Integer} for {@code int}, 
     * {@link Boolean} for {@code bool}, 
     * {@link String} for {@code string}, 
     * {@link Double} for {@code real}, 
     */
    DEFAULT("default", JavaIntAlgebra.instance, JavaBoolAlgebra.instance,
            JavaStringAlgebra.instance, JavaRealAlgebra.instance),
    /** Point algebra family: every sort has a single value. */
    POINT("point", PointIntAlgebra.instance, PointBoolAlgebra.instance,
            PointStringAlgebra.instance, PointRealAlgebra.instance),
    /** High-precision algebra family:
     * {@link BigInteger} for {@code int}, 
     * {@link Boolean} for {@code bool}, 
     * {@link String} for {@code string}, 
     * {@link BigDecimal} for {@code real}, 
     */
    BIG("big", BigIntAlgebra.instance, BigBoolAlgebra.instance, BigStringAlgebra.instance,
            BigRealAlgebra.instance),
    /** Term algebra: symbolic representations for all values. */
    TERM("term", TermIntAlgebra.instance, TermBoolAlgebra.instance, TermStringAlgebra.instance,
            TermRealAlgebra.instance);

    /**
     * Constructs a new register, loaded with a given set of algebras.
     * @throws IllegalArgumentException if there is an algebra for which there
     *         is no known signature, or more than one algebra for the same
     *         signature
     * @throws IllegalStateException if there are signatures without algebras
     */
    private AlgebraFamily(String name, Algebra<?>... algebras) throws IllegalArgumentException,
        IllegalStateException {
        this.name = name;
        for (Algebra<?> algebra : algebras) {
            setImplementation(algebra);
        }
        checkCompleteness();
    }

    /**
     * Adds an algebra to the register. The algebra must implement an already
     * known signature.
     * @param algebra the algebra to be added
     */
    private void setImplementation(Algebra<?> algebra) {
        SignatureKind sigKind = algebra.getSignature();
        Algebra<?> oldAlgebra = this.algebraMap.put(sigKind, algebra);
        if (oldAlgebra != null) {
            throw new IllegalArgumentException(String.format(
                "Signature '%s' already implemented by '%s'", sigKind, oldAlgebra.getName()));
        }
    }

    /**
     * Checks for the completeness of the register.
     * @throws IllegalStateException if there is an implementation missing for
     *         some signature.
     */
    private void checkCompleteness() throws IllegalStateException {
        for (SignatureKind sigKind : SignatureKind.values()) {
            if (!this.algebraMap.containsKey(sigKind)) {
                throw new IllegalStateException(String.format(
                    "Implementation of signature '%s' is missing", sigKind));
            }
        }
    }

    /** Returns the name of this algebra family. */
    public final String getName() {
        return this.name;
    }

    /**
     * Returns the algebra class registered for a given named signature, if any.
     */
    public Algebra<?> getAlgebra(SignatureKind sigKind) {
        return this.algebraMap.get(sigKind);
    }

    /** Indicates if this algebra family can assign definite values to variables. */
    public boolean supportsSymbolic() {
        return this == POINT;
    }

    /** 
     * Returns the value for a given term.
     * @return the value {@code term} (in the appropriate algebra)
     */
    public Object toValue(Expression term) {
        switch (term.getKind()) {
        case CONST:
            return getAlgebra(term.getSignature()).toValueFromConstant((Constant) term);
        case VAR:
            assert this == POINT;
            return ((PointAlgebra<?>) getAlgebra(term.getSignature())).getPointValue();
        case CALL:
            CallExpr call = (CallExpr) term;
            List<Object> args = new ArrayList<Object>();
            for (Expression arg : call.getArgs()) {
                args.add(toValue(arg));
            }
            return getOperation(call.getOperator()).apply(args);
        default:
            assert false;
            return null;
        }
    }

    /**
     * Returns the method associated with a certain operator.
     */
    public Operation getOperation(Operator operator) {
        Algebra<?> algebra = getAlgebra(operator.getSignature());
        assert algebra != null;
        return getOperations(algebra).get(operator.getName());
    }

    /**
     * Returns, for a given algebra, the corresponding mapping from
     * method names to methods.
     */
    private Map<String,Operation> getOperations(Algebra<?> algebra) {
        Map<String,Operation> result = this.operationsMap.get(algebra);
        if (result == null) {
            result = createOperationsMap(algebra);
            this.operationsMap.put(algebra, result);
        }
        return result;
    }

    /**
     * Returns a mapping from operation names to operations for a given algebra.
     */
    private Map<String,Operation> createOperationsMap(Algebra<?> algebra) {
        Map<String,Operation> result = new HashMap<String,Operation>();
        // first find out what methods were declared in the signature
        Set<String> methodNames = new HashSet<String>();
        Method[] signatureMethods = algebra.getSignature().getSignatureClass().getDeclaredMethods();
        for (Method method : signatureMethods) {
            if (Modifier.isAbstract(method.getModifiers())
                && Modifier.isPublic(method.getModifiers())) {
                methodNames.add(method.getName());
            }
        }
        // now create an operation for all those declared methods
        // including those from superclasses
        Class<?> myClass = algebra.getClass();
        while (!methodNames.isEmpty()) {
            for (Method method : myClass.getDeclaredMethods()) {
                if (methodNames.remove(method.getName())) {
                    result.put(method.getName(), createOperation(algebra, method));
                }
            }
            myClass = myClass.getSuperclass();
        }
        return result;
    }

    /**
     * Returns a new algebra operation object for the given method (from a given
     * algebra).
     */
    private Operation createOperation(Algebra<?> algebra, Method method) {
        return new Operation(this, algebra, method);
    }

    @Override
    public String toString() {
        return this.algebraMap.toString();
    }

    /** The algebra family name. */
    private final String name;
    /** A map from signature kinds to algebras registered for that name. */
    private final Map<SignatureKind,Algebra<?>> algebraMap = new EnumMap<SignatureKind,Algebra<?>>(
        SignatureKind.class);
    /** Store of operations created from the algebras. */
    private final Map<Algebra<?>,Map<String,Operation>> operationsMap =
        new HashMap<Algebra<?>,Map<String,Operation>>();

    /** Returns the algebra register with the family of default algebras. */
    static public AlgebraFamily getInstance() {
        return DEFAULT;
    }

    /**
     * Returns the algebra register with a given name.
     */
    static public AlgebraFamily getInstance(String instanceName) {
        AlgebraFamily result = familyMap.get(instanceName);
        return result;
    }

    /** Mapping from names to algebra families. */
    private static Map<String,AlgebraFamily> familyMap = new HashMap<String,AlgebraFamily>();
    static {
        for (AlgebraFamily family : values()) {
            familyMap.put(family.getName(), family);
        }
    }

    /** Implementation of an algebra operation. */
    private static class Operation implements groove.algebra.Operation {
        Operation(AlgebraFamily register, Algebra<?> algebra, Method method) {
            this.algebra = algebra;
            this.method = method;
            SignatureKind returnType =
                algebra.getSignature().getOperator(method.getName()).getResultType();
            this.returnType = register.getAlgebra(returnType);
        }

        @Override
        public Object apply(List<Object> args) throws IllegalArgumentException {
            try {
                return this.method.invoke(this.algebra, args.toArray());
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException();
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof Error) {
                    throw (Error) e.getCause();
                } else {
                    throw new IllegalArgumentException();
                }
            }
        }

        @Override
        public Algebra<?> getAlgebra() {
            return this.algebra;
        }

        @Override
        public int getArity() {
            return this.method.getParameterTypes().length;
        }

        @Override
        public Algebra<?> getResultAlgebra() {
            return this.returnType;
        }

        @Override
        public String getName() {
            return this.method.getName();
        }

        @Override
        public String toString() {
            return getName();
        }

        private final Algebra<?> algebra;
        private final Algebra<?> returnType;
        private final Method method;
    }
}
