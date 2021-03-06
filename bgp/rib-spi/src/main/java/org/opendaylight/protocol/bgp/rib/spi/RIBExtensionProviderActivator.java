/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.bgp.rib.spi;

import javax.annotation.Nonnull;
import org.opendaylight.mdsal.binding.dom.codec.api.BindingNormalizedNodeSerializer;

public interface RIBExtensionProviderActivator {
    void startRIBExtensionProvider(
            @Nonnull RIBExtensionProviderContext context,
            @Nonnull BindingNormalizedNodeSerializer mappingService);

    void stopRIBExtensionProvider();
}
