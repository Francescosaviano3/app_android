package com.worldgn.connector;

/**
 * Created by Krishna Rao on 11/09/2017.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

class ConnectorUUIDs {

    // Service UUIDs
    private static final Map<String, String> sCHARACTERISTIC_CONFIG;
    static {
        Map<String, String> aMap = new HashMap<>();

        aMap.put("00002902-0000-1000-8000-00805f9b34fb".toUpperCase(), "CHARACTERISTIC_CONFIG_00002902");

        sCHARACTERISTIC_CONFIG = Collections.unmodifiableMap(aMap);
    }

    // Service UUIDs
    private static final Map<String, String> sServiceUUIDs;
    static {
        Map<String, String> aMap = new HashMap<>();

        aMap.put("0AABCDEF-1111-2222-0000-FACEBEADAAAA".toUpperCase(), "Service_0AABCDEF");
        aMap.put("1AABCDEF-1111-2222-0000-FACEBEADAAAA".toUpperCase(), "Service_1AABCDEF");
        aMap.put("2AABCDEF-1111-2222-0000-FACEBEADAAAA".toUpperCase(), "Service_2AABCDEF");
        aMap.put("ECA95120-F940-11E4-9ED0-0002A5D5C51B".toUpperCase(), "Service_ECA95120");

        sServiceUUIDs = Collections.unmodifiableMap(aMap);
    }

    public static List<UUID> getConnectorServicesToDiscover(){
        List<UUID> services=new ArrayList<UUID>();
        services.add(UUID.fromString("0AABCDEF-1111-2222-0000-FACEBEADAAAA"));
        services.add(UUID.fromString("1AABCDEF-1111-2222-0000-FACEBEADAAAA"));
        services.add(UUID.fromString("2AABCDEF-1111-2222-0000-FACEBEADAAAA"));
        services.add(UUID.fromString("ECA95120-F940-11E4-9ED0-0002A5D5C51B"));
        return services;
    }
    public static List<UUID> getConnectorCharactersticsToDiscover(){
        List<UUID> characterstics=new ArrayList<UUID>();
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0001-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0002-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0003-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0004-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0005-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0010-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0020-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0100-facebeadaaaa"));
        characterstics.add(UUID.fromString("facebead-ffff-eeee-0200-facebeadaaaa"));
        characterstics.add(UUID.fromString("c1c8a4a0-f941-11e4-a534-0002a5d5c51b"));
        return characterstics;
    }

    // Characteristic UUIDs
    private static final Map<String, String> sCharacteristicUUIDs;
    static {
        Map<String, String> aMap = new HashMap<>();

        // HELOLX
        aMap.put("facebead-ffff-eeee-0001-facebeadaaaa".toUpperCase(), "CharacteristicUUID_01");
        aMap.put("facebead-ffff-eeee-0002-facebeadaaaa".toUpperCase(), "CharacteristicUUID_02");
        aMap.put("facebead-ffff-eeee-0003-facebeadaaaa".toUpperCase(), "CharacteristicUUID_03");
        aMap.put("facebead-ffff-eeee-0004-facebeadaaaa".toUpperCase(), "CharacteristicUUID_04");
        aMap.put("facebead-ffff-eeee-0005-facebeadaaaa".toUpperCase(), "CharacteristicUUID_05");
        aMap.put("facebead-ffff-eeee-0010-facebeadaaaa".toUpperCase(), "CharacteristicUUID_10");
        aMap.put("facebead-ffff-eeee-0020-facebeadaaaa".toUpperCase(), "CharacteristicUUID_20");
        aMap.put("facebead-ffff-eeee-0100-facebeadaaaa".toUpperCase(), "CharacteristicUUID_100");
        aMap.put("facebead-ffff-eeee-0200-facebeadaaaa".toUpperCase(), "CharacteristicUUID_200");
        aMap.put("c1c8a4a0-f941-11e4-a534-0002a5d5c51b".toUpperCase(), "CharacteristicUUID_a534");

        sCharacteristicUUIDs = Collections.unmodifiableMap(aMap);
    }

    // Descriptors UUIDs
    private static final Map<String, String> sDescriptorUUIDs;
    static {
        Map<String, String> aMap = new HashMap<>();

        sDescriptorUUIDs = Collections.unmodifiableMap(aMap);
    }


    // Public Getters
    public static String getConfigName(String uuid) {
        String result;

        uuid = uuid.toUpperCase();  // To avoid problems with lowercase/uppercase
        result = sCHARACTERISTIC_CONFIG.get(uuid);

        return result;
    }

    // Public Getters
    public static String getServiceName(String uuid) {
        String result;

        uuid = uuid.toUpperCase();  // To avoid problems with lowercase/uppercase
        result = sServiceUUIDs.get(uuid);

        return result;
    }

    public static String getCharacteristicName(String uuid) {
        String result;

        uuid = uuid.toUpperCase();  // To avoid problems with lowercase/uppercase
        result = sCharacteristicUUIDs.get(uuid);

        return result;
    }

    public static String getDescriptorName(String uuid) {
        String result;

        uuid = uuid.toUpperCase();  // To avoid problems with lowercase/uppercase
        result = sDescriptorUUIDs.get(uuid);

        return result;
    }
}
