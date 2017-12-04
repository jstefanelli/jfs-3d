package com.jstefanelli.jfs3d.engine.geometry

import com.jstefanelli.jfs3d.engine.Mathf
import org.joml.Vector3f

class rect(var q: Float, var m: Float){

    constructor(first: Vector3f, second: Vector3f) : this(0f, 0f) {
        if(first.x == second.x){
            this.m = Float.POSITIVE_INFINITY
            this.q = first.x
            return
        }

        if(first.z == second.z){
            this.m = 0f
            this.q = first.z
            return
        }

        val yDen = second.z - first.z
        val xDen = second.x - first.x
        val m = yDen / xDen
        val q = ((-first.x / xDen) * yDen) + first.z

        this.q = q
        this.m = m
    }

    fun getX(y: Float): Float{
        if(m != 0f)
            if(m != Float.POSITIVE_INFINITY)
                return (y - q) / m
            else
                return q
        else
            if(y != q){
                return Float.NEGATIVE_INFINITY
            }else{
                return Float.POSITIVE_INFINITY
            }

    }

    fun getY(x: Float) : Float{
        if(m == Float.POSITIVE_INFINITY){
            if(x == q){
                return Float.POSITIVE_INFINITY
            }else{
                return Float.NEGATIVE_INFINITY
            }
        }

        return (x * m ) + q
    }

    fun incidenceAngle(other: rect): Float{
        if(this.m == Float.POSITIVE_INFINITY ){
            return if(other.m == Float.POSITIVE_INFINITY){
                0f
            }else{
                Mathf.atan(-other.m)
            }
        }else{
            return if(other.m == Float.POSITIVE_INFINITY){
                Mathf.atan(-m)
            }else{
                Mathf.atan(m - other.m)
            }
        }
    }

    override fun toString(): String {
        if(m == Float.POSITIVE_INFINITY)
            return "x = $q"
        return "y = " +  m + "x + " + q
    }
}