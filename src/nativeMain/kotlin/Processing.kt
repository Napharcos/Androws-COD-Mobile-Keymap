import kotlinx.cinterop.*
import kotlinx.cinterop.alloc
import platform.windows.*
import kotlin.ranges.coerceIn

lateinit var currentStep: Steps

var activeEditId: Int? = null

@OptIn(ExperimentalForeignApi::class)
fun wndProc(hwnd: HWND?, msg: UINT, wParam: WPARAM, lParam: LPARAM): LRESULT {
    when (msg) {
        WM_SETCURSOR.toUInt() -> {
            val iBeamCursor = LoadCursorW(null, IDC_IBEAM)
            val hwndUnderCursor = wParam.toLong().toCPointer<HWND__>()!!

            if (keyMaps[selectedKeymap]?.keys?.any { it.hwnd == hwndUnderCursor && !it.fakeKey } == true) {
                SetCursor(iBeamCursor)
                return 1
            }
        }

        WM_COMMAND.toUInt() -> {
            val controlId = LOWORD(wParam.toInt())

            if (controlId == CANCEL_BUTTON_ID) {
                PostMessageW(hwnd, WM_CLOSE.toUInt(), 0u, 0L)
            } else currentStep.clicked(controlId)
        }

        WM_CTLCOLORSTATIC.toUInt() -> {
            whiteBrush?.let {
                val hdc = wParam.toLong().toCPointer<HDC__>()!!
                val childHwnd = lParam.toCPointer<HWND__>()!!

                SetBkMode(hdc, TRANSPARENT)

                val selectedHwnd = keyMaps[selectedKeymap]?.hwnd

                if (childHwnd == separator || childHwnd == vSeparator || childHwnd == selectedHwnd) {
                    grayBrush?.let { gray ->
                        return gray.rawValue.toLong()
                    }
                }

                return it.rawValue.toLong()
            }
        }

        WM_DRAWITEM.toUInt() -> {
            val drawItem = lParam.toCPointer<DRAWITEMSTRUCT>()!!
            val hdc = drawItem.pointed.hDC
            val rect = drawItem.pointed.rcItem
            val hwnd = drawItem.pointed.hwndItem
            val text = getWindowText(hwnd)
            val selectedHwnd = keyMaps[selectedKeymap]?.hwnd

            if (hwnd == selectedHwnd) {
                grayBrush?.let {
                    FillRect(hdc, rect.ptr, it)
                }
            }

            SetBkMode(hdc, TRANSPARENT)

            val flags = if (text.isSingleLine(hwnd))
                DT_CENTER.toUInt() or DT_VCENTER.toUInt() or DT_SINGLELINE.toUInt()
            else DT_CENTER.toUInt() or DT_WORDBREAK.toUInt() or DT_END_ELLIPSIS.toUInt()

            DrawTextW(hdc, text, -1, rect.ptr, flags)

            return 1
        }

        WM_CLOSE.toUInt() -> {
            val result = MessageBoxW(
                hwnd,
                strings[cancel_text],
                strings[confirmation],
                (MB_OKCANCEL or MB_ICONQUESTION).toUInt()
            )

            if (result == IDOK) {
                DestroyWindow(hwnd)
            }
            return 0
        }

        WM_DESTROY.toUInt() -> {
            PostQuitMessage(0)
            return 0
        }

        WM_MOUSEWHEEL.toUInt() -> {
            val delta = GET_WHEEL_DELTA_WPARAM(wParam.toLong())

            // 1 egérgörgő-lépés = 40 pixel.
            // Ha gyorsabb/lassabb görgetést szeretnél, ezt az értéket módosítsd.
            val scrollAmount =
                -(delta / WHEEL_DELTA) * 40

            memScoped {
                val startY = 10
                val textHeight = 35
                val editHeight = 20
                val radioHeight = 18

                // Egy normál elem által elfoglalt függőleges hely.
                val baseElementHeight = startY + textHeight

                val keys = keyMaps[selectedKeymap]?.keys

                // Teljes tartalommagasság pixelben.
                var contentHeight = 0

                keys?.forEach { k ->
                    contentHeight += baseElementHeight

                    if (k.wheelAvailable && k.wheelUiData != null) {
                        contentHeight += radioHeight
                    }
                }

                // Látható kliensmagasság pixelben.
                val rect = alloc<RECT>()
                GetClientRect(hwnd, rect.ptr)

                val viewportHeight = rect.bottom - rect.top

                // Ennyit lehet maximum legörgetni.
                val maxScrollPos =
                    (contentHeight - viewportHeight).coerceAtLeast(0)

                val si = alloc<SCROLLINFO>().apply {
                    cbSize = sizeOf<SCROLLINFO>().toUInt()
                    fMask = (
                            SIF_ALL.toUInt() or
                                    SIF_TRACKPOS.toUInt()
                            )
                    nMin = 0
                }

                GetScrollInfo(
                    hwnd,
                    SB_VERT,
                    si.ptr
                )

                val oldPos = si.nPos

                // A scroll pozíció most már PIXEL.
                si.nPos = (si.nPos + scrollAmount)
                    .coerceIn(0, maxScrollPos)

                // A scrollbar teljes tartalma pixelben.
                si.nMin = 0
                si.nMax = contentHeight.coerceAtLeast(0)

                // A scrollbar látható része pixelben.
                si.nPage = viewportHeight.toUInt()

                SetScrollInfo(
                    hwnd,
                    SB_VERT,
                    si.ptr,
                    TRUE
                )

                if (si.nPos != oldPos) {

                    // A tartalom aktuális Y pozíciója.
                    // Ez NEM tartalmazza a scrollt.
                    var contentY = startY

                    keys?.forEach { k ->

                        // Az elem eredeti pozíciója a teljes tartalmon belül.
                        val currentTextY = contentY

                        // Pixelalapú eltolás.
                        val newTextY =
                            currentTextY - si.nPos

                        val currentEditY =
                            currentTextY +
                                    (textHeight - editHeight) / 2 -
                                    si.nPos

                        SetWindowPos(
                            k.hwndText,
                            null,
                            0,
                            newTextY,
                            0,
                            0,
                            SWP_NOSIZE.toUInt() or
                                    SWP_NOZORDER.toUInt()
                        )

                        SetWindowPos(
                            k.hwnd,
                            null,
                            keymapTextWidth +
                                    if (k.fakeKey) 40 else 10,
                            currentEditY,
                            0,
                            0,
                            SWP_NOSIZE.toUInt() or
                                    SWP_NOZORDER.toUInt()
                        )

                        if (k.wheelAvailable && k.wheelUiData != null) {

                            SetWindowPos(
                                k.wheelUiData.radio1Hwnd,
                                null,
                                90,
                                newTextY + textHeight,
                                0,
                                0,
                                SWP_NOSIZE.toUInt() or
                                        SWP_NOZORDER.toUInt()
                            )

                            SetWindowPos(
                                k.wheelUiData.radio2Hwnd,
                                null,
                                210,
                                newTextY + textHeight,
                                0,
                                0,
                                SWP_NOSIZE.toUInt() or
                                        SWP_NOZORDER.toUInt()
                            )

                            // A wheel UI miatt ez az elem 18 pixellel
                            // több helyet foglal.
                            contentY += radioHeight
                        }

                        // Következő elem pozíciója.
                        contentY += baseElementHeight
                    }
                }
            }

            return 0
        }

        WM_VSCROLL.toUInt() -> {
            memScoped {
                val action = LOWORD(wParam.toInt())

                val si = alloc<SCROLLINFO>().apply {
                    cbSize = sizeOf<SCROLLINFO>().toUInt()
                    fMask = (SIF_ALL or SIF_TRACKPOS).toUInt()
                    nMin = 0
                }

                GetScrollInfo(hwnd, SB_VERT, si.ptr)

                val oldPos = si.nPos

                val startY = 10
                val textHeight = 35
                val editHeight = 20
                val radioHeight = 18

                // Egy normál elem teljes függőleges lépése:
                //
                // startY = 10
                // textHeight = 35
                //
                // tehát 45 pixel.
                val baseElementHeight = startY + textHeight

                val keys = keyMaps[selectedKeymap]?.keys

                // Teljes tartalom magassága pixelben.
                var contentHeight = 0

                keys?.forEach { k ->
                    contentHeight += baseElementHeight

                    if (k.wheelAvailable && k.wheelUiData != null) {
                        contentHeight += radioHeight
                    }
                }

                // A kliensablak tényleges látható magassága.
                val rect = alloc<RECT>()
                GetClientRect(hwnd, rect.ptr)

                val viewportHeight = rect.bottom - rect.top

                // Ennyire lehet maximum legörgetni.
                val maxScrollPos =
                    (contentHeight - viewportHeight).coerceAtLeast(0)

                when (action) {
                    SB_LINEUP -> {
                        si.nPos -= 20
                    }

                    SB_LINEDOWN -> {
                        si.nPos += 20
                    }

                    SB_PAGEUP -> {
                        si.nPos -= viewportHeight
                    }

                    SB_PAGEDOWN -> {
                        si.nPos += viewportHeight
                    }

                    SB_THUMBTRACK -> {
                        // Pixelalapú scrollbar esetén ezt használjuk,
                        // nem a HIWORD(wParam)-ot.
                        si.nPos = si.nTrackPos
                    }

                    SB_THUMBPOSITION -> {
                        si.nPos = si.nTrackPos
                    }
                }

                si.nPos = si.nPos.coerceIn(0, maxScrollPos)

                si.nMin = 0
                si.nMax = (contentHeight - 1).coerceAtLeast(0)
                si.nPage = viewportHeight.toUInt()

                SetScrollInfo(
                    hwnd,
                    SB_VERT,
                    si.ptr,
                    TRUE
                )

                if (si.nPos != oldPos) {

                    // Aktuális Y pozíció a tartalmon belül.
                    var contentY = startY

                    keys?.forEach { k ->

                        // Az elem jelenlegi, görgetés előtti pozíciója
                        // a teljes tartalomban.
                        val textY = contentY

                        // Ebből vonjuk le a pixelben mért scroll pozíciót.
                        val newTextY = textY - si.nPos

                        val currentEditY =
                            textY +
                                    (textHeight - editHeight) / 2 -
                                    si.nPos

                        SetWindowPos(
                            k.hwndText,
                            null,
                            0,
                            newTextY,
                            0,
                            0,
                            SWP_NOSIZE.toUInt() or
                                    SWP_NOZORDER.toUInt()
                        )

                        SetWindowPos(
                            k.hwnd,
                            null,
                            keymapTextWidth +
                                    if (k.fakeKey) 40 else 10,
                            currentEditY,
                            0,
                            0,
                            SWP_NOSIZE.toUInt() or
                                    SWP_NOZORDER.toUInt()
                        )

                        if (k.wheelAvailable && k.wheelUiData != null) {

                            SetWindowPos(
                                k.wheelUiData.radio1Hwnd,
                                null,
                                90,
                                newTextY + textHeight,
                                0,
                                0,
                                SWP_NOSIZE.toUInt() or
                                        SWP_NOZORDER.toUInt()
                            )

                            SetWindowPos(
                                k.wheelUiData.radio2Hwnd,
                                null,
                                210,
                                newTextY + textHeight,
                                0,
                                0,
                                SWP_NOSIZE.toUInt() or
                                        SWP_NOZORDER.toUInt()
                            )
                        }

                        // Következő elem kezdete.
                        contentY += baseElementHeight

                        // A wheel UI további helyet foglal.
                        if (k.wheelAvailable && k.wheelUiData != null) {
                            contentY += radioHeight
                        }
                    }
                }
            }

            return 0
        }

        WM_KEYDOWN.toUInt() -> {
            if (activeEditId != null) {
                val key = keyMaps[selectedKeymap]?.keys?.firstOrNull { it.hwndId == activeEditId }
                if (key != null) {
                    val code = wParam.toInt()

                    updateKeyCode(key.id, code)

                    val char = code.keyCodeToName()

                    char?.let {
                        val hwnd = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == activeEditId && !it.fakeKey }?.hwnd

                        HideCaret(hwnd)
                        DestroyCaret()

                        SetWindowTextW(key.hwnd, char)

                        updateKey(key.id, char)

                        activeEditId = null
                    }
                }
            }
            0
        }

        WM_SYSKEYDOWN.toUInt() -> {
            if (activeEditId != null) {
                val key = keyMaps[selectedKeymap]?.keys?.firstOrNull { it.hwndId == activeEditId && !it.fakeKey }
                if (key != null) {
                    val code = wParam.toInt()

                    updateKeyCode(key.id, code)

                    val char = code.keyCodeToName()

                    char?.let {
                        val hwnd = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == activeEditId && !it.fakeKey }?.hwnd

                        HideCaret(hwnd)
                        DestroyCaret()

                        SetWindowTextW(key.hwnd, char)

                        updateKey(key.id, char)

                        activeEditId = null
                    }
                }
                0
            }
        }

        WM_CHAR.toUInt() -> {
            if (activeEditId != null) {
                val key = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == activeEditId && !it.fakeKey }
                if (key != null) {
                    val char = wParam.toInt().toChar().toString()

                    val hwnd = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == activeEditId && !it.fakeKey }?.hwnd

                    HideCaret(hwnd)
                    DestroyCaret()

                    SetWindowTextW(key.hwnd, char.uppercase())

                    updateKey(key.id, char)

                    activeEditId = null
                }
            }
            0
        }
    }

    return DefWindowProcW(hwnd, msg, wParam, lParam)
}

fun start(step: Steps) {
    currentStep = step
    if (step == Steps.SELECT_LANGUAGE) initLanguage()
    if (step == Steps.CODM_KEYMAP) {
        initKeyMapUI()
        updateKeymapPageText()
    }
    updatePageVisibility()
}

@OptIn(ExperimentalForeignApi::class)
fun updatePageVisibility() {
    Steps.entries.forEach { step ->
        step.elements.forEach {
            ShowWindow(it, SW_HIDE)
        }
    }

    keyMaps.entries.forEach { (_, value) ->
        ShowWindow(value.hwnd, SW_HIDE)
        value.keys.forEach {
            ShowWindow(it.hwndText, SW_HIDE)
            ShowWindow(it.hwnd, SW_HIDE)
            ShowWindow(it.wheelUiData?.radio1Hwnd, SW_HIDE)
            ShowWindow(it.wheelUiData?.radio2Hwnd, SW_HIDE)
        }
    }

    currentStep.elements.forEach {
        ShowWindow(it, SW_SHOW)
    }

    if (currentStep == Steps.CODM_KEYMAP) {
        keyMaps.entries.forEach { (key, value) ->
            ShowWindow(value.hwnd, SW_SHOW)
            if (key == selectedKeymap) {
                value.keys.forEach {
                    ShowWindow(it.hwndText, SW_SHOW)
                    ShowWindow(it.hwnd, SW_SHOW)
                    ShowWindow(it.wheelUiData?.radio1Hwnd, SW_SHOW)
                    ShowWindow(it.wheelUiData?.radio2Hwnd, SW_SHOW)

                    when (selectedKeymap) {
                        4002 -> {
                            if (it.id == key4) {
                                val show = value.keys.firstOrNull { e -> e.id == separateArmor }?.currentValue

                                if (show == false) {
                                    ShowWindow(it.hwndText, SW_HIDE)
                                    ShowWindow(it.hwnd, SW_HIDE)
                                }
                            } else if (it.id == nKey) {
                                val show = value.keys.firstOrNull { e -> e.id == separateBuyStation }?.currentValue

                                if (show == false) {
                                    ShowWindow(it.hwndText, SW_HIDE)
                                    ShowWindow(it.hwnd, SW_HIDE)
                                }
                            }
                        }
                        4003 -> {
                            if (it.id == nKey) {
                                val show = value.keys.firstOrNull { e -> e.id == separateBuyStation }?.currentValue

                                if (show == false) {
                                    ShowWindow(it.hwndText, SW_HIDE)
                                    ShowWindow(it.hwnd, SW_HIDE)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun getWindowText(hwnd: HWND?): String {
    memScoped {
        val size = 256
        val buffer = allocArray<UShortVar>(size)
        GetWindowTextW(hwnd, buffer, size)

        return buffer.toKString()
    }
}

const val keymapTextWidth = 250

@OptIn(ExperimentalForeignApi::class)
fun initKeyMapUI() {
    val x = 0
    val startY = 10
    val buttonHeight = 50
    val textHeight = 35
    val editHeight = 20
    val radioHeight = 18

    keyMaps.entries.forEach { (key, value) ->
        val index = key - 4001
        val y = 0 + buttonHeight * index

        val static = CreateWindowExW(
            0u, W_STATIC, value.name,
            WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or SS_NOTIFY.toUInt() or SS_CENTER.toUInt() or SS_OWNERDRAW.toUInt(),
            0, y, 120, buttonHeight,
            hwndMain, key.toLong().toCPointer(), instance, null
        )

        value.hwnd = static

        var createdWheels = 0

        value.keys.withIndex().forEach { (i, k) ->
            val y = startY * (i + 1) + textHeight * i + radioHeight * createdWheels

            val static = CreateWindowExW(
                0u, W_STATIC, null,
                WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or SS_NOPREFIX.toUInt() or SS_OWNERDRAW.toUInt(),
                x, y, keymapTextWidth, textHeight,
                container, null, instance, null
            )

            k.hwndText = static

            val edit = if (!k.fakeKey)
                CreateWindowExW(
                    0u, W_STATIC, k.currentKey,
                    WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or WS_BORDER.toUInt() or SS_CENTER.toUInt() or SS_NOTIFY.toUInt(),
                    x + keymapTextWidth + 10, y + (textHeight - editHeight) / 2, 80, editHeight,
                    container, k.hwndId.toLong().toCPointer(), instance, null
                )
            else {
                val hwnd = CreateWindowExW(
                    0u, W_BUTTON, null,
                    WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or BS_AUTOCHECKBOX.toUInt(),
                    x + keymapTextWidth + 40, y + (textHeight - editHeight) / 2, 80, editHeight,
                    container, k.hwndId.toLong().toCPointer(), instance, null
                )

                SendMessage?.invoke(hwnd, BM_SETCHECK.toUInt(), (if (k.currentValue == true) BST_CHECKED else BST_UNCHECKED).toULong(), 0)

                hwnd
            }

            k.hwnd = edit

            if (k.wheelAvailable && k.wheelUiData != null) {
                val radio1 = CreateWindowExW(
                    0u, W_BUTTON, null,
                    WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or BS_AUTORADIOBUTTON.toUInt() or WS_GROUP.toUInt(),
                    90, y + textHeight,
                    120, radioHeight,
                    container, k.wheelUiData.radio1Id.toLong().toCPointer(), instance, null
                )

                val radio2 = CreateWindowExW(
                    0u, W_BUTTON, null,
                    WS_CHILD.toUInt() or WS_VISIBLE.toUInt() or BS_AUTORADIOBUTTON.toUInt(),
                    210, y + textHeight,
                    120, radioHeight,
                    container, k.wheelUiData.radio2Id.toLong().toCPointer(), instance, null
                )

                k.wheelUiData.radio1Hwnd = radio1
                k.wheelUiData.radio2Hwnd = radio2

                SendMessage?.invoke(
                    radio1,
                    BM_SETCHECK.toUInt(),
                    (if (k.applyWheel) BST_CHECKED else BST_UNCHECKED).toULong(),
                    0
                )

                SendMessage?.invoke(
                    radio2,
                    BM_SETCHECK.toUInt(),
                    (if (k.applyWheel) BST_UNCHECKED else BST_CHECKED).toULong(),
                    0
                )

                createdWheels++
            }
        }
    }

    updateScrollSize()
}

@OptIn(ExperimentalForeignApi::class)
fun updateScrollSize() {
    memScoped {
        val startY = 10
        val textHeight = 35
        val radioHeight = 18

        // Egy normál elem függőleges helyigénye.
        val baseElementHeight = startY + textHeight

        val keys = keyMaps[selectedKeymap]?.keys

        // Teljes tartalommagasság pixelben.
        var contentHeight = 0

        keys?.forEach { k ->
            contentHeight += baseElementHeight

            if (k.wheelAvailable && k.wheelUiData != null) {
                contentHeight += radioHeight
            }
        }

        // A ténylegesen látható terület magassága.
        val rect = alloc<RECT>()
        GetClientRect(container, rect.ptr)

        val viewportHeight = rect.bottom - rect.top

        // Maximális scroll pozíció pixelben.
        val maxScrollPos =
            (contentHeight - viewportHeight).coerceAtLeast(0)

        val si = alloc<SCROLLINFO>().apply {
            cbSize = sizeOf<SCROLLINFO>().toUInt()
            fMask = SIF_ALL.toUInt()
            nMin = 0
        }

        GetScrollInfo(container, SB_VERT, si.ptr)

        si.nPos = si.nPos.coerceIn(0, maxScrollPos)

        si.nMax = contentHeight.coerceAtLeast(0)

        // A scrollbar "ablaka" pixelben akkora,
        // mint a ténylegesen látható kliensterület.
        si.nPage = viewportHeight.toUInt()

        SetScrollInfo(
            container,
            SB_VERT,
            si.ptr,
            TRUE
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
fun scrollToTop() {
    memScoped {
        val si = alloc<SCROLLINFO>().apply {
            cbSize = sizeOf<SCROLLINFO>().toUInt()
            fMask = SIF_POS.toUInt()
            nPos = 0
        }

        SetScrollInfo(
            container,
            SB_VERT,
            si.ptr,
            TRUE
        )

        // Minden elem visszahelyezése az eredeti pozíciójára.
        var contentY = 10

        val startY = 10
        val textHeight = 35
        val editHeight = 20
        val radioHeight = 18

        keyMaps[selectedKeymap]?.keys?.forEach { k ->

            SetWindowPos(
                k.hwndText,
                null,
                0,
                contentY,
                0,
                0,
                SWP_NOSIZE.toUInt() or SWP_NOZORDER.toUInt()
            )

            SetWindowPos(
                k.hwnd,
                null,
                keymapTextWidth + if (k.fakeKey) 40 else 10,
                contentY + (textHeight - editHeight) / 2,
                0,
                0,
                SWP_NOSIZE.toUInt() or SWP_NOZORDER.toUInt()
            )

            if (k.wheelAvailable && k.wheelUiData != null) {
                SetWindowPos(
                    k.wheelUiData.radio1Hwnd,
                    null,
                    90,
                    contentY + textHeight,
                    0,
                    0,
                    SWP_NOSIZE.toUInt() or SWP_NOZORDER.toUInt()
                )

                SetWindowPos(
                    k.wheelUiData.radio2Hwnd,
                    null,
                    210,
                    contentY + textHeight,
                    0,
                    0,
                    SWP_NOSIZE.toUInt() or SWP_NOZORDER.toUInt()
                )

                contentY += radioHeight
            }

            contentY += startY + textHeight
        }
    }

    InvalidateRect(container, null, TRUE)
}

@OptIn(ExperimentalForeignApi::class)
fun keymapPageCommand(id: Int): Int {
    return when (id) {
        NEXT_BUTTON_ID -> {
            applyKeymap()
            0
        }

        BACK_BUTTON_ID -> {
            updateLicensePageText()
            start(Steps.LICENSE)
            0
        }

        SKIP_BUTTON_ID -> {
            ConsoleManager.stopEmulator()
            DatabaseManager.deleteTriggers()
            ConsoleManager.startCODM()
            0
        }

        in 4001..4010 -> {
            if (keyMaps[id] != null) {
                selectedKeymap = id
                updateKeymapPageText()
                updatePageVisibility()
                updateScrollSize()
                scrollToTop()
            }
            0
        }

        in 3000..3100 -> {
            val element = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == id || it.wheelUiData?.radio1Id == id || it.wheelUiData?.radio2Id == id }
            val hwnd = element?.hwnd

            if (element?.fakeKey == true) {
                val newValue = element.currentValue == false

                SendMessage?.invoke(hwnd, BM_SETCHECK.toUInt(), (if (newValue) BST_CHECKED else BST_UNCHECKED).toULong(), 0)

                keyMaps[selectedKeymap]?.keys?.find { it.hwndId == id }?.currentValue = newValue

                val mode = when (selectedKeymap) {
                    4001 -> MP
                    4002 -> BR
                    else -> DMZ
                }

                reg?.let { r -> setValue(r, mode + element.id, newValue.toString()) }

                updatePageVisibility()
            } else if (element?.hwndId != id) {
                val apply = element?.wheelUiData?.radio1Id == id

                keyMaps[selectedKeymap]?.keys?.find { it.wheelUiData?.radio1Id == id || it.wheelUiData?.radio2Id == id }?.applyWheel = apply

                val mod = when (selectedKeymap) {
                    4001 -> MP
                    4002 -> BR
                    else -> DMZ
                }

                reg?.let { r -> setValue(r, "${mod}_${element?.id}_wheel", apply.toString()) }
            } else {
                val oldHwnd = keyMaps[selectedKeymap]?.keys?.find { it.hwndId == activeEditId }?.hwnd

                SetFocus(hwnd)
                HideCaret(oldHwnd)
                DestroyCaret()

                CreateCaret(hwnd, null, 1, 14)
                SetCaretPos(38, 2)

                activeEditId = id

                SetWindowTextW(hwnd, null)
                ShowCaret(hwnd)

                SetFocus(hwndMain)
            }

            0
        }

        else -> 0
    }
}

@OptIn(ExperimentalForeignApi::class)
fun languagePageCommand(id: Int): Int {
    return when (id) {
        EN_LANG_RADIO_ID -> {
            strings = en
            updateLangPageText()
            updatePageVisibility()
            reg?.let { setValue(it, lang, "en") }
            0
        }
        HU_LANG_RADIO_ID -> {
            strings = hu
            updateLangPageText()
            updatePageVisibility()
            reg?.let { setValue(it, lang, "hu") }
            0
        }
        NEXT_BUTTON_ID -> {
            updateLicensePageText()
            start(Steps.LICENSE)
            0
        }
        else -> 0
    }
}

fun licensePageCommand(id: Int): Int {
    return when (id) {
        BACK_BUTTON_ID -> {
            updateLangPageText()
            start(Steps.SELECT_LANGUAGE)
            0
        }
        NEXT_BUTTON_ID -> {
            updateKeymapPageText()
            start(Steps.CODM_KEYMAP)
            0
        }
        else -> 0
    }
}

fun updateKeymapPageText() {
    keyMaps[selectedKeymap]?.keys?.forEach {
        SetWindowTextW(it.hwndText, strings[it.text].reduce(it.hwndText))
        if (it.wheelAvailable && it.wheelUiData != null) {
            SetWindowTextW(it.wheelUiData.radio1Hwnd, strings[rotary_key])
            SetWindowTextW(it.wheelUiData.radio2Hwnd, strings[simple_key])
        }
    }
    SetWindowTextW(nextButton, strings[apply])
    SetWindowTextW(skipButton, strings[skip])
}

fun updateLicensePageText() {
    SetWindowTextW(title, strings[title_2])
    SetWindowTextW(nextButton, strings[accept])
    SetWindowTextW(cancelButton, strings[cancel])
    SetWindowTextW(backButton, strings[back])
    SetWindowTextW(text2, license)
}

fun updateLangPageText() {
    SetWindowTextW(title, strings[welcome])
    SetWindowTextW(text1_1, strings[text_1_1])
    SetWindowTextW(radio1, strings[lang_en])
    SetWindowTextW(radio2, strings[lang_hu])
    SetWindowTextW(nextButton, strings[next])
    SetWindowTextW(cancelButton, strings[cancel])
}