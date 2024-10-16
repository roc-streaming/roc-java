#include "interface_config.h"
#include "common.h"

int interface_config_unmarshal(JNIEnv* env, roc_interface_config* config, jobject jconfig) {
    jclass interfaceConfigClass = NULL;
    jstring jOutgoingAddress = NULL;
    const char* outgoingAddress = NULL;
    jstring jMulticastGroup = NULL;
    const char* multicastGroup = NULL;
    int err = 0;

    interfaceConfigClass = (*env)->FindClass(env, INTERFACE_CONFIG_CLASS);
    assert(interfaceConfigClass != NULL);

    // set all fields to zero
    assert(config != NULL);
    memset(config, 0, sizeof(*config));

    // outgoing_address
    jOutgoingAddress = (jstring) get_object_field(
        env, interfaceConfigClass, jconfig, "outgoingAddress", "Ljava/lang/String;");
    if (jOutgoingAddress != NULL) {
        outgoingAddress = (*env)->GetStringUTFChars(env, jOutgoingAddress, 0);
        if (!outgoingAddress || strlen(outgoingAddress) >= sizeof(config->outgoing_address)) {
            err = -1;
            goto out;
        }
        strcpy(config->outgoing_address, outgoingAddress);
    }

    // multicast_group
    jMulticastGroup = (jstring) get_object_field(
        env, interfaceConfigClass, jconfig, "multicastGroup", "Ljava/lang/String;");
    if (jMulticastGroup != NULL) {
        multicastGroup = (*env)->GetStringUTFChars(env, jMulticastGroup, 0);
        if (!multicastGroup || strlen(multicastGroup) >= sizeof(config->multicast_group)) {
            err = -1;
            goto out;
        }
        strcpy(config->multicast_group, multicastGroup);
    }

    // reuse_address
    config->reuse_address
        = get_boolean_field_value(env, interfaceConfigClass, jconfig, "reuseAddress", &err);
    if (err) goto out;

out:
    if (outgoingAddress != NULL)
        (*env)->ReleaseStringUTFChars(env, jOutgoingAddress, outgoingAddress);
    if (multicastGroup != NULL) (*env)->ReleaseStringUTFChars(env, jMulticastGroup, multicastGroup);

    return err;
}
