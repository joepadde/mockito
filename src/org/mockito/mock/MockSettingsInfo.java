/*
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */

package org.mockito.mock;

import org.mockito.Incubating;

/**
 * Informs about the mock settings
 */
@Incubating
public interface MockSettingsInfo {

    /**
     * if the mock is serializable
     */
    boolean isSerializable();
}
