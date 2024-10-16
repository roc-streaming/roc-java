#include "media_encoding.h"
#include "channel_layout.h"
#include "common.h"
#include "format.h"

int media_encoding_unmarshal(JNIEnv* env, roc_media_encoding* encoding, jobject jencoding) {
    jclass mediaEncodingClass = NULL;
    jobject jobj = NULL;
    int err = 0;

    mediaEncodingClass = (*env)->FindClass(env, MEDIA_ENCODING_CLASS);
    assert(mediaEncodingClass != NULL);

    // set all fields to zeros
    assert(encoding != NULL);
    memset(encoding, 0, sizeof(*encoding));

    // rate
    encoding->rate = get_int_field_value(env, mediaEncodingClass, jencoding, "rate", &err);
    if (err) return err;

    // format
    jobj = get_object_field(env, mediaEncodingClass, jencoding, "format", "L" FORMAT_CLASS ";");
    if (jobj != NULL) encoding->format = get_format(env, jobj);

    // channels
    jobj = get_object_field(
        env, mediaEncodingClass, jencoding, "channels", "L" CHANNEL_LAYOUT_CLASS ";");
    if (jobj != NULL) encoding->channels = get_channel_layout(env, jobj);

    // tracks
    encoding->tracks = get_int_field_value(env, mediaEncodingClass, jencoding, "tracks", &err);
    if (err) return err;

    return 0;
}
