import kotlin.to

private const val SIMPLE_END = """</KeyMapping>"""
private const val WHEEL_END = """</KeyMappingEx>"""

private val removableMPKeys = listOf(
    RemoveButtonData(
        id = xKey,
        description = "Mark",
        simpleStart = "<KeyMapping ItemName=\"\$xName\"",
        wheelStart = "<KeyMappingEx ItemName=\"\$xName\""
    )
)

private val removableBRKeys = listOf(
    RemoveButtonData(
        id = xKey,
        description = "Mark",
        simpleStart = "<KeyMapping ItemName=\"\$xName\"",
        wheelStart = "<KeyMappingEx ItemName=\"\$xName\""
    ),
    RemoveButtonData(
        id = key3,
        description = "Grenades",
        simpleStart = "<KeyMapping ItemName=\"$3Name\"",
        wheelStart = "<KeyMappingEx ItemName=\"$3Name\""
    )
)

private val removableDMZKeys = listOf(
    RemoveButtonData(
        id = xKey,
        description = "Mark",
        simpleStart = "<KeyMapping ItemName=\"\$xName\"",
        wheelStart = "<KeyMappingEx ItemName=\"\$xName\""
    ),
    RemoveButtonData(
        id = key3,
        description = "Grenades",
        simpleStart = "<KeyMapping ItemName=\"$3Name\"",
        wheelStart = "<KeyMappingEx ItemName=\"$3Name\""
    ),
    RemoveButtonData(
        id = hKey,
        description = "Armor",
        simpleStart = "<KeyMapping ItemName=\"\$hName\"",
        wheelStart = "<KeyMappingEx ItemName=\"\$hName\""
    )
)

private fun String.removeDuplicateButtons(mod: String): String {
    val (modList, removeList) = when (mod) {
        MP -> mpKeys to removableMPKeys
        BR -> brKeys to removableBRKeys
        DMZ -> dmzKeys to removableDMZKeys
        else -> return this
    }

    return removeList.fold(this) { acc, removeData ->
        val applyWheel = modList.firstOrNull { it.id == removeData.id }?.applyWheel

        if (applyWheel != false)
            removeButton(acc, removeData.simpleStart, SIMPLE_END)
        else removeButton(acc, removeData.wheelStart, WHEEL_END)
    }
}

private fun replaceFire(codmText: String): String {
    val defaultFire = """Point_X="0.854688" Point_Y="0.745833""""
    val newFire = """Point_X="0.060937" Point_Y="0.519444""""

    return codmText.replace(defaultFire, newFire)
}

private fun removeArmorButton(codmText: String): String {
    val armorButtonStart = """<KeyMapping ItemName="$4Name""""

    return removeButton(codmText, armorButtonStart, SIMPLE_END)
}

private fun removeBuyStationButton(codmText: String): String {
    val buyStationButtonStart = "<KeyMapping ItemName=\"\$nName\""

    return removeButton(codmText, buyStationButtonStart, SIMPLE_END)
}

private fun removeButton(codmText: String, start: String, end: String): String {
    val startIndex = codmText.indexOf(start)
    if (startIndex == -1) return codmText

    val endIndex = codmText.indexOf(end, startIndex)
    if (endIndex == -1) return codmText

    val fullEndIndex = endIndex + end.length

    return codmText.substring(0, startIndex) + codmText.substring(fullEndIndex)
}

fun String.replaceKeys(mod: String, keys: List<KeyData>): String {
    var newText = this

    newText = newText.removeDuplicateButtons(mod)

    if ((mod == BR || mod == DMZ) && keys.firstOrNull { it.id == separateBuyStation }?.currentValue == false)
        newText = removeBuyStationButton(newText)

    if (mod == BR && keys.firstOrNull { it.id == separateArmor }?.currentValue == false)
        newText = removeArmorButton(newText)

    keys.filter { !it.fakeKey }.forEach {
        newText = newText.replace(('$' + it.id.substringBefore(key) + name), it.currentKey!!)
        newText = newText.replace(('$' + it.id.substringBefore(key) + CODE), it.currentCode.toString())
    }

    if ((mod == MP || mod == BR) && keys.firstOrNull { it.id == replaceFire }?.currentValue == true)
        newText = replaceFire(newText)

    return newText
}

private data class RemoveButtonData(
    val id: String,
    val description: String,
    val simpleStart: String,
    val wheelStart: String
)