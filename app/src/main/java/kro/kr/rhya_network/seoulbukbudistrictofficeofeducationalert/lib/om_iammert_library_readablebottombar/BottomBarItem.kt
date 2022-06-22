package kro.kr.rhya_network.utaiteplayer.lib.com_iammert_library_readablebottombar

import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import kro.kr.rhya_network.seoulbukbudistrictofficeofeducationalert.lib.om_iammert_library_readablebottombar.ReadableBottomBar

data class BottomBarItem(
        val index: Int,
        val text: String,
        val textSize: Float,
        @ColorInt val textColor: Int,
        @ColorInt val iconColor: Int,
        val drawable: Drawable,
        val type: ReadableBottomBar.ItemType
)