/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.impl;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.opendaylight.protocol.concepts.Ipv6Util;
import org.opendaylight.protocol.pcep.PCEPDeserializerException;
import org.opendaylight.protocol.pcep.impl.subobject.EROAsNumberSubobjectParser;
import org.opendaylight.protocol.pcep.impl.subobject.EROIpPrefixSubobjectParser;
import org.opendaylight.protocol.pcep.impl.subobject.EROPathKeySubobjectParser;
import org.opendaylight.protocol.pcep.impl.subobject.EROUnnumberedInterfaceSubobjectParser;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.AsNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.IpPrefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev100924.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.PathKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.PceId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.SubobjectsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.subobjects.subobject.type.PathKeyBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.AsNumberBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.IpPrefixBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.rsvp.rev130820.basic.explicit.route.subobjects.subobject.type.UnnumberedBuilder;

public class PCEPEROSubobjectParserTest {
	private static final byte[] ip4PrefixBytes = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0x16, (byte) 0x00 };
	private static final byte[] ip6PrefixBytes = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0x16, (byte) 0x00 };
	private static final byte[] asNumberBytes = { (byte) 0x00, (byte) 0x64 };
	private static final byte[] unnumberedBytes = { (byte) 0x00, (byte) 0x00, (byte) 0x12, (byte) 0x34, (byte) 0x50, (byte) 0x00,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	private static final byte[] pathKey32Bytes = { (byte) 0x12, (byte) 0x34, (byte) 0x12, (byte) 0x34, (byte) 0x50, (byte) 0x00 };
	private static final byte[] pathKey128Bytes = { (byte) 0x12, (byte) 0x34, (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78,
			(byte) 0x9A, (byte) 0xBC, (byte) 0xDE, (byte) 0x12, (byte) 0x34, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00 };

	@Test
	public void testEROIp4PrefixSubobject() throws PCEPDeserializerException {
		final EROIpPrefixSubobjectParser parser = new EROIpPrefixSubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setLoose(true);
		subs.setSubobjectType(new IpPrefixBuilder().setIpPrefix(new IpPrefix(new Ipv4Prefix("255.255.255.255/22"))).build());
		assertEquals(subs.build(), parser.parseSubobject(ip4PrefixBytes, true));
		assertArrayEquals(ip4PrefixBytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	public void testEROIp6PrefixSubobject() throws PCEPDeserializerException {
		final EROIpPrefixSubobjectParser parser = new EROIpPrefixSubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setSubobjectType(new IpPrefixBuilder().setIpPrefix(
				new IpPrefix(Ipv6Util.prefixForBytes(new byte[] { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
						(byte) 0xFF, (byte) 0xFF, (byte) 0xFF }, 22))).build());
		subs.setLoose(false);
		assertEquals(subs.build(), parser.parseSubobject(ip6PrefixBytes, false));
		assertArrayEquals(ip6PrefixBytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	public void testEROAsNumberSubobject() throws PCEPDeserializerException {
		final EROAsNumberSubobjectParser parser = new EROAsNumberSubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setLoose(true);
		subs.setSubobjectType(new AsNumberBuilder().setAsNumber(new AsNumber(0x64L)).build());
		assertEquals(subs.build(), parser.parseSubobject(asNumberBytes, true));
		assertArrayEquals(asNumberBytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	public void testEROUnnumberedSubobject() throws PCEPDeserializerException {
		final EROUnnumberedInterfaceSubobjectParser parser = new EROUnnumberedInterfaceSubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setLoose(true);
		subs.setSubobjectType(new UnnumberedBuilder().setRouterId(0x12345000L).setInterfaceId(0xffffffffL).build());
		assertEquals(subs.build(), parser.parseSubobject(unnumberedBytes, true));
		assertArrayEquals(unnumberedBytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	public void testEROPathKey32Subobject() throws PCEPDeserializerException {
		final EROPathKeySubobjectParser parser = new EROPathKeySubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setLoose(true);
		final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.subobjects.subobject.type.path.key.PathKeyBuilder pBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.subobjects.subobject.type.path.key.PathKeyBuilder();
		pBuilder.setPceId(new PceId(new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x50, (byte) 0x00 }));
		pBuilder.setPathKey(new PathKey(4660));
		subs.setSubobjectType(new PathKeyBuilder().setPathKey(pBuilder.build()).build());
		assertEquals(subs.build(), parser.parseSubobject(pathKey32Bytes, true));
		assertArrayEquals(pathKey32Bytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	public void testEROPathKey128Subobject() throws PCEPDeserializerException {
		final EROPathKeySubobjectParser parser = new EROPathKeySubobjectParser();
		final SubobjectsBuilder subs = new SubobjectsBuilder();
		subs.setLoose(true);
		final org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.subobjects.subobject.type.path.key.PathKeyBuilder pBuilder = new org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.pcep.types.rev131005.explicit.route.object.subobjects.subobject.type.path.key.PathKeyBuilder();
		pBuilder.setPceId(new PceId(new byte[] { (byte) 0x12, (byte) 0x34, (byte) 0x56, (byte) 0x78, (byte) 0x9A, (byte) 0xBC, (byte) 0xDE,
				(byte) 0x12, (byte) 0x34, (byte) 0x54, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 }));
		pBuilder.setPathKey(new PathKey(4660));
		subs.setSubobjectType(new PathKeyBuilder().setPathKey(pBuilder.build()).build());
		assertEquals(subs.build(), parser.parseSubobject(pathKey128Bytes, true));
		assertArrayEquals(pathKey128Bytes, parser.serializeSubobject(subs.build()));
	}

	@Test
	@Ignore
	public void testEROSubojectsSerDeserWithoutBin() throws PCEPDeserializerException {
		// objsToTest.add(new EROType1LabelSubobject(0xFFFF51F2L, true, false));
		// objsToTest.add(new EROType1LabelSubobject(0x12345648L, false, true));
		// objsToTest.add(new EROWavebandSwitchingLabelSubobject(0x12345678L, 0x87654321L, 0xFFFFFFFFL, false, false));
		// objsToTest.add(new EROExplicitExclusionRouteSubobject(Arrays.asList((ExcludeRouteSubobject) new
		// XROAsNumberSubobject(new AsNumber((long) 2588), true))));
	}
}
