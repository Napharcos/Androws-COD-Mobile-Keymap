val UPDATE_SMK = """
    INSERT INTO configs (key, value, encrypt, json_data)
    VALUES ('androws.com.activision.callofduty.shooter.smk', '${SMK.trimIndent()}', 0, '') 
    ON CONFLICT(key) DO UPDATE SET value = excluded.value, encrypt = excluded.encrypt;
""".trimIndent()

val UPDATE_DEFAULT_KEYMAP = """
    INSERT INTO configs (key, value, encrypt, json_data)
    VALUES ('androws.com.activision.callofduty.shooter.default.key_mapping', '', 0, '') 
    ON CONFLICT(key) DO UPDATE SET value = excluded.value, encrypt = excluded.encrypt;
""".trimIndent()

const val CLEAR_KEYMAP_CONFIGS = """
DELETE FROM key_map_configs_cache_official
WHERE key LIKE '%activision%';
"""

val OFFICIAL_CONFIG2 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_config2.com.activision.callofduty.shooter', NULL, '${OFFICIAL_CONFIG2_VALUE1.trimIndent()}', '${OFFICIAL_CONFIG2_VALUE2.trimIndent()}', NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

val LAST_MODE = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('lastmodeid.unlogin.com.activision.callofduty.shooter', '13', '', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// MP - Config
val KEYMAP_13 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_mode.unlogin.com.activision.callofduty.shooter.13', '${KEYMAP_CONTENT_13.trimIndent()}', '', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

//MP - Default
val KEYMAP_13_28 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.13.28', '${KEYMAP_CONTENT_13_28.trimIndent()}', '223', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// MP - 16:10
var KEYMAP_13_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.13.1795311862400', '${KEYMAP_CONTENT_13_1795.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// MP - 16:9
var KEYMAP_13_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.13.1794448606514', '${KEYMAP_CONTENT_13_1794.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

fun reinitMP(keys: List<KeyData>) {
    KEYMAP_13_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.13.1794448606514', '${KEYMAP_CONTENT_13_1794.trimIndent().replaceKeys(MP, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

    KEYMAP_13_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.13.1795311862400', '${KEYMAP_CONTENT_13_1795.trimIndent().replaceKeys(MP, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()
}

// BR - Config
val KEYMAP_2220 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_mode.unlogin.com.activision.callofduty.shooter.2220', '${KEYMAP_CONTENT_2220.trimIndent()}', '', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - Default
val KEYMAP_2220_3609 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2220.3609', '${KEYMAP_CONTENT_2220_3609.trimIndent()}', '230', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - 16:9
var KEYMAP_2220_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.2220.1794450115798', '${KEYMAP_CONTENT_2220_1794.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - 16:10
var KEYMAP_2220_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2220.1795311842652', '${KEYMAP_CONTENT_2220_1795.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

fun reinitBR(keys: List<KeyData>) {
    KEYMAP_2220_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.2220.1794450115798', '${KEYMAP_CONTENT_2220_1794.trimIndent().replaceKeys(BR, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

    KEYMAP_2220_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2220.1795311842652', '${KEYMAP_CONTENT_2220_1795.trimIndent().replaceKeys(BR, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()
}

// DMZ - Config
val KEYMAP_2221 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_mode.unlogin.com.activision.callofduty.shooter.2221', '${KEYMAP_CONTENT_2221.trimIndent()}', '', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - Default
val KEYMAP_2221_3610 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2221.3610', '${KEYMAP_CONTENT_2221_3610.trimIndent()}', '222', NULL, NULL, NULL) 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - 16:9
var KEYMAP_2221_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.2221.1794492819158', '${KEYMAP_CONTENT_2221_1794.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

// BR - 16:10
var KEYMAP_2221_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2221.1795311871905', '${KEYMAP_CONTENT_2221_1795.trimIndent()}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

fun reinitDMZ(keys: List<KeyData>) {
    KEYMAP_2221_1794 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('keymap_data.unlogin.com.activision.callofduty.shooter.2221.1794492819158', '${KEYMAP_CONTENT_2220_1794.trimIndent().replaceKeys(DMZ, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()

    KEYMAP_2221_1795 = """
    INSERT INTO key_map_configs_cache_official (key, value0, value1, value2, value3, value4)
    VALUES ('offcial_keymap_data2.com.activision.callofduty.shooter.2221.1795311871905', '${KEYMAP_CONTENT_2220_1795.trimIndent().replaceKeys(DMZ, keys)}', '', '', '', '') 
    ON CONFLICT(key) DO UPDATE SET value0 = excluded.value0;
""".trimIndent()
}