package jdk.internal.sys.posix;

import static org.qbicc.runtime.CNative.*;
import static org.qbicc.runtime.stdc.Stddef.*;

@define(value = "_POSIX_C_SOURCE", as = "200809L")
@include("<limits.h>")
public final class Limits {
    public static final c_int IOV_MAX = constant();

    public static final size_t PATH_MAX = constant();

    public static final size_t PTHREAD_STACK_MIN = constant();
}
