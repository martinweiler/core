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

package org.switchyard.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.handlers.HandlerChain;
import org.switchyard.metadata.ServiceInterface;
import org.switchyard.spi.Endpoint;
import org.switchyard.spi.ServiceRegistry;

/**
 * Standalone implementation of ServiceRegistry.
 */
public class DefaultServiceRegistry implements ServiceRegistry {

    private final Map<QName, List<ServiceRegistration>> _services =
        new HashMap<QName, List<ServiceRegistration>>();

    @Override
    public List<Service> getServicesForDomain(String domainName) {
        List<Service> domainServices = getServices();
        // Using an explicit iterator because we are removing elements
        for (Iterator<Service> i = domainServices.iterator(); i.hasNext();) {
            ServiceRegistration sr = (ServiceRegistration) i.next();
            // prune services that do not match the specified domain
            if (!sr.getDomain().getName().equals(domainName)) {
                i.remove();
            }
        }

        return domainServices;
    }

    @Override
    public synchronized List<Service> getServices() {
        LinkedList<Service> serviceList = new LinkedList<Service>();
        for (List<ServiceRegistration> services : _services.values()) {
            serviceList.addAll(services);
        }

        return serviceList;
    }

    @Override
    public synchronized List<Service> getServices(QName serviceName) {
        List<ServiceRegistration> services = _services.get(serviceName);
        if (services == null) {
            return Collections.emptyList();
        }

        return new LinkedList<Service>(services);
    }

    @Override
    public synchronized Service registerService(QName serviceName,
            ServiceInterface serviceInterface, Endpoint endpoint,
            HandlerChain handlers, ServiceDomain domain) {

        ServiceRegistration sr = new ServiceRegistration(
                serviceName, serviceInterface, endpoint, handlers, this, domain);

        List<ServiceRegistration> serviceList = _services.get(serviceName);
        if (serviceList == null) {
            serviceList = new LinkedList<ServiceRegistration>();
             _services.put(serviceName, serviceList);
        }

        serviceList.add(sr);
        return sr;
    }

    @Override
    public synchronized void unregisterService(Service service) {
        List<ServiceRegistration> serviceList =
            _services.get(service.getName());
        if (serviceList != null) {
            serviceList.remove(service);
        }
    }

}
