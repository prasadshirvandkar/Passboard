package com.midsizemango.passboard.Utils

import java.util.*
/**
 * Created by ABC on 12/22/2017.
 */
class ColorGenerator (private val mColors: List<Int>) {
    private val mRandom: Random = Random(System.currentTimeMillis())

    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[Math.abs(key.hashCode()) % mColors.size]
    }

    companion object {

        var DEFAULT: ColorGenerator

        var MATERIAL: ColorGenerator

        init {
            DEFAULT = create(Arrays.asList(
                    -0xe9c9c,
                    -0xa7aa7,
                    -0x65bc2,
                    -0x1b39d2,
                    -0x98408c,
                    -0xa65d42,
                    -0xdf6c33,
                    -0x529d59,
                    -0x7fa87f
            ))
            MATERIAL = create(Arrays.asList(
                    -0xff432c,
                    -0xff6978,
                    -0x86aab8,
                    -0xbaa59c,
                    -0x1a848e,
                    -0x72dcbb,
                    -0x9f8275,
                    -0x8fbd,
                    -0xbf7f,
                    -0x16e19d,
                    -0xfc8cc1,
                    -0xa41db,
                    -0xca600e,
                    -0xa98804,
                    -0x2bb19311,
                    -0xd9cdc8,
                    -0xff6978,
                    -0xfd651c,
                    -0xff61d7,
                    -0xcc4987
            ))
        }

        private fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}