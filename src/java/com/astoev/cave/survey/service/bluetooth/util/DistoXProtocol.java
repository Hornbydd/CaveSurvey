package com.astoev.cave.survey.service.bluetooth.util;

import android.util.Log;

import com.astoev.cave.survey.Constants;
import com.astoev.cave.survey.service.bluetooth.Measure;

import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by astoev on 7/18/15.
 * DistoX protocol implementation.
 * Adopted file from Rich Smith, see https://github.com/richsmith/sexytopo/blob/master/src/main/java/org/hwyl/sexytopo/comms/DistoXProtocol.java.
 */
public class DistoXProtocol {

    private static final int ADMIN = 0;
    private static final int DISTANCE_LOW_BYTE = 1;
    private static final int DISTANCE_HIGH_BYTE = 2;
    private static final int DECLINATION_LOW_BYTE = 3;
    private static final int DECLINATION_HIGH_BYTE = 4;
    private static final int INCLINATION_LOW_BYTE = 5;
    private static final int INCLINATION_HIGH_BYTE = 6;
    private static final int ROLL_ANGLE_HIGH_BYTE = 7;

    private static final int SEQUENCE_BIT_MASK = 0x80;
    private static final int ACKNOWLEDGEMENT_PACKET_BASE = 0x55;

    /**
     * An acknowledgement packet consists of a single byte; bits 0-7 are 1010101 and bit 7 is the
     * same as the sequence bit of the packet being acknowledged.
     *
     * @param dataPacket
     * @return
     */
    public static byte[] createAcknowledgementPacket(byte[] dataPacket) {
        byte sequenceBit = (byte)(dataPacket[ADMIN] & SEQUENCE_BIT_MASK);
        byte[] acknowledgePacket = new byte[1];
        acknowledgePacket[0] = (byte)(sequenceBit | ACKNOWLEDGEMENT_PACKET_BASE);
        return acknowledgePacket;
    }


    public static List<Measure> parseDataPacket(byte[] dataPacket) {

        Log.i(Constants.LOG_TAG_BT, "Decoding distoX : " + Base64.encodeBase64(dataPacket));

        List<Measure> measures = new ArrayList<Measure>();

        int d0 = (int)(dataPacket[ADMIN] & 0x40 );
        int d1  = (int)(dataPacket[DISTANCE_LOW_BYTE] & 0xff);
        if (d1 < 0) d1 += 256;
        int d2  = (int)(dataPacket[DISTANCE_HIGH_BYTE] & 0xff);
        if (d2 < 0) d2 += 256;
        // double d =  (((int)mBuffer[0]) & 0x40) * 1024.0 + (mBuffer[1] & 0xff) * 1.0 + (mBuffer[2] & 0xff) * 256.0;
        Double distance =  (d0 * 1024 + d2 * 256 + d1 * 1) / 1000.0; // in mm


        int b3 = (int)(dataPacket[DECLINATION_LOW_BYTE] & 0xff); if ( b3 < 0 ) b3 += 256;
        int b4 = (int)(dataPacket[DECLINATION_HIGH_BYTE] & 0xff); if ( b4 < 0 ) b4 += 256;
        // double b = (mBuffer[3] & 0xff) + (mBuffer[4] & 0xff) * 256.0;
        double b = b3 + b4 * 256.0;
        Double bearing  = b * 180.0 / 32768.0;

        int c5 = (int)(dataPacket[INCLINATION_LOW_BYTE] & 0xff); if ( c5 < 0 ) c5 += 256;
        int c6 = (int)(dataPacket[INCLINATION_HIGH_BYTE] & 0xff); if ( c6 < 0 ) c6 += 256;
        // double c = (mBuffer[5] & 0xff) + (mBuffer[6] & 0xff) * 256.0;
        double c = c5 + c6 * 256.0;
        Double inclination    = c * 90.0  / 16384.0; // 90/0x4000;
        if ( c >= 32768 ) { inclination = (65536 - c) * (-90.0) / 16384.0; }

        int r7 = (int)(dataPacket[7]/* & 0xff*/); if ( r7 < 0 ) r7 += 256;
        // double r = (mBuffer[7] & 0xff);
        double r = r7;
        double roll = r * 180.0 / 128.0;

//        Leg leg = new Leg(distance, bearing, inclination);


        Measure angleMeasure = new Measure(Constants.MeasureTypes.angle, Constants.MeasureUnits.degrees, bearing.floatValue());
        measures.add(angleMeasure);
        Log.i(Constants.LOG_TAG_BT, "Got angle " + angleMeasure);

        Measure slopeMeasure = new Measure(Constants.MeasureTypes.slope, Constants.MeasureUnits.degrees, inclination.floatValue());
        measures.add(slopeMeasure);
        Log.i(Constants.LOG_TAG_BT, "Got slope " + slopeMeasure);

        Measure distanceMeasure = new Measure(Constants.MeasureTypes.distance, Constants.MeasureUnits.meters, distance.floatValue());
        measures.add(distanceMeasure);
        Log.i(Constants.LOG_TAG_BT, "Got distance " + distanceMeasure);

        return measures;
    }

    public static String describeDataPacket(byte[] dataPacket) {
        String description = "[";
        for (int i = 0; i < dataPacket.length; i++) {
            if (i == ADMIN) {
                description += Integer.toBinaryString(dataPacket[i] & 0xFF);
            } else {
                description += ", " + dataPacket[i];
            }
        }
        description += "]";
        return description;
    }

    public static String describeAcknowledgementPacket(byte[] acknowledgementPacket) {
        return "[" + Integer.toBinaryString(acknowledgementPacket[0] & 0xFF) + "]";
    }

    public static boolean isDataPacket(byte[] dataPacket) {
        return ((dataPacket[ADMIN] & 0x3F) == 1);
    }
}
