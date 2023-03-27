package jdk.internal.sys.zlib;

import static org.qbicc.runtime.CNative.*;

import org.qbicc.runtime.CNative;

/**
 *
 */
@include("<zlib.h>")
@lib("z")
public final class ZLib {
    public static final class uLong extends word {}
    public static final class uInt extends word {}
    public static final class Bytef extends word {}
    public static final class z_off_t extends word {}
    public static final class z_size_t extends word {}

    public static native uLong crc32(uLong crc, ptr<@CNative.c_const Bytef> buf, uInt len);
}
