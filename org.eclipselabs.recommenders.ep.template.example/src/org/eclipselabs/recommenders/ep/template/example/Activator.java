package org.eclipselabs.recommenders.ep.template.example;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;

    @Override
    public void start(final BundleContext context) throws Exception {
        serviceRegistration = context.registerService(IHelloService.class.getName(), new HelloService(), null);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        serviceRegistration.unregister();
    }

}
