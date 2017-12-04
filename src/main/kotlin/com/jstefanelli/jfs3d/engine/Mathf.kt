package com.jstefanelli.jfs3d.engine

import org.joml.Vector3f

class Mathf {
    companion object {
        val Pif = Math.PI.toFloat()
        private val Ef = Math.E.toFloat()

        fun toRadians(angle: Float) : Float {
            return (angle * Pif) / 180.0f
        }

        fun atan(angle: Float) : Float{
            return Math.atan(angle.toDouble()).toFloat()
        }

        fun atan2(v0: Float, v1: Float): Float{
            return Math.atan2(v0.toDouble(), v1.toDouble()).toFloat()
        }

        fun toGrad(rad: Float) : Float{
            return (180f * rad) / Pif
        }

        fun concord(first: Vector3f, second: Vector3f) : Boolean{
            return first.x * second.x >= 0 && first.z * second.z >= 0
        }

        fun angleFromOrigin(point: Vector3f): Float{
            val vector1 = Vector3f(0f, 0f, -1f)

            return Mathf.atan2(-point.z, point.x) - Mathf.atan2(-vector1.z, vector1.x)
        }
    }
}