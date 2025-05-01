package org.rocstreaming.roctoolkit;

/**
 * Adds validation to {@link MediaEncoding} builder.
 */
class MediaEncodingValidator extends MediaEncoding.Builder {
    @Override
    public MediaEncoding build() {
        MediaEncoding encoding = super.build();

        Check.notNegative(encoding.getRate(), "MediaEncoding.rate");
        Check.notNull(encoding.getFormat(), "MediaEncoding.format");
        Check.notNull(encoding.getChannels(), "MediaEncoding.channels");
        Check.notNegative(encoding.getTracks(), "MediaEncoding.tracks");

        if (encoding.getChannels() == ChannelLayout.MULTITRACK) {
            if (encoding.getTracks() < 1 || encoding.getTracks() > 1024) {
                throw new IllegalArgumentException("Invalid MediaEncoding: when 'channels' is MULTITRACK, 'tracks' must be in range [1; 1024]");
            }
        } else {
            if (encoding.getTracks() != 0) {
                throw new IllegalArgumentException("Invalid MediaEncoding: when 'channels' isn't MULTITRACK, 'tracks' must be zero");
            }
        }

        return encoding;
    }
}
