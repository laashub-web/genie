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

import java.util.List;

/**
 * Validates a URI collection parameter by delegating validation to {@link URIValidator}.
 * TODO this class does not have a test
 *
 * @author mprimi
 * @since 4.0.0
 */
public class URIListValidator implements IValueValidator<List<String>> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final String name, final List<String> values) throws ParameterException {
        final URIValidator validator = new URIValidator();
        for (final String value : values) {
            validator.validate(name, value);
        }
    }
}
