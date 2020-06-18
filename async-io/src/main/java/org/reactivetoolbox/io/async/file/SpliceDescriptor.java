package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;

import java.util.EnumSet;

/**
 * Container for all necessary details of the SPLICE operation.
 * This operation performs copying from one file descriptor to another completely
 * at kernel space without involving any user space code or memory.
 */
//TODO: toString
public class SpliceDescriptor {
    private final FileDescriptor from;
    private final FileDescriptor to;
    private final OffsetT fromOffset;
    private final OffsetT toOffset;
    private final SizeT toCopy;
    private final EnumSet<SpliceFlags> flags;

    private SpliceDescriptor(final FileDescriptor from,
                             final FileDescriptor to,
                             final OffsetT fromOffset,
                             final OffsetT toOffset,
                             final SizeT toCopy,
                             final EnumSet<SpliceFlags> flags) {
        this.from = from;
        this.to = to;
        this.fromOffset = fromOffset;
        this.toOffset = toOffset;
        this.toCopy = toCopy;
        this.flags = flags;
    }

    /**
     * Source file descriptor.
     */
    public FileDescriptor from() {
        return from;
    }

    /**
     * Destination file descriptor.
     */
    public FileDescriptor to() {
        return to;
    }

    /**
     * Offset in the source file. Must be 0 if source file descriptor is a socket.
     */
    public OffsetT fromOffset() {
        return fromOffset;
    }

    /**
     * Offset in the destination file. Must be 0 if destination file descriptor is a socket.
     */
    public OffsetT toOffset() {
        return toOffset;
    }

    /**
     * Number of bytes to copy.
     */
    public SizeT toCopy() {
        return toCopy;
    }

    /**
     * Operation flags.
     */
    public EnumSet<SpliceFlags> flags() {
        return flags;
    }

    public static SpliceDescriptorBuilder0 builder() {
        return new SpliceDescriptorBuilder0();
    }

    private static class SpliceDescriptorBuilder0 {
        private final MutableSpliceDescriptor descriptor = new MutableSpliceDescriptor();

        public SpliceDescriptorBuilder1 from(final FileDescriptor from) {
            descriptor.from(from);
            return to -> {
                descriptor.to(to);
                return (SpliceDescriptorBuilder2) fromOffset -> {
                    descriptor.fromOffset(fromOffset);
                    return (SpliceDescriptorBuilder3) toOffset -> {
                        descriptor.toOffset(toOffset);
                        return (SpliceDescriptorBuilder4) toCopy -> {
                            descriptor.toCopy(toCopy);
                            return (SpliceDescriptorBuilder5) flags -> descriptor.flags(flags).toImmutable();
                        };
                    };
                };
            };
        }

        public interface SpliceDescriptorBuilder1 {
            SpliceDescriptorBuilder2 to(final FileDescriptor to);
        }

        public interface SpliceDescriptorBuilder2 {
            SpliceDescriptorBuilder3 fromOffset(final OffsetT fromOffset);
        }

        public interface SpliceDescriptorBuilder3 {
            SpliceDescriptorBuilder4 toOffset(final OffsetT toOffset);
        }

        public interface SpliceDescriptorBuilder4 {
            SpliceDescriptorBuilder5 toCopy(final SizeT toCopy);
        }

        public interface SpliceDescriptorBuilder5 {
            SpliceDescriptor flags(final EnumSet<SpliceFlags> flags);
        }
    }

    private static class MutableSpliceDescriptor {
        private FileDescriptor from;
        private FileDescriptor to;
        private OffsetT fromOffset;
        private OffsetT toOffset;
        private SizeT toCopy;
        private EnumSet<SpliceFlags> flags;

        public MutableSpliceDescriptor from(final FileDescriptor from) {
            this.from = from;
            return this;
        }

        public MutableSpliceDescriptor to(final FileDescriptor to) {
            this.to = to;
            return this;
        }

        public MutableSpliceDescriptor fromOffset(final OffsetT fromOffset) {
            this.fromOffset = fromOffset;
            return this;
        }

        public MutableSpliceDescriptor toOffset(final OffsetT toOffset) {
            this.toOffset = toOffset;
            return this;
        }

        public MutableSpliceDescriptor toCopy(final SizeT toCopy) {
            this.toCopy = toCopy;
            return this;
        }

        public MutableSpliceDescriptor flags(final EnumSet<SpliceFlags> flags) {
            this.flags = flags;
            return this;
        }

        public SpliceDescriptor toImmutable() {
            return new SpliceDescriptor(from, to, fromOffset, toOffset, toCopy, flags);
        }
    }
}
