/**
 * Copyright 2009 the original author or authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package it.av.youeat.ocm.util;

import java.sql.Timestamp;
import java.util.Calendar;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class DateUtil {

    private DateUtil() {

    }

    /**
     * Format used in the UI. dd/MM/yyyy HH:mm:ss
     */
    public static final DateTimeFormatter SDF2SHOW = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");

    /**
     * Return the actual time
     * 
     * @return Timestamp
     */
    public static Timestamp getTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

}
