package com.kurnivan_ny.foodit.view.utils

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import java.nio.FloatBuffer

fun konversiInput(sJenisKelamin: String, sUmur: String, sTinggi: String, sBerat: String): FloatBuffer {
    if (sJenisKelamin.equals("Laki-laki")) {
        val floatBufferInputs = FloatBuffer.wrap(
            floatArrayOf(
                sUmur.toFloat(),
                sBerat.toFloat(),
                sTinggi.toFloat(),
                1.0f,
                0.0f
            )
        )
        return floatBufferInputs
    } else {
        val floatBufferInputs = FloatBuffer.wrap(
            floatArrayOf(
                sUmur.toFloat(),
                sBerat.toFloat(),
                sTinggi.toFloat(),
                0.0f,
                1.0f
            )
        )
        return floatBufferInputs
    }
}

fun predictRegressi(modelBytes: ByteArray, input: FloatBuffer): Float {

    val ortEnvironment = OrtEnvironment.getEnvironment()
    val ortSession = ortEnvironment.createSession(modelBytes)
    val output = runPrediction(input, ortSession, ortEnvironment)

    val TotalEnergi:Float = (output.toString() + "F").replace(",",".").toFloat()

    return TotalEnergi
}

fun runPrediction(
    floatBufferInputs: FloatBuffer?,
    ortSession: OrtSession,
    ortEnvironment: OrtEnvironment?
): Any {
    val inputName = ortSession.inputNames?.iterator()?.next()
    val inputTensor =
        OnnxTensor.createTensor(ortEnvironment, floatBufferInputs, longArrayOf(1, 5))
    val results = ortSession.run(mapOf(inputName to inputTensor))
    val output = results[0].value as Array<FloatArray>
    return output[0][0]
}