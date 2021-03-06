/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.server.provider;

import org.jboss.as.clustering.controller.CapabilityServiceBuilder;
import org.jboss.as.controller.capability.CapabilityServiceSupport;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.ServiceTarget;
import org.jboss.msc.service.ValueService;
import org.jboss.msc.value.Value;
import org.wildfly.clustering.group.Group;
import org.wildfly.clustering.provider.ServiceProviderRegistry;
import org.wildfly.clustering.service.Builder;
import org.wildfly.clustering.service.InjectedValueDependency;
import org.wildfly.clustering.service.ValueDependency;
import org.wildfly.clustering.spi.ClusteringCacheRequirement;

/**
 * Builds a non-clustered {@link ServiceProviderRegistrationFactory} service.
 * @author Paul Ferraro
 */
public class LocalServiceProviderRegistryBuilder<T> implements CapabilityServiceBuilder<ServiceProviderRegistry<T>>, Value<ServiceProviderRegistry<T>> {

    private final ServiceName name;
    private final String containerName;
    private final String cacheName;

    private volatile ValueDependency<Group> group;

    public LocalServiceProviderRegistryBuilder(ServiceName name, String containerName, String cacheName) {
        this.name = name;
        this.containerName = containerName;
        this.cacheName = cacheName;
    }

    @Override
    public ServiceProviderRegistry<T> getValue() {
        return new LocalServiceProviderRegistry<>(this.group.getValue());
    }

    @Override
    public ServiceName getServiceName() {
        return this.name;
    }

    @Override
    public Builder<ServiceProviderRegistry<T>> configure(CapabilityServiceSupport support) {
        this.group = new InjectedValueDependency<>(ClusteringCacheRequirement.GROUP.getServiceName(support, this.containerName, this.cacheName), Group.class);
        return this;
    }

    @Override
    public ServiceBuilder<ServiceProviderRegistry<T>> build(ServiceTarget target) {
        return this.group.register(target.addService(this.name, new ValueService<>(this)).setInitialMode(ServiceController.Mode.ON_DEMAND));
    }
}
