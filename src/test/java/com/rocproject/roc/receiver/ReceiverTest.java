package com.rocproject.roc.receiver;

import com.rocproject.roc.address.Address;
import com.rocproject.roc.address.Family;
import com.rocproject.roc.config.*;
import com.rocproject.roc.context.Context;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReceiverTest {
    static {
        System.loadLibrary("native");
    }

    private ReceiverConfig config;

    ReceiverTest() {
        this.config = new ReceiverConfig.Builder(44100,
                                            ChannelSet.ROC_CHANNEL_SET_STEREO,
                                            FrameEncoding.ROC_FRAME_ENCODING_PCM_FLOAT)
                                        .build();
    }

    @Test
    public void TestValidReceiverCreationAndDeinitialization() {
        assertDoesNotThrow(() -> {
            try (
                    Context context = new Context();
                    Receiver receiver = new Receiver(context, config);
            ) {}
        });
    }

    @Test
    public void TestInvalidReceiverCreation() {
        assertThrows(IllegalArgumentException.class, () -> new Receiver(null, config));
        assertThrows(IllegalArgumentException.class, () -> {
            try (Context context = new Context()) { new Receiver(context, null); }
        });
    }

    @Test
    public void TestValidReceiverBind() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            assertDoesNotThrow(() -> receiver.bind(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10001)));
            assertDoesNotThrow(() -> receiver.bind(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10002)));
        }
    }

    @Test
    public void TestReceiverBindEphemeralPort() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            Address sourceAddress = new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0);
            Address repairAddress = new Address(Family.ROC_AF_AUTO, "0.0.0.0", 0);
            receiver.bind(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP_RS8M_SOURCE, sourceAddress);
            receiver.bind(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, repairAddress);
            assertNotEquals(0, sourceAddress.getPort());
            assertNotEquals(0, repairAddress.getPort());
        }
    }

    @Test
    public void TestInvalidReceiverBind() throws Exception {
        try (
                    Context context = new Context();
                    Receiver receiver = new Receiver(context, config);
        ) {
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(null, Protocol.ROC_PROTO_RTP, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10001)));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(PortType.ROC_PORT_AUDIO_SOURCE, null, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10001)));
            assertThrows(IllegalArgumentException.class, () -> receiver.bind(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP, null));
        }
    }

    @Test
    public void TestInvalidReadFloatArray() throws Exception {
        try (
                Context context = new Context();
                Receiver receiver = new Receiver(context, config);
        ) {
            receiver.bind(PortType.ROC_PORT_AUDIO_SOURCE, Protocol.ROC_PROTO_RTP, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10001));
            receiver.bind(PortType.ROC_PORT_AUDIO_REPAIR, Protocol.ROC_PROTO_RS8M_REPAIR, new Address(Family.ROC_AF_AUTO, "0.0.0.0", 10002));
            assertThrows(IllegalArgumentException.class, () -> receiver.read(null));
        }
    }
}
