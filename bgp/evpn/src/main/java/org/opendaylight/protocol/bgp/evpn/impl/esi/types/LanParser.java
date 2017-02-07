/*
 * Copyright (c) 2016 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.protocol.bgp.evpn.impl.esi.types;

import static org.opendaylight.protocol.bgp.evpn.impl.esi.types.EsiModelUtil.extractBP;
import static org.opendaylight.protocol.bgp.evpn.impl.esi.types.EsiModelUtil.extractBrigeMac;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import org.opendaylight.protocol.util.ByteArray;
import org.opendaylight.protocol.util.ByteBufWriteUtil;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.IetfYangUtil;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.EsiType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.esi.Esi;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.esi.esi.LanAutoGeneratedCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.esi.esi.LanAutoGeneratedCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.esi.esi.lan.auto.generated._case.LanAutoGenerated;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.bgp.evpn.rev171213.esi.esi.lan.auto.generated._case.LanAutoGeneratedBuilder;
import org.opendaylight.yangtools.yang.data.api.schema.ContainerNode;

final class LanParser extends AbstractEsiType {

    @Override
    public void serializeBody(final Esi esi, final ByteBuf body) {
        Preconditions.checkArgument(esi instanceof LanAutoGeneratedCase, "Unknown esi instance. Passed %s. Needed LanAutoGeneratedCase.", esi.getClass());
        final LanAutoGenerated lan = ((LanAutoGeneratedCase) esi).getLanAutoGenerated();
        body.writeBytes(IetfYangUtil.INSTANCE.bytesFor(lan.getRootBridgeMacAddress()));
        ByteBufWriteUtil.writeUnsignedShort(lan.getRootBridgePriority(), body);
        body.writeZero(ZERO_BYTE);
    }

    @Override
    protected EsiType getType() {
        return EsiType.LanAutoGenerated;
    }

    @Override
    public Esi serializeEsi(final ContainerNode esi) {
        final LanAutoGeneratedBuilder lanBuilder = new LanAutoGeneratedBuilder();
        lanBuilder.setRootBridgeMacAddress(extractBrigeMac(esi));
        lanBuilder.setRootBridgePriority(extractBP(esi));
        return new LanAutoGeneratedCaseBuilder().setLanAutoGenerated(lanBuilder.build()).build();
    }

    @Override
    public Esi parseEsi(final ByteBuf buffer) {
        final LanAutoGenerated t2 = new LanAutoGeneratedBuilder()
            .setRootBridgeMacAddress(IetfYangUtil.INSTANCE.macAddressFor(ByteArray.readBytes(buffer, MAC_ADDRESS_LENGTH)))
            .setRootBridgePriority(buffer.readUnsignedShort()).build();
        return new LanAutoGeneratedCaseBuilder().setLanAutoGenerated(t2).build();
    }
}
