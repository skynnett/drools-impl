package com.wordpress.salaboy.pachinkoo;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wordpress.salaboy.pachinkoo.util.ReflectionUtils;

/**
 *
 * @author salaboy
 */
public class AlphaNode extends ObjectSource implements ObjectSink {

    private final COMPARATOR comparator;

    public COMPARATOR getComparator() {
        return comparator;
    }

    private final String variable;

    public String getVariable() {
        return variable;
    }

    private final String fieldName;
    private final String value;

    private ClassVariable classVariable;

    public ClassVariable getClassVariable() {
        return classVariable;
    }

    public void setClassVariable(ClassVariable classVariable) {
        this.classVariable = classVariable;
    }

    private final FieldVariable fieldVariable;

    public FieldVariable getFieldVariable() {
        return fieldVariable;
    }

    public AlphaNode(ClassVariable classVariable, COMPARATOR comparator, String fieldName, String value) {
        this.classVariable = classVariable;
        this.variable = null;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.value = value;
        this.fieldVariable = null;
    }

    public AlphaNode(COMPARATOR comparator, String fieldName, String value) {
        this.variable = null;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.value = value;
        this.fieldVariable = null;
    }

    public AlphaNode(COMPARATOR comparator, String fieldName, FieldVariable fieldVariable) {
        this.variable = null;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.value = null;
        this.fieldVariable = fieldVariable;
    }

    public AlphaNode(String variable, COMPARATOR comparator, String fieldName, String value) {
        this.variable = variable;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.value = value;
        this.fieldVariable = null;
    }

    public AlphaNode(String variable, COMPARATOR comparator, String fieldName, FieldVariable fieldVariable) {
        this.variable = variable;
        this.comparator = comparator;
        this.fieldName = fieldName;
        this.value = null;
        this.fieldVariable = fieldVariable;
    }

    @Override
    public long getId() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void assertObject(Handle factHandle, PropagationContext propagationContext, WorkingMemory wm) {
        try {
            final Object object = factHandle.getObject();
            final Class<?> clazz = object.getClass();
            final Field field = ReflectionUtils.getField(clazz, fieldName);
            if (field == null) {
                return;
            }
            boolean shouldPropagate = false;
            final Object result = field.get(object);
            switch (comparator) {
                case EQUAL:
                    if (Objects.equals(result, value)) {
                        shouldPropagate = true;
                    }
                    break;
                case NOT_EQUAL:
                    if (!Objects.equals(result, value)) {
                        shouldPropagate = true;
                    }
                    break;
            }
            if (shouldPropagate) {
                if (this.classVariable != null) {
                    propagationContext.getBindingVariables().put(this.classVariable.getName(), object);
                }
                if (this.variable != null) {
                    propagationContext.getBindingVariables().put(this.variable, result);
                }
                sinkPropagator.propagateAssertObject(factHandle, propagationContext, wm);
            }

        } catch (final Exception ex) {
            Logger.getLogger(AlphaNode.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
