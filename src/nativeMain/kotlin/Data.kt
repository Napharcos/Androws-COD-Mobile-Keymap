@file:OptIn(ExperimentalForeignApi::class)

import kotlinx.cinterop.ExperimentalForeignApi
import platform.windows.EnableWindow
import platform.windows.FALSE
import platform.windows.HWND
import platform.windows.SetWindowTextW
import platform.windows.TRUE

val mpKeys = mutableListOf(
    KeyData(hip_fire, 3000, replaceFire, loadMpKey(replaceFire)?.toBooleanStrictOrNull() ?: true),
    KeyData(lockMouseKey, lock_mouse, loadMpKey(lockMouseKey) ?: "Ctrl", loadMpKeyCode(lockMouseKey) ?: 17, 3001),
    KeyData(tabKey, performance_panel, loadMpKey(tabKey) ?: "Tab", loadMpKeyCode(tabKey) ?: 9, 3002),
    KeyData(escKey, menu, loadMpKey(escKey) ?: "Esc", loadMpKeyCode(escKey) ?: 27,3003),
    KeyData(spaceKey, jump, loadMpKey(spaceKey) ?: "Space", loadMpKeyCode(spaceKey) ?: 32, 3004),
    KeyData(key1, key1, loadMpKey(key1) ?: "1", loadMpKeyCode(key1) ?: 49, 3005),
    KeyData(key2, key2, loadMpKey(key2) ?: "2", loadMpKeyCode(key2) ?: 50, 3006),
    KeyData(key3, key3, loadMpKey(key3) ?: "3", loadMpKeyCode(key3) ?: 51, 3007),
    KeyData(key4, key4, loadMpKey(key4) ?: "4", loadMpKeyCode(key4) ?: 52, 3008),
    KeyData(key5, key5, loadMpKey(key5) ?: "5", loadMpKeyCode(key5) ?: 53, 3009),
    KeyData(key6, key6, loadMpKey(key6) ?: "6", loadMpKeyCode(key6) ?: 54, 3010),
    KeyData(key7, key7, loadMpKey(key7) ?: "7", loadMpKeyCode(key7) ?: 55, 3011),
    KeyData(bKey, keyb, loadMpKey(bKey) ?: "B", loadMpKeyCode(bKey) ?: 66, 3012),
    KeyData(cKey, keyc, loadMpKey(cKey) ?: "C", loadMpKeyCode(cKey) ?: 67, 3013),
    KeyData(eKey, keye, loadMpKey(eKey) ?: "E", loadMpKeyCode(eKey) ?: 69, 3014),
    KeyData(fKey, keyf, loadMpKey(fKey) ?: "F", loadMpKeyCode(fKey) ?: 70, 3015),
    KeyData(gKey, keyg, loadMpKey(gKey) ?: "G", loadMpKeyCode(gKey) ?: 71, 3016),
    KeyData(hKey, keyh, loadMpKey(hKey) ?: "H", loadMpKeyCode(hKey) ?: 72, 3017),
    KeyData(mKey, keym, loadMpKey(mKey) ?: "M", loadMpKeyCode(mKey) ?: 77, 3018),
    KeyData(qKey, keyq, loadMpKey(qKey) ?: "Q", loadMpKeyCode(qKey) ?: 81, 3019),
    KeyData(rKey, keyr, loadMpKey(rKey) ?: "R", loadMpKeyCode(rKey) ?: 82, 3020),
    KeyData(vKey, keyv, loadMpKey(vKey) ?: "V", loadMpKeyCode(vKey) ?: 86, 3021),
    KeyData(yKey, keyy, loadMpKey(yKey) ?: "Z", loadMpKeyCode(yKey) ?: 90, 3022),
    KeyData(zKey, keyz, loadMpKey(zKey) ?: "Y", loadMpKeyCode(zKey) ?: 89, 3023),
    KeyData(f4Key, keyf4, loadMpKey(f4Key) ?: "F4", loadMpKeyCode(f4Key) ?: 115, 3024),
    KeyData(oKey, keyo, loadMpKey(oKey) ?: "=", loadMpKeyCode(oKey) ?: 187, 3025),
    KeyData(xKey, keyx, loadMpKey(xKey) ?: "X", loadMpKeyCode(xKey) ?: 88, 3026, true, loadWheel(MP, xKey), WheelUiData(3027, 3028)),
    KeyData(tKey, keyt, loadMpKey(tKey) ?: "T", loadMpKeyCode(tKey) ?: 84, 3029),
    KeyData(f2Key, br_keyf2, loadMpKey(f2Key) ?: "F2", loadMpKeyCode(f2Key) ?: 113, 3030),
    KeyData(f3Key, br_keyf3, loadMpKey(f3Key) ?: "F3", loadMpKeyCode(f3Key) ?: 114, 3031)
)

val brKeys = mutableListOf(
    KeyData(hip_fire_br, 3000, replaceFire, loadBrKey(replaceFire)?.toBooleanStrictOrNull() ?: true),
    KeyData(separate_armor, 3001, separateArmor, loadBrKey(separateArmor)?.toBooleanStrictOrNull() ?: true),
    KeyData(separate_buy_station, 3002, separateBuyStation, loadBrKey(separateBuyStation)?.toBooleanStrictOrNull() ?: false),
    KeyData(tabKey, br_tabkey, loadBrKey(tabKey) ?: "Tab", loadBrKeyCode(tabKey) ?: 9, 3003),
    KeyData(shiftKey, br_shiftkey, loadBrKey(shiftKey) ?: "Shift", loadBrKeyCode(shiftKey) ?: 16, 3004),
    KeyData(escKey, br_esckey, loadBrKey(escKey) ?: "Escape", loadBrKeyCode(escKey) ?: 27, 3005),
    KeyData(spaceKey, br_spacekey, loadBrKey(spaceKey) ?: "Space", loadBrKeyCode(spaceKey) ?: 32, 3006),
    KeyData(key1, br_key1, loadBrKey(key1) ?: "1", loadBrKeyCode(key1) ?: 49, 3007),
    KeyData(key2, br_key2, loadBrKey(key2) ?: "2", loadBrKeyCode(key2) ?: 50, 3008),
    KeyData(bKey, br_keyb, loadBrKey(bKey) ?: "B", loadBrKeyCode(bKey) ?: 66, 3009),
    KeyData(cKey, br_keyc, loadBrKey(cKey) ?: "C", loadBrKeyCode(cKey) ?: 67, 3010),
    KeyData(eKey, br_keye, loadBrKey(eKey) ?: "E", loadBrKeyCode(eKey) ?: 69, 3011),
    KeyData(fKey, br_keyf, loadBrKey(fKey) ?: "F", loadBrKeyCode(fKey) ?: 70, 3012),
    KeyData(hKey, br_keyh, loadBrKey(hKey) ?: "H", loadBrKeyCode(hKey) ?: 72, 3013),
    KeyData(mKey, br_keym, loadBrKey(mKey) ?: "M", loadBrKeyCode(mKey) ?: 77, 3014),
    KeyData(qKey, br_keyq, loadBrKey(qKey) ?: "Q", loadBrKeyCode(qKey) ?: 81, 3015),
    KeyData(rKey, br_keyr, loadBrKey(rKey) ?: "R", loadBrKeyCode(rKey) ?: 82, 3016),
    KeyData(vKey, br_keyv, loadBrKey(vKey) ?: "V", loadBrKeyCode(vKey) ?: 86, 3017),
    KeyData(yKey, br_keyy, loadBrKey(yKey) ?: "Z", loadBrKeyCode(yKey) ?: 90, 3018),
    KeyData(zKey, br_keyz,  loadBrKey(zKey) ?: "Y", loadBrKeyCode(zKey) ?: 89, 3019),
    KeyData(f4Key, br_keyf4, loadBrKey(f4Key) ?: "F4", loadBrKeyCode(f4Key) ?: 115, 3020),
    KeyData(oKey, br_keyo, loadBrKey(oKey) ?: "=", loadBrKeyCode(oKey) ?: 187, 3021),
    KeyData(lockMouseKey, br_lock_mouse, loadBrKey(lockMouseKey) ?: "Control", loadBrKeyCode(lockMouseKey) ?: 17, 3022),
    KeyData(altKey, br_alt_key, loadBrKey(altKey) ?: "Alt", loadBrKeyCode(altKey) ?: 18, 3023),
    KeyData(xKey, br_keyx, loadBrKey(xKey) ?: "X", loadBrKeyCode(xKey) ?: 88, 3024, true, loadWheel(BR, xKey), WheelUiData(3025, 3026)),
    KeyData(tKey, br_keyt, loadBrKey(tKey) ?: "T", loadBrKeyCode(tKey) ?: 84, 3027),
    KeyData(key5, br_key5, loadBrKey(key5) ?: "5", loadBrKeyCode(key5) ?: 53, 3028),
    KeyData(key6, br_key6, loadBrKey(key6) ?: "6", loadBrKeyCode(key6) ?: 54, 3029),
    KeyData(key7, br_key7, loadBrKey(key7) ?: "7", loadBrKeyCode(key7) ?: 55, 3030),
    KeyData(key8, br_key8, loadBrKey(key8) ?: "8", loadBrKeyCode(key8) ?: 56, 3031),
    KeyData(gKey, br_keyg, loadBrKey(gKey) ?: "G", loadBrKeyCode(gKey) ?: 71, 3032),
    KeyData(capsKey, br_keycaps, loadBrKey(capsKey) ?: "Caps", loadBrKeyCode(capsKey) ?: 20, 3033),
    KeyData(key3, br_key3, loadBrKey(key3) ?: "3", loadBrKeyCode(key3) ?: 51, 3034),
    KeyData(f2Key, br_keyf2, loadBrKey(f2Key) ?: "F2", loadBrKeyCode(f2Key) ?: 113, 3035),
    KeyData(f3Key, br_keyf3, loadBrKey(f3Key) ?: "F3", loadBrKeyCode(f3Key) ?: 114, 3036),
    KeyData(key4, br_key4, loadBrKey(key4) ?: "4", loadBrKeyCode(key4) ?: 52, 3037),
    KeyData(nKey, br_keyn, loadBrKey(nKey) ?: "N", loadBrKeyCode(nKey) ?: 78, 3038)
)

val dmzKeys = mutableListOf(
    KeyData(separate_buy_station, 3000, separateBuyStation, loadDmzKey(separateBuyStation)?.toBooleanStrictOrNull() ?: true),
    KeyData(tabKey, dmz_tabkey, loadDmzKey(tabKey) ?: "TAB", loadDmzKeyCode(tabKey) ?: 9, 3001),
    KeyData(shiftKey, dmz_shiftkey, loadDmzKey(shiftKey) ?: "Shift", loadDmzKeyCode(shiftKey) ?: 16, 3002),
    KeyData(yKey, dmz_keyy, loadDmzKey(yKey) ?: "Z", loadDmzKeyCode(yKey) ?: 90, 3003),
    KeyData(spaceKey, dmz_spacekey, loadDmzKey(spaceKey) ?: "SPACE", loadDmzKeyCode(spaceKey) ?: 32, 3004),
    KeyData(key1, dmz_key1, loadDmzKey(key1) ?: "1", loadDmzKeyCode(key1) ?: 49, 3005),
    KeyData(key2, dmz_key2, loadDmzKey(key2) ?: "2", loadDmzKeyCode(key2) ?: 50, 3006),
    KeyData(bKey, dmz_keyb, loadDmzKey(bKey) ?: "B", loadDmzKeyCode(bKey) ?: 66, 3007),
    KeyData(cKey, dmz_keyc, loadDmzKey(cKey) ?: "C", loadDmzKeyCode(cKey) ?: 67, 3008),
    KeyData(eKey, dmz_keye, loadDmzKey(eKey) ?: "E", loadDmzKeyCode(eKey) ?: 69, 3009),
    KeyData(fKey, dmz_keyf, loadDmzKey(fKey) ?: "F", loadDmzKeyCode(fKey) ?: 70, 3010),
    KeyData(hKey, dmz_keyh, loadDmzKey(hKey) ?: "H", loadDmzKeyCode(hKey) ?: 72, 3011, true, loadWheel(DMZ, hKey), WheelUiData(3012, 3013)),
    KeyData(jKey, dmz_keyj, loadDmzKey(jKey) ?: "J", loadDmzKeyCode(jKey) ?: 74, 3014),
    KeyData(gKey, dmz_keyg, loadDmzKey(gKey) ?: "G", loadDmzKeyCode(gKey) ?: 71, 3015),
    KeyData(qKey, dmz_keyq, loadDmzKey(qKey) ?: "Q", loadDmzKeyCode(qKey) ?: 81, 3016),
    KeyData(escKey, dmz_esckey, loadDmzKey(escKey) ?: "ESC", loadDmzKeyCode(escKey) ?: 27, 3017),
    KeyData(mKey, dmz_keym, loadDmzKey(mKey) ?: "M", loadDmzKeyCode(mKey) ?: 77, 3018),
    KeyData(rKey, dmz_keyr, loadDmzKey(rKey) ?: "R", loadDmzKeyCode(rKey) ?: 82, 3019),
    KeyData(f4Key, dmz_keyf4, loadDmzKey(f4Key) ?: "F4", loadDmzKeyCode(f4Key) ?: 115, 3020),
    KeyData(oKey, dmz_keyo, loadDmzKey(oKey) ?: "O", loadDmzKeyCode(oKey) ?: 187, 3021),
    KeyData(xKey, dmz_keyx, loadDmzKey(xKey) ?: "X", loadDmzKeyCode(xKey) ?: 88, 3022 , true, loadWheel(DMZ, xKey), WheelUiData(3023, 3024)),
    KeyData(key3, dmz_key3, loadDmzKey(key3) ?: "3", loadDmzKeyCode(key3) ?: 51, 3025, true, loadWheel(DMZ, key3), WheelUiData(3026, 3027)),
    KeyData(vKey, dmz_keyv, loadDmzKey(vKey) ?: "V", loadDmzKeyCode(vKey) ?: 86, 3028),
    KeyData(tKey, dmz_keyt, loadDmzKey(tKey) ?: "T", loadDmzKeyCode(tKey) ?: 84, 3029),
    KeyData(lockMouseKey, br_lock_mouse,loadDmzKey(lockMouseKey) ?: "CTRL", loadDmzKeyCode(lockMouseKey) ?: 17, 3030),
    KeyData(zKey, dmz_keyz, loadDmzKey(zKey) ?: "Y", loadDmzKeyCode(zKey) ?: 89, 3031),
    KeyData(nKey, dmz_keyn, loadDmzKey(nKey) ?: "N", loadDmzKeyCode(nKey) ?: 78, 3032)
)

val keyMaps by lazy {
    mutableMapOf(
        4001 to KeyMaps("Multi Player", MP, mpKeys),
        4002 to KeyMaps("Battle Royale", BR, brKeys),
        4003 to KeyMaps("DMZ", DMZ, dmzKeys),
    )
}

fun loadMpKeyCode(id: String) = reg?.let { getValue(it, MP + id + code) }?.toInt()

fun loadBrKeyCode(id: String) = reg?.let { getValue(it, BR + id + code) }?.toInt()

fun loadDmzKeyCode(id: String) = reg?.let { getValue(it, DMZ + id + code) }?.toInt()

fun loadMpKey(id: String) = reg?.let { getValue(it, MP + id) }

fun loadBrKey(id: String) = reg?.let { getValue(it, BR + id) }

fun loadDmzKey(id: String) = reg?.let { getValue(it, DMZ + id) }

fun loadWheel(mod: String, id: String) = reg?.let { getValue(it, "${mod}_${id}_wheel") }?.toBoolean() ?: true

fun applyKeymap() {
    reinitMP(keyMaps[4001]?.keys ?: emptyList())
    reinitBR(keyMaps[4002]?.keys ?: emptyList())
    reinitDMZ(keyMaps[4003]?.keys ?: emptyList())
    ConsoleManager.stopEmulator()
    DatabaseManager.launchUpdateDb()
    ConsoleManager.startCODM()
}

fun updateKeyCode(id: String?, keyCode: Int) {
    val keyMap = keyMaps[selectedKeymap]
    val mode = keyMap?.mode
    val element = keyMap?.keys?.firstOrNull { it.id == id }

    if (element?.currentCode == keyCode) return

    removeKey(keyCode)

    element?.let {
        it.currentCode = keyCode

        reg?.let { r -> setValue(r, mode + id + code, keyCode.toString()) }
    }

    updateSubmitButtonState(nextButton)
}

fun updateKey(id: String?, key: String) {
    val keyMap = keyMaps[selectedKeymap]
    val mode = keyMap?.mode
    val element = keyMap?.keys?.firstOrNull { it.id == id }

    element?.let {
        it.currentKey = key

        reg?.let { r -> setValue(r, mode + id, key) }
    }
}

fun removeKey(keyCode: Int) {
    val keymap = keyMaps[selectedKeymap]
    val mode = keymap?.mode
    val filterElement = keymap?.keys?.firstOrNull { it.currentCode == keyCode }

    filterElement?.let {
        it.currentCode = -1
        it.currentKey = ""

        SetWindowTextW(it.hwnd, "")


        reg?.let { r ->
            setValue(r, mode + filterElement.id + code, (-1).toString())
            setValue(r, mode + filterElement.id, "")
        }
    }
}

fun updateSubmitButtonState(button: HWND?) {
    var enabled = true

    for ((_, v) in keyMaps) {
        if (!enabled) break
        enabled = v.keys.filter { !it.fakeKey }.all { it.currentCode!! >= 0 }
    }

    EnableWindow(button, if (enabled) TRUE else FALSE)
}

var selectedKeymap = 4001

data class KeyMaps(
    val name: String,
    val mode: String,
    val keys: MutableList<KeyData> = mutableListOf(),
    var hwnd: HWND? = null,
)

data class KeyData(
    val id: String,
    val text: String,
    var currentKey: String? = null,
    var currentCode: Int? = null,
    val hwndId: Int,
    val wheelAvailable: Boolean = false,
    var applyWheel: Boolean = true,
    val wheelUiData: WheelUiData? = null,
    var hwndText: HWND? = null,
    var hwnd: HWND? = null,
    val fakeKey: Boolean = false,
    var currentValue: Boolean? = null
) {
    constructor(
        text: String,
        hwndId: Int,
        id: String,
        currentValue: Boolean
    ) : this(
        id = id,
        text = text,
        fakeKey = true,
        hwndId = hwndId,
        currentValue = currentValue
    )
}

data class WheelUiData(
    val radio1Id: Int,
    val radio2Id: Int,
    var radio1Hwnd: HWND? = null,
    var radio2Hwnd: HWND? = null
)