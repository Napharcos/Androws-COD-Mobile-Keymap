import hip_fire
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.nativeHeap
import kotlinx.cinterop.toKString
import platform.windows.CheckRadioButton
import platform.windows.GetUserDefaultLocaleName
import platform.windows.WCHARVar
import kotlin.to

const val next = "next"
const val apply = "apply"
const val back = "back"
const val skip = "skip"
const val error = "error"
const val welcome = "welcome"
const val cancel = "cancel"
const val confirmation = "confirmation"
const val cancel_text = "cancel_text"
const val text_1_1 = "text_1_1"
const val title_2 = "title_2"
const val lang = "lang"
const val accept = "accept"
const val lang_en = "lang_en"
const val lang_hu = "lang_hu"
const val hip_fire = "hip_fire"
const val hip_fire_br = "hip_fire_br"
const val separate_armor = "separate_armor"
const val separate_buy_station = "separate_buy_station"
const val simple_key = "simple_key"
const val rotary_key = "rotary_key"

val en = mapOf(
    next to "Next",
    apply to "Apply",
    back to "Back",
    skip to "Delete configuration",
    error to "Error",
    cancel to "Cancel",
    welcome to "Welcome!",
    confirmation to "Confirmation",
    cancel_text to "Are you sure you want to close the application?",
    text_1_1 to "Choose a language to get started:",
    title_2 to "Terms of use",
    accept to "Accept",
    lang_en to "English - English",
    lang_hu to "Hungarian - Magyar",

    hip_fire to "Enables hip fire for score streaks and operator skills, but disables the 1-tap ADS mode.",
    hip_fire_br to "Enables hip fire for annihilator, but disables the 1-tap ADS mode.",
    separate_armor to "Separate armor button",
    separate_buy_station to "Separate buy station button",

    simple_key to "Simple key",
    rotary_key to "Rotary key"
) + description_en

val hu = mapOf(
    next to "Következő",
    apply to "Alkalmaz",
    back to "Vissza",
    skip to "Konfiguráció törlése",
    cancel to "Mégse",
    error to "Hiba",
    welcome to "Üdvözöljük!",
    confirmation to "Megerősítés",
    cancel_text to "Biztosan be szeretnéd zárni az alkalmazást?",
    text_1_1 to "A kezdéshez válasszon nyelvet:",
    title_2 to "Felhasználói feltételek",
    accept to "Elfogadom",
    lang_en to "Angol - English",
    lang_hu to "Magyar - Magyar",

    hip_fire to "Engedélyezi a hip fire módot a score streak-ekhez és operator skill-ekhez, de letíltja az 1-tap ADS módot.",
    hip_fire_br to "Engedélyezi a  hip fire módot az annihilator-nak, de letíltja az 1-tap ADS módot.",
    separate_armor to "Külön páncél gomb",
    separate_buy_station to "Külön buy station gomb",

    simple_key to "Egyszerű gomb",
    rotary_key to "Forgó gomb"
) + description_hu

lateinit var strings: Map<String, String>

fun initLanguage() {
    val langId = when (strings) {
        en   -> EN_LANG_RADIO_ID
        hu   -> HU_LANG_RADIO_ID
        else -> EN_LANG_RADIO_ID
    }

    CheckRadioButton(hwndMain, 2001, 2002, langId)
}

@OptIn(ExperimentalForeignApi::class)
fun initStrings(): Map<String, String> {
    val language = reg?.let { getValue(it, lang) } ?: getLocalLanguage()

    return when (language) {
        "hu" -> hu
        else -> en
    }
}

@OptIn(ExperimentalForeignApi::class)
fun getLocalLanguage(): String {
    val bufferSize = 85
    val buffer = nativeHeap.allocArray<WCHARVar>(bufferSize)
    val length = GetUserDefaultLocaleName(buffer, bufferSize)

    return if (length > 0) {
        val langName = buffer.toKString()
        langName.substringBefore('-')
    } else {
        "en"
    }.also {
        nativeHeap.free(buffer.rawValue)
    }
}

const val license =
    "Copyright 2026 Napharcos. All rights reserved.\r\n\r\nPermission is granted to use this software for personal," +
            " non-commercial purposes only.\r\nAny form of copying, distribution, modification, reverse engineering," +
            " or creation of derivative works is strictly prohibited without the express written consent of the author, " +
            "unless otherwise explicitly stated.\r\n\r\nThe source code is made publicly available for transparency and " +
            "to promote trust, not for reuse or redistribution.\r\nViewing or running the source code does not imply " +
            "permission to reuse or incorporate it elsewhere.\r\n\r\nFor any commercial, educational, or redistributive use, " +
            "please contact the author directly for explicit permission.\r\n\r\n\r\nThis software and its components are " +
            "provided \"as is\", without any warranties, express or implied, including but not limited to merchantability " +
            "or fitness for a particular purpose.\r\nUse at your own risk.\r\n\r\nThis software is not affiliated with, " +
            "endorsed by, or supported by the original software developers or publishers, including but not limited to " +
            "Tencent, Activision, or their affiliates.\r\nAny use of associated configurations is done " +
            "at your own risk. The author assumes no responsibility for any resulting issues or damages."