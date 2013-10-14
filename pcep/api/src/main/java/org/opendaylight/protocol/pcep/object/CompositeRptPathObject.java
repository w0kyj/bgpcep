/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.protocol.pcep.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opendaylight.protocol.pcep.PCEPObject;

/**
 * Structure that combines set of related objects.
 *
 * @see <a
 *      href="http://tools.ietf.org/html/draft-crabbe-pce-stateful-pce-02#section-6.1">PCRpt
 *      Message</a>
 */
public class CompositeRptPathObject {
	private final PCEPExplicitRouteObject explicitRoute;

	private final PCEPLspaObject lspa;

	private final PCEPExistingPathBandwidthObject bandwidth;

	private final PCEPReportedRouteObject reportedRoute;

	private List<PCEPMetricObject> metrics;

	/**
	 * Constructs basic composite object only with mandatory objects.
	 *
	 * @param explicitRoute
	 *            PCEPExplicitRouteObject. Can't be null.
	 */
	public CompositeRptPathObject(final PCEPExplicitRouteObject explicitRoute) {
		this(explicitRoute, null, null, null, null);
	}

	/**
	 * Constructs composite object with optional objects.
	 *
	 * @param explicitRoute
	 *            PCEPExplicitRouteObject. Can't be null.
	 * @param lspa
	 *            PCEPLspaObject
	 * @param bandwidth
	 *            PCEPRequestedPathBandwidthObject
	 * @param reportedRoute
	 *            PCEPReportedRouteObject
	 * @param metrics
	 *            List<PCEPMetricObject>
	 */
	public CompositeRptPathObject(final PCEPExplicitRouteObject explicitRoute, final PCEPLspaObject lspa, final PCEPExistingPathBandwidthObject bandwidth,
			final PCEPReportedRouteObject reportedRoute, final List<PCEPMetricObject> metrics) {
		if (explicitRoute == null)
			throw new IllegalArgumentException("Explicit Route Object is mandatory.");
		this.explicitRoute = explicitRoute;
		this.lspa = lspa;
		this.bandwidth = bandwidth;
		if (metrics != null)
			this.metrics = metrics;
		else
			this.metrics = Collections.emptyList();
		this.reportedRoute = reportedRoute;
	}

	/**
	 * Gets list of all objects, which are in appropriate order.
	 *
	 * @return List<PCEPObject>. Can't be null or empty.
	 */
	public List<PCEPObject> getCompositeAsList() {
		final List<PCEPObject> list = new ArrayList<PCEPObject>();
		list.add(this.explicitRoute);
		if (this.lspa != null)
			list.add(this.lspa);
		if (this.bandwidth != null)
			list.add(this.bandwidth);
		if (this.reportedRoute != null)
			list.add(this.reportedRoute);
		if (this.metrics != null && !this.metrics.isEmpty())
			list.addAll(this.metrics);
		return list;
	}

	/**
	 * Creates this object from a list of PCEPObjects.
	 * @param objects List<PCEPObject> list of PCEPObjects from whose this
	 * object should be created.
	 * @return CompositeRptPathObject
	 */
	public static CompositeRptPathObject getCompositeFromList(final List<PCEPObject> objects) {
		if (objects == null || objects.isEmpty()) {
			throw new IllegalArgumentException("List cannot be null or empty.");
		}

		PCEPExplicitRouteObject explicitRoute = null;
		if (objects.get(0) instanceof PCEPExplicitRouteObject) {
			explicitRoute = (PCEPExplicitRouteObject) objects.get(0);
			objects.remove(explicitRoute);
		} else
			return null;

		PCEPLspaObject lspa = null;
		PCEPExistingPathBandwidthObject bandwidth = null;
		final List<PCEPMetricObject> metrics = new ArrayList<PCEPMetricObject>();
		PCEPReportedRouteObject rro = null;

		int state = 1;
		while (!objects.isEmpty()) {
			final PCEPObject obj = objects.get(0);

			switch (state) {
				case 1:
					state = 2;
					if (obj instanceof PCEPLspaObject) {
						lspa = (PCEPLspaObject) obj;
						break;
					}
				case 2:
					state = 3;
					if (obj instanceof PCEPExistingPathBandwidthObject) {
						bandwidth = (PCEPExistingPathBandwidthObject) obj;
						break;
					}
				case 3:
					state = 4;
					if (obj instanceof PCEPReportedRouteObject) {
						rro = (PCEPReportedRouteObject) obj;
						break;
					}
				case 4:
					if (obj instanceof PCEPMetricObject) {
						metrics.add((PCEPMetricObject) obj);
						state = 4;
						break;
					} else
						state = 5;
			}

			if (state == 5) {
				break;
			}

			objects.remove(obj);
		}

		return new CompositeRptPathObject(explicitRoute, lspa, bandwidth, rro, metrics);
	}

	/**
	 * Gets {@link PCEPExplicitRouteObject}.
	 *
	 * @return PCEPExplicitRouteObject. Can't be null.
	 */
	public PCEPExplicitRouteObject getExcludedRoute() {
		return this.explicitRoute;
	}

	/**
	 * Gets {@link PCEPLspaObject}.
	 *
	 * @return PCEPLspaObject. May be null.
	 */
	public PCEPLspaObject getLspa() {
		return this.lspa;
	}

	/**
	 * Gets {@link PCEPBandwidthObject}.
	 *
	 * @return PCEPBandwidthObject. May be null.
	 */
	public PCEPBandwidthObject getBandwidth() {
		return this.bandwidth;
	}

	/**
	 * Gets list of {@link PCEPMetricObject}.
	 *
	 * @return List<PCEPMetricObject>. Can't be null, but may be empty.
	 */
	public List<PCEPMetricObject> getMetrics() {
		return this.metrics;
	}

	/**
	 * Gets {@link PCEPReportedRouteObject}.
	 *
	 * @return PCEPReportedRouteObject. May be null.
	 */
	public PCEPReportedRouteObject getReportedRoute() {
		return this.reportedRoute;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.bandwidth == null) ? 0 : this.bandwidth.hashCode());
		result = prime * result + ((this.explicitRoute == null) ? 0 : this.explicitRoute.hashCode());
		result = prime * result + ((this.lspa == null) ? 0 : this.lspa.hashCode());
		result = prime * result + ((this.metrics == null) ? 0 : this.metrics.hashCode());
		result = prime * result + ((this.reportedRoute == null) ? 0 : this.reportedRoute.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final CompositeRptPathObject other = (CompositeRptPathObject) obj;
		if (this.bandwidth == null) {
			if (other.bandwidth != null)
				return false;
		} else if (!this.bandwidth.equals(other.bandwidth))
			return false;
		if (this.explicitRoute == null) {
			if (other.explicitRoute != null)
				return false;
		} else if (!this.explicitRoute.equals(other.explicitRoute))
			return false;
		if (this.lspa == null) {
			if (other.lspa != null)
				return false;
		} else if (!this.lspa.equals(other.lspa))
			return false;
		if (this.metrics == null) {
			if (other.metrics != null)
				return false;
		} else if (!this.metrics.equals(other.metrics))
			return false;
		if (this.reportedRoute == null) {
			if (other.reportedRoute != null)
				return false;
		} else if (!this.reportedRoute.equals(other.reportedRoute))
			return false;
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("CompositeRptPathObject [explicitRoute=");
		builder.append(this.explicitRoute);
		builder.append(", lspa=");
		builder.append(this.lspa);
		builder.append(", bandwidth=");
		builder.append(this.bandwidth);
		builder.append(", reportedRoute=");
		builder.append(this.reportedRoute);
		builder.append(", metrics=");
		builder.append(this.metrics);
		builder.append("]");
		return builder.toString();
	}
}
