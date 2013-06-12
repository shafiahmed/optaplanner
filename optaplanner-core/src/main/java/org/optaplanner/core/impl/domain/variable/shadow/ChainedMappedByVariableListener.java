/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.domain.variable.shadow;

import org.optaplanner.core.impl.domain.variable.PlanningVariableDescriptor;
import org.optaplanner.core.impl.domain.variable.listener.AbstractPlanningVariableListener;

public class ChainedMappedByVariableListener extends AbstractPlanningVariableListener<Object> {

    private final ShadowVariableDescriptor shadowVariableDescriptor;
    private final PlanningVariableDescriptor mappedByVariableDescriptor;

    public ChainedMappedByVariableListener(ShadowVariableDescriptor shadowVariableDescriptor) {
        this.shadowVariableDescriptor = shadowVariableDescriptor;
        mappedByVariableDescriptor = shadowVariableDescriptor.getMappedByVariableDescriptor();
    }

    public void beforeEntityAdded(Object entity) {
        // Do nothing
    }

    public void afterEntityAdded(Object entity) {
        insert(entity);
    }

    public void beforeVariableChanged(Object entity) {
        retract(entity);
    }

    public void afterVariableChanged(Object entity) {
        insert(entity);
    }

    public void beforeEntityRemoved(Object entity) {
        retract(entity);
    }

    public void afterEntityRemoved(Object entity) {
        // Do nothing
    }

    private void insert(Object entity) {
        Object shadowEntity = mappedByVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != null) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + mappedByVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a mappedBy variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not null.\n"
                        + "Verify the consistency of your input problem for that mappedBy variable.");
            }
            shadowVariableDescriptor.setValue(shadowEntity, entity);
        }
    }

    private void retract(Object entity) {
        Object shadowEntity = mappedByVariableDescriptor.getValue(entity);
        if (shadowEntity != null) {
            Object shadowValue = shadowVariableDescriptor.getValue(shadowEntity);
            if (shadowValue != entity) {
                throw new IllegalStateException("The entity (" + entity
                        + ") has a variable (" + mappedByVariableDescriptor.getVariableName()
                        + ") with value (" + shadowEntity
                        + ") which has a mappedBy variable (" + shadowVariableDescriptor.getVariableName()
                        + ") with a value (" + shadowValue + ") which is not that entity.\n"
                        + "Verify the consistency of your input problem for that mappedBy variable.");
            }
            shadowVariableDescriptor.setValue(shadowEntity, null);
        }
    }

}
