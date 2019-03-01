/*
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.core.tracing;

import com.google.common.annotations.Beta;
import io.jaegertracing.Configuration;
import io.jaegertracing.micrometer.MicrometerMetricsFactory;
import io.opentracing.Tracer;
import io.opentracing.contrib.tracerresolver.TracerResolver;
import io.opentracing.util.GlobalTracer;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Guice @{@link javax.inject.Provider} of @{@link io.opentracing.Tracer} objects.If Che metrics
 * are enabled, then enable custom metrics factory, which would expose jaeger internal metrics to
 * Prometheus. Register Tracer in @{@link io.opentracing.util.GlobalTracer} for future use by
 * classes that has no access to container like datasources, etc.
 */
@Beta
@Singleton
public class TracerProvider implements Provider<Tracer> {
  private final Tracer tracer;

  public TracerProvider() {
    if (Boolean.valueOf(System.getenv("CHE_METRICS_ENABLED"))) {
      MicrometerMetricsFactory metricsReporter = new MicrometerMetricsFactory();
      Configuration configuration = Configuration.fromEnv();
      tracer = configuration.getTracerBuilder().withMetricsFactory(metricsReporter).build();
    } else {
      tracer = TracerResolver.resolveTracer();
    }

    GlobalTracer.register(tracer);
  }

  @Override
  public Tracer get() {
    return tracer;
  }
}
