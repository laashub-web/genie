/*
 *
 *  Copyright 2020 Netflix, Inc.
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
package com.netflix.genie.agent.cli.argumentvalidators;

import com.beust.jcommander.IValueValidator;
import com.beust.jcommander.ParameterException;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Validates an URI parameter can be parsed as URI.
 * If the resource type is local file, validate it exists, it is readable, etc.
 * TODO this class does not have a test
 *
 * @author mprimi
 * @since 4.0.0
 */
public class URIValidator implements IValueValidator<String> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final String name, final String value) throws ParameterException {
        // Make sure the value is a valid URI
        final URI uri;
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            throw new ParameterException("Invalid URI " + value + " (for option: " + name + ")", e);
        }

        // If it's a file, make sure it exists, it's a file, it's readable, etc.
        if (uri.getScheme().equals("file")) {
            final Path path = Paths.get(uri.getPath());

            if (!Files.exists(path)) {
                throw new ParameterException("File " + value + " does not exist (for option: " + name + ")");
            } else if (!Files.isRegularFile(path)) {
                throw new ParameterException("File " + value + " is not a file (for option: " + name + ")");
            } else if (!Files.isReadable(path)) {
                throw new ParameterException("File " + value + " is not readable (for option: " + name + ")");
            }
        }
    }
}
