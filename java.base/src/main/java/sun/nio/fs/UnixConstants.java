package sun.nio.fs;

import cc.quarkus.qcc.runtime.posix.Errno;
import cc.quarkus.qcc.runtime.posix.Fcntl;
import cc.quarkus.qcc.runtime.posix.SysStat;
import cc.quarkus.qcc.runtime.posix.Unistd;
import cc.quarkus.qccrt.annotation.Tracking;

@Tracking("java.base/unix/classes/sun/nio/fs/UnixConstants.java.template")
final class UnixConstants {
    private UnixConstants() {}

    static final int O_RDONLY = Fcntl.O_RDONLY.intValue();
    static final int O_WRONLY = Fcntl.O_WRONLY.intValue();
    static final int O_RDWR = Fcntl.O_RDWR.intValue();
    static final int O_APPEND = Fcntl.O_APPEND.intValue();
    static final int O_CREAT = Fcntl.O_CREAT.intValue();
    static final int O_EXCL = Fcntl.O_EXCL.intValue();
    static final int O_TRUNC = Fcntl.O_TRUNC.intValue();
    static final int O_SYNC = Fcntl.O_SYNC.intValue();
    static final int O_DSYNC = Fcntl.O_DSYNC.intValue();
    static final int O_NOFOLLOW = Fcntl.O_NOFOLLOW.intValue();
    static final int O_DIRECT = Fcntl.O_DIRECT.intValue();

    static final int S_IRUSR = SysStat.S_IRUSR.intValue();
    static final int S_IWUSR = SysStat.S_IWUSR.intValue();
    static final int S_IXUSR = SysStat.S_IXUSR.intValue();
    static final int S_IRGRP = SysStat.S_IRGRP.intValue();
    static final int S_IWGRP = SysStat.S_IWGRP.intValue();
    static final int S_IXGRP = SysStat.S_IXGRP.intValue();
    static final int S_IROTH = SysStat.S_IROTH.intValue();
    static final int S_IWOTH = SysStat.S_IWOTH.intValue();
    static final int S_IXOTH = SysStat.S_IXOTH.intValue();
    static final int S_IFMT = SysStat.S_IFMT.intValue();
    static final int S_IFREG = SysStat.S_IFREG.intValue();
    static final int S_IFDIR = SysStat.S_IFDIR.intValue();
    static final int S_IFLNK = SysStat.S_IFLNK.intValue();
    static final int S_IFCHR = SysStat.S_IFCHR.intValue();
    static final int S_IFBLK = SysStat.S_IFBLK.intValue();
    static final int S_IFIFO = SysStat.S_IFIFO.intValue();

    static final int S_IAMB = S_IRUSR | S_IWUSR | S_IXUSR | S_IRGRP | S_IWGRP | S_IXGRP | S_IROTH | S_IWOTH | S_IXOTH;

    static final int R_OK = Unistd.R_OK.intValue();
    static final int W_OK = Unistd.W_OK.intValue();
    static final int X_OK = Unistd.X_OK.intValue();
    static final int F_OK = Unistd.F_OK.intValue();

    static final int ENOENT = Errno.ENOENT.intValue();
    static final int ENXIO = Errno.ENXIO.intValue();
    static final int EACCES = Errno.EACCES.intValue();
    static final int EEXIST = Errno.EEXIST.intValue();
    static final int ENOTDIR = Errno.ENOTDIR.intValue();
    static final int EINVAL = Errno.EINVAL.intValue();
    static final int EXDEV = Errno.EXDEV.intValue();
    static final int EISDIR = Errno.EISDIR.intValue();
    static final int ENOTEMPTY = Errno.ENOTEMPTY.intValue();
    static final int ENOSPC = Errno.ENOSPC.intValue();
    static final int EAGAIN = Errno.EAGAIN.intValue();
    static final int EWOULDBLOCK = Errno.EWOULDBLOCK.intValue();
    static final int ENOSYS = Errno.ENOSYS.intValue();
    static final int ELOOP = Errno.ELOOP.intValue();
    static final int EROFS = Errno.EROFS.intValue();
    static final int ENODATA = Errno.ENODATA.intValue();
    static final int ERANGE = Errno.ERANGE.intValue();
    static final int EMFILE = Errno.EMFILE.intValue();

    static final int AT_SYMLINK_NOFOLLOW = Fcntl.AT_SYMLINK_NOFOLLOW.intValue();
    static final int AT_REMOVEDIR = Fcntl.AT_REMOVEDIR.intValue();

}