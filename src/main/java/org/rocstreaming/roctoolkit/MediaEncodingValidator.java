package org.rocstreaming.roctoolkit;

/**
 * A <code>MediaEncodingValidator</code> adds validation to MediaEncoding builder.
 */
class MediaEncodingValidator extends MediaEncoding.Builder {
    @Override
    public MediaEncoding build() {
        MediaEncoding encoding = super.build();
        Check.notNegative(encoding.getRate(), "rate");
        Check.notNull(encoding.getFormat(), "format");
        Check.notNull(encoding.getChannels(), "channels");
        Check.notNegative(encoding.getTracks(), "tracks");
        return encoding;
    }
}
