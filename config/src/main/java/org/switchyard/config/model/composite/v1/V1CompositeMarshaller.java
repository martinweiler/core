/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 */

package org.switchyard.config.model.composite.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.ComponentReferenceModel;
import org.switchyard.config.model.composite.ComponentServiceModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.composite.CompositeServiceModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.InterfaceModel;

/**
 * V1CompositeMarshaller.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1CompositeMarshaller extends BaseMarshaller {

    public V1CompositeMarshaller(Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(CompositeModel.COMPOSITE)) {
            return new V1CompositeModel(config, desc);
        } else if (name.equals(CompositeServiceModel.SERVICE)) {
            if (config.getFirstChildStartsWith(BindingModel.BINDING) != null) {
                return new V1CompositeServiceModel(config, desc);
            } else if (config.getFirstChildStartsWith(InterfaceModel.INTERFACE) != null) {
                return new V1ComponentServiceModel(config, desc);
            }
        } else if (name.startsWith(BindingModel.BINDING)) {
            return new V1BindingModel(config, desc);
        } else if (name.equals(ComponentModel.COMPONENT)) {
            return new V1ComponentModel(config, desc);
        } else if (name.startsWith(ComponentImplementationModel.IMPLEMENTATION)) {
            return new V1ComponentImplementationModel(config, desc);
        } else if (name.startsWith(InterfaceModel.INTERFACE)) {
            Configuration config_parent = config.getParent();
            if (config_parent != null) {
                if (config_parent.getName().equals(ComponentServiceModel.SERVICE)) {
                    return new V1ComponentServiceInterfaceModel(config, desc);
                } else if (config_parent.getName().equals(ComponentReferenceModel.REFERENCE)) {
                    return new V1ComponentReferenceInterfaceModel(config, desc);
                }
            }
        } else if (name.equals(ComponentReferenceModel.REFERENCE)) {
            Configuration config_parent = config.getParent();
            if (config_parent != null) {
                if (config_parent.getName().equals(CompositeModel.COMPOSITE)) {
                    return new V1CompositeReferenceModel(config, desc);
                } else if (config_parent.getName().equals(ComponentModel.COMPONENT)) {
                    return new V1ComponentReferenceModel(config, desc);
                }
            }
        }
        return null;
    }

}
