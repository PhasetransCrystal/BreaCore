package com.phasetranscrystal.breacore.data.translation

import com.phasetranscrystal.breacore.api.lang.initialize
import com.phasetranscrystal.breacore.api.lang.toLiteralSupplier
import com.phasetranscrystal.breacore.api.lang.translatedTo
import com.phasetranscrystal.breacore.api.misc.AutoInitialize

object ComponentSlang : AutoInitialize<ComponentSlang>() {
    // ****** 量词 ****** //
    val Infinite = ("无限" translatedTo "Infinite").initialize()

    // ****** 符号 ****** //
    val right = "✔".toLiteralSupplier()
    val wrong = "✘".toLiteralSupplier()
}
