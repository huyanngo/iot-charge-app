package com.example.gst.trainingcourse.iotcharger.model

import java.io.Serializable

data class Device(
    var name: String? = null,
    var status: Int = 0,
    var serverPass: String? = null,
    var clientPass: String? = null,
    var dateOn: String? = null
): Serializable