/*
 * This code is based on OpenJDK source file(s) which contain the following copyright notice:
 *
 * ------
 * Copyright (c) 2001, 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 * ------
 *
 * This file may contain additional modifications which are Copyright (c) Red Hat and other
 * contributors.
 */

package jdk.internal.reflect;

import java.lang.reflect.*;

import org.qbicc.rt.annotation.Tracking;
import org.qbicc.runtime.Build;

@Tracking("src/java.base/share/classes/jdk/internal/reflect/NativeConstructorAccessorImpl.java")
class NativeConstructorAccessorImpl$_native {

    // Hook this method to provide a user-friendly error message at runtime.
    // If we get here, it means that the program has attempted to do
    // newInstance() on a Class that was not registered for runtime reflection.
    // Give an error message that includes the class name, to make it easier for
    // the user to properly annotate that Class.
    private static Object newInstance0(Constructor<?> c, Object[] args)
            throws InstantiationException,
            IllegalArgumentException,
            InvocationTargetException {
        if (Build.isTarget()) {
            Class<?> clazz = c.getDeclaringClass();
            throw new UnsupportedOperationException("Must register "+clazz+" for runtime reflection at compile time");
        } else {
            throw new IllegalStateException("NativeConstructorAccessorImpl.newInstance0() should have been intercepted in interpreter!");
        }
    }
}