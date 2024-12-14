package com.skylerclagg.adc_hub

import android.content.Context
import android.os.Vibrator

fun Context.getVibrator() = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator