#include "media_encoding.h"
#include "exceptions.h"
#include "helpers.h"
#include "package.h"

#include <assert.h>
#include <string.h>

bool media_encoding_unmarshal(JNIEnv* env, jobject jencoding, roc_media_encoding* result) {
    assert(env);
    assert(jencoding);
    assert(result);

    memset(result, 0, sizeof(*result));

    jclass jclass = find_class(env, MEDIA_ENCODING_CLASS);
    if (!jclass) {
        return false;
    }

    int enum_value = 0;

    // rate
    if (!read_uint_field(env, jclass, jencoding, MEDIA_ENCODING_CLASS, "rate", &result->rate)) {
        return false;
    }

    // format
    if (!read_enum_field(
            env, jclass, jencoding, MEDIA_ENCODING_CLASS, "format", FORMAT_CLASS, &enum_value)) {
        return false;
    }
    result->format = (roc_format) enum_value;

    // channels
    if (!read_enum_field(env, jclass, jencoding, MEDIA_ENCODING_CLASS, "channels",
            CHANNEL_LAYOUT_CLASS, &enum_value)) {
        return false;
    }
    result->channels = (roc_channel_layout) enum_value;

    // tracks
    if (!read_uint_field(env, jclass, jencoding, MEDIA_ENCODING_CLASS, "tracks", &result->tracks)) {
        return false;
    }

    return true;
}
