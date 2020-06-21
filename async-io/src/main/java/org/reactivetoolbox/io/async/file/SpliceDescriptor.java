package org.reactivetoolbox.io.async.file;

import org.reactivetoolbox.io.async.common.OffsetT;
import org.reactivetoolbox.io.async.common.SizeT;

import java.util.EnumSet;

/**
 * Container for all necessary details of the SPLICE operation.
 * <p>
 * This operation performs copying from one file descriptor to another completely at kernel space without involving any user space code or memory.
 */
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

    @Override
    public String toString() {
        return "SpliceDescriptor(" +
               "from: " + from +
               ", to: " + to +
               ", fromOffset: " + fromOffset +
               ", toOffset: " + toOffset +
               ", toCopy: " + toCopy +
               ", flags: " + flags +
               ')';
    }

    /**
     * Create new builder for assembling complete {@link SpliceDescriptor} instance.
     */
    public static SpliceDescriptorBuilder builder() {
        return from -> to -> fromOffset -> toOffset -> toCopy -> flags ->
                () -> new SpliceDescriptor(from, to, fromOffset, toOffset, toCopy, flags);
    }

    public interface SpliceDescriptorBuilder {
        Stage1 from(final FileDescriptor from);

        interface Stage1 {
            Stage2 to(final FileDescriptor to);
        }

        interface Stage2 {
            Stage3 fromOffset(final OffsetT fromOffset);
        }

        interface Stage3 {
            Stage4 toOffset(final OffsetT toOffset);
        }

        interface Stage4 {
            Stage5 toCopy(final SizeT toCopy);
        }

        interface Stage5 {
            Stage6 flags(final EnumSet<SpliceFlags> flags);
        }

        interface Stage6 {
            SpliceDescriptor build();
        }
    }
}
