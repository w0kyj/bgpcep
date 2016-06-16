/*
 * Copyright (c) 2015 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.controller.config.yang.bmp.spi;

import com.google.common.reflect.AbstractInvocationHandler;
import com.google.common.reflect.Reflection;
import java.lang.reflect.Method;
import org.opendaylight.controller.config.api.osgi.WaitingServiceTracker;
import org.opendaylight.protocol.bmp.spi.registry.BmpExtensionProviderContext;
import org.osgi.framework.BundleContext;

/**
 * @deprecated Replaced by blueprint wiring
 */
@Deprecated
public class SimpleBmpExtensionProviderContextModule extends org.opendaylight.controller.config.yang.bmp.spi.AbstractSimpleBmpExtensionProviderContextModule {
    private BundleContext bundleContext;

    public SimpleBmpExtensionProviderContextModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public SimpleBmpExtensionProviderContextModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.controller.config.yang.bmp.spi.SimpleBmpExtensionProviderContextModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
        // add custom validation form module attributes here.
    }

    @Override
    public AutoCloseable createInstance() {
        final WaitingServiceTracker<BmpExtensionProviderContext> tracker =
                WaitingServiceTracker.create(BmpExtensionProviderContext.class, bundleContext);
        final BmpExtensionProviderContext service = tracker.waitForService(WaitingServiceTracker.FIVE_MINUTES);

        return Reflection.newProxy(AutoCloseableBmpExtensionProviderContext.class, new AbstractInvocationHandler() {
            @Override
            protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("close")) {
                    tracker.close();
                    return null;
                } else {
                    return method.invoke(service, args);
                }
            }
        });
    }

    void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    private static interface AutoCloseableBmpExtensionProviderContext extends BmpExtensionProviderContext, AutoCloseable {
    }
}
