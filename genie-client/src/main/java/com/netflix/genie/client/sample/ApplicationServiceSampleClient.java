/*
 *
 *  Copyright 2014 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.genie.client.sample;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.netflix.config.ConfigurationManager;
import com.netflix.genie.client.ApplicationServiceClient;
import com.netflix.genie.common.exceptions.CloudServiceException;
import com.netflix.genie.common.model.Application;
import com.netflix.genie.common.model.Command;
import com.netflix.genie.common.model.Types.ApplicationStatus;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A sample client demonstrating usage of the Application Service Client.
 *
 * @author tgianos
 */
public final class ApplicationServiceSampleClient {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationServiceSampleClient.class);

    /**
     * The id for the sample application.
     */
    protected static final String ID = "mr2";

    /**
     * The name for the sample application.
     */
    protected static final String APP_NAME = "MapReduce2";

    /**
     * Private.
     */
    private ApplicationServiceSampleClient() {
        // never called
    }

    /**
     * Main for running client code.
     *
     * @param args
     * @throws java.lang.Exception
     */
    public static void main(final String[] args) throws Exception {

        // Initialize Eureka, if it is being used
        // LOG.info("Initializing Eureka");
        // ApplicationServiceClient.initEureka("test");
        LOG.info("Initializing list of Genie servers");
        ConfigurationManager.getConfigInstance().setProperty("genieClient.ribbon.listOfServers",
                "localhost:7001");

        LOG.info("Initializing ApplicationServiceClient");
        final ApplicationServiceClient appClient = ApplicationServiceClient.getInstance();

        LOG.info("Creating new application config");
        final Application app1 = appClient.createApplication(getSampleApplication(null));
        LOG.info("Application configuration created with id: " + app1.getId());
        LOG.info(app1.toString());

        LOG.info("Getting Applications using specified filter criteria name =  " + APP_NAME);
        final Multimap<String, String> params = ArrayListMultimap.create();
        params.put("name", APP_NAME);
        final List<Application> appResponses = appClient.getApplications(params);
        if (appResponses.isEmpty()) {
            LOG.info("No applications found for specified criteria.");
        } else {
            LOG.info("Applications found:");
            for (final Application appResponse : appResponses) {
                LOG.info(appResponse.toString());
            }
        }

        LOG.info("Getting application config by id");
        final Application app2 = appClient.getApplication(app1.getId());
        LOG.info(app2.toString());

        LOG.info("Updating existing application config");
        app2.setStatus(ApplicationStatus.INACTIVE);
        final Application app3 = appClient.updateApplication(app1.getId(), app2);
        LOG.info(app3.toString());

        LOG.info("Configurations for application with id " + app1.getId());
        final Set<String> configs = appClient.getConfigsForApplication(app1.getId());
        for (final String config : configs) {
            LOG.info("Config = " + config);
        }

        LOG.info("Adding configurations to application with id " + app1.getId());
        final Set<String> newConfigs = new HashSet<String>();
        newConfigs.add("someNewConfigFile");
        newConfigs.add("someOtherNewConfigFile");
        final Set<String> configs2 = appClient.addConfigsToApplication(app1.getId(), newConfigs);
        for (final String config : configs2) {
            LOG.info("Config = " + config);
        }

        LOG.info("Updating set of configuration files associated with id " + app1.getId());
        //This should remove the original config leaving only the two in this set
        final Set<String> configs3 = appClient.updateConfigsForApplication(app1.getId(), newConfigs);
        for (final String config : configs3) {
            LOG.info("Config = " + config);
        }

        LOG.info("Deleting all the configuration files from the application with id " + app1.getId());
        //This should remove the original config leaving only the two in this set
        final Set<String> configs4 = appClient.removeAllConfigsForApplication(app1.getId());
        for (final String config : configs4) {
            //Shouldn't print anything
            LOG.info("Config = " + config);
        }

        LOG.info("Jars for application with id " + app1.getId());
        final Set<String> jars = appClient.getJarsForApplication(app1.getId());
        for (final String jar : jars) {
            LOG.info("jar = " + jar);
        }

        LOG.info("Adding jars to application with id " + app1.getId());
        final Set<String> newJars = new HashSet<String>();
        newJars.add("someNewJarFile.jar");
        newJars.add("someOtherNewJarFile.jar");
        final Set<String> jars2 = appClient.addJarsToApplication(app1.getId(), newJars);
        for (final String jar : jars2) {
            LOG.info("jar = " + jar);
        }

        LOG.info("Updating set of jars associated with id " + app1.getId());
        //This should remove the original jar leaving only the two in this set
        final Set<String> jars3 = appClient.updateJarsForApplication(app1.getId(), newJars);
        for (final String jar : jars3) {
            LOG.info("jar = " + jar);
        }

        LOG.info("Deleting all the jars from the application with id " + app1.getId());
            //This should remove the original jar leaving only the two in this set
        final Set<String> jars4 = appClient.removeAllJarsForApplication(app1.getId());
        for (final String jar : jars4) {
            //Shouldn't print anything
            LOG.info("jar = " + jar);
        }

        LOG.info("Getting the commands associated with id " + app1.getId());
        final Set<Command> commands = appClient.getCommandsForApplication(app1.getId());
        for (final Command command : commands) {
            LOG.info("Command: " + command.toString());
        }

        LOG.info("Deleting application using id");
        final Application app4 = appClient.deleteApplication(app1.getId());
        LOG.info("Deleted application with id: " + app4.getId());
        LOG.info(app4.toString());

        LOG.info("Done");
    }

    /**
     * Helper method to quickly create an application for use in samples.
     *
     * @param id The id to use or null/empty if want one created.
     * @return A sample application with id MR2
     * @throws CloudServiceException
     */
    public static Application getSampleApplication(final String id) throws CloudServiceException {
        final Application app = new Application(APP_NAME, "tgianos", ApplicationStatus.ACTIVE);
        if (StringUtils.isNotEmpty(id)) {
            app.setId(id);
        }
        app.setVersion("2.4.0");
        final Set<String> configs = new HashSet<String>();
        configs.add("s3://netflix-bdp-emr-clusters/users/bdp/hquery/20140505/185527/genie/mapred-site.xml");
        app.setConfigs(configs);
        final Set<String> jars = new HashSet<String>();
        jars.add("s3://netflix-dataoven-test/genie2/application/mapreduce1/foo.jar");
        app.setJars(jars);
        return app;
    }
}
